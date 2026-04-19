package com.pfm.analytics;

import com.pfm.analytics.dto.DailyTotalDTO;
import com.pfm.analytics.dto.CategorySpendingDTO;
import com.pfm.analytics.dto.SortDirection;
import com.pfm.analytics.dto.SortField;
import com.pfm.analytics.dto.TransactionStatsDTO;
import com.pfm.analytics.dto.TransactionTypeView;
import com.pfm.analytics.dto.TransactionViewDTO;
import com.pfm.model.Category;
import com.pfm.model.Currency;
import com.pfm.model.Transaction;
import com.pfm.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class TransactionAnalyticsService {

    private final TransactionService transactionService;

    public TransactionAnalyticsService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<TransactionViewDTO> getAll() {
        Consumer<Transaction> sink = tx -> { };
        return transactionService.all().stream()
                .peek(sink)
                .map(TransactionViewMapper::toView)
                .toList();
    }

    public List<TransactionViewDTO> getSorted(SortField sortField, SortDirection direction) {
        return transactionService.all().stream()
                .sorted(buildComparator(sortField, direction))
                .map(TransactionViewMapper::toView)
                .toList();
    }

    public List<TransactionViewDTO> getFiltered(
            String type,
            String currency,
            String category,
            LocalDate fromDate,
            LocalDate toDate,
            Integer limit,
            SortField sortField,
            SortDirection direction
    ) {
        Supplier<LocalDate> defaultFrom = () -> LocalDate.MIN;
        Supplier<LocalDate> defaultTo = () -> LocalDate.MAX;
        LocalDate effectiveFrom = fromDate != null ? fromDate : defaultFrom.get();
        LocalDate effectiveTo = toDate != null ? toDate : defaultTo.get();
        int effectiveLimit = limit != null && limit > 0 ? limit : Integer.MAX_VALUE;

        Predicate<Transaction> dateFilter = tx ->
                !tx.date().isBefore(effectiveFrom) && !tx.date().isAfter(effectiveTo);

        Predicate<Transaction> typeFilter = tx -> type == null
                || TransactionViewMapper.detectType(tx).name().equalsIgnoreCase(type);

        Predicate<Transaction> currencyFilter = tx -> currency == null
                || tx.money().currency().name().equalsIgnoreCase(currency);

        Predicate<Transaction> categoryFilter = tx -> category == null
                || tx.category().map(Enum::name).orElse("").equalsIgnoreCase(category);

        return transactionService.all().stream()
                .filter(dateFilter.and(typeFilter).and(currencyFilter).and(categoryFilter))
                .sorted(buildComparator(sortField, direction))
                .limit(effectiveLimit)
                .map(TransactionViewMapper::toView)
                .toList();
    }

    public Optional<TransactionViewDTO> findFirstByCurrency(Currency currency) {
        return transactionService.all().stream()
                .filter(tx -> tx.money().currency() == currency)
                .findFirst()
                .map(TransactionViewMapper::toView);
    }

    public Optional<TransactionViewDTO> findAnyTransfer() {
        return transactionService.all().stream()
                .filter(tx -> TransactionViewMapper.detectType(tx) == TransactionTypeView.TRANSFER)
                .findAny()
                .map(TransactionViewMapper::toView);
    }

    public TransactionStatsDTO getStats() {
        List<Transaction> transactions = transactionService.all();
        Function<Transaction, BigDecimal> amountExtractor = tx -> tx.money().amount();

        long totalCount = transactions.stream().count();
        long debitCount = countByType(TransactionTypeView.DEBIT.name());
        long creditCount = countByType(TransactionTypeView.CREDIT.name());
        long transferCount = countByType(TransactionTypeView.TRANSFER.name());

        BigDecimal minAmount = transactions.stream()
                .map(amountExtractor)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxAmount = transactions.stream()
                .map(amountExtractor)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal averageAmount = totalCount == 0
                ? BigDecimal.ZERO
                : transactions.stream()
                .map(amountExtractor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);

        return new TransactionStatsDTO(
                totalCount,
                debitCount,
                creditCount,
                transferCount,
                minAmount,
                maxAmount,
                averageAmount
        );
    }

    public List<DailyTotalDTO> getDailyTotals(LocalDate fromDate, LocalDate toDate) {
        return transactionService.all().stream()
                .filter(tx -> !tx.date().isBefore(fromDate) && !tx.date().isAfter(toDate))
                .collect(Collectors.groupingBy(
                        Transaction::date,
                        Collectors.mapping(tx -> tx.money().amount(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new DailyTotalDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Map<String, List<TransactionViewDTO>> groupByType() {
        return transactionService.all().stream()
                .collect(Collectors.groupingBy(
                        tx -> TransactionViewMapper.detectType(tx).name(),
                        Collectors.mapping(TransactionViewMapper::toView, Collectors.toList())
                ));
    }

    public Map<String, List<TransactionViewDTO>> groupByCurrency() {
        return transactionService.all().stream()
                .collect(Collectors.groupingBy(
                        tx -> tx.money().currency().name(),
                        Collectors.mapping(TransactionViewMapper::toView, Collectors.toList())
                ));
    }

    public Map<String, List<TransactionViewDTO>> groupByCategory() {
        return transactionService.all().stream()
                .filter(tx -> tx.category().isPresent())
                .collect(Collectors.groupingBy(
                        tx -> tx.category().map(Enum::name).orElse(Category.OTHER.name()),
                        Collectors.mapping(TransactionViewMapper::toView, Collectors.toList())
                ));
    }

    public Map<Boolean, List<TransactionViewDTO>> partitionByCredit() {
        return transactionService.all().stream()
                .collect(Collectors.partitioningBy(
                        tx -> TransactionViewMapper.detectType(tx) == TransactionTypeView.CREDIT,
                        Collectors.mapping(TransactionViewMapper::toView, Collectors.toList())
                ));
    }

    public Map<LocalDate, TransactionViewDTO> toDateIndexedMap() {
        return transactionService.all().stream()
                .collect(Collectors.toMap(
                        Transaction::date,
                        TransactionViewMapper::toView,
                        (first, second) -> first
                ));
    }

    public boolean allMatchCurrency(String currency) {
        return transactionService.all().stream()
                .allMatch(tx -> tx.money().currency().name().equalsIgnoreCase(currency));
    }

    public boolean anyMatchTransfer() {
        return transactionService.all().stream()
                .anyMatch(tx -> TransactionViewMapper.detectType(tx) == TransactionTypeView.TRANSFER);
    }

    public boolean noneMatchFutureDated() {
        LocalDate today = LocalDate.now();
        return transactionService.all().stream()
                .noneMatch(tx -> tx.date().isAfter(today));
    }

    public long countByType(String type) {
        return transactionService.all().stream()
                .filter(tx -> TransactionViewMapper.detectType(tx).name().equalsIgnoreCase(type))
                .count();
    }

    public List<Transaction> getTransactionsForRange(LocalDate fromDate, LocalDate toDate) {
        return transactionService.all().stream()
                .filter(tx -> !tx.date().isBefore(fromDate) && !tx.date().isAfter(toDate))
                .toList();
    }

    public List<CategorySpendingDTO> getCategorySpending(LocalDate fromDate, LocalDate toDate) {
        return transactionService.all().stream()
                .filter(Transaction.Debit.class::isInstance)
                .filter(tx -> !tx.date().isBefore(fromDate) && !tx.date().isAfter(toDate))
                .filter(tx -> tx.category().isPresent())
                .collect(Collectors.groupingBy(
                        tx -> tx.category().map(Enum::name).orElse(Category.OTHER.name())
                ))
                .entrySet().stream()
                .map(entry -> new CategorySpendingDTO(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(tx -> tx.money().amount())
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue().size()
                ))
                .sorted(Comparator.comparing(CategorySpendingDTO::totalAmount).reversed())
                .toList();
    }

    public Optional<CategorySpendingDTO> getTopSpendingCategory(LocalDate fromDate, LocalDate toDate) {
        return getCategorySpending(fromDate, toDate).stream().findFirst();
    }

    private Comparator<Transaction> buildComparator(SortField sortField, SortDirection direction) {
        SortField effectiveField = sortField != null ? sortField : SortField.DATE;
        SortDirection effectiveDirection = direction != null ? direction : SortDirection.ASC;

        Comparator<Transaction> comparator = switch (effectiveField) {
            case DATE -> Comparator.comparing(Transaction::date);
            case AMOUNT -> Comparator.comparing(tx -> tx.money().amount());
        };

        return effectiveDirection == SortDirection.DESC ? comparator.reversed() : comparator;
    }
}
