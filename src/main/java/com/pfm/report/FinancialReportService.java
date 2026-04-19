package com.pfm.report;

import com.pfm.analytics.AccountAnalyticsService;
import com.pfm.analytics.BudgetAnalyticsService;
import com.pfm.analytics.TransactionAnalyticsService;
import com.pfm.model.Transaction;
import com.pfm.report.dto.CategorySpendingSummaryDTO;
import com.pfm.report.dto.MonthlySummaryDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class FinancialReportService {

    private final TransactionAnalyticsService transactionAnalyticsService;
    private final AccountAnalyticsService accountAnalyticsService;
    private final BudgetAnalyticsService budgetAnalyticsService;

    public FinancialReportService(
            TransactionAnalyticsService transactionAnalyticsService,
            AccountAnalyticsService accountAnalyticsService,
            BudgetAnalyticsService budgetAnalyticsService
    ) {
        this.transactionAnalyticsService = transactionAnalyticsService;
        this.accountAnalyticsService = accountAnalyticsService;
        this.budgetAnalyticsService = budgetAnalyticsService;
    }

    public MonthlySummaryDTO buildMonthlySummary(YearMonth yearMonth) {
        LocalDate fromDate = yearMonth.atDay(1);
        LocalDate toDate = yearMonth.atEndOfMonth();
        List<Transaction> monthlyTransactions = transactionAnalyticsService.getTransactionsForRange(fromDate, toDate);

        BigDecimal totalCredits = monthlyTransactions.stream()
                .filter(Transaction.Credit.class::isInstance)
                .map(tx -> tx.money().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebits = monthlyTransactions.stream()
                .filter(Transaction.Debit.class::isInstance)
                .map(tx -> tx.money().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netCashFlow = totalCredits.subtract(totalDebits);

        return new MonthlySummaryDTO(
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                totalCredits,
                totalDebits,
                netCashFlow,
                monthlyTransactions.size(),
                transactionAnalyticsService.getCategorySpending(fromDate, toDate).stream()
                        .map(category -> new CategorySpendingSummaryDTO(
                                category.category(),
                                category.totalAmount(),
                                category.transactionCount()
                        ))
                        .toList(),
                accountAnalyticsService.getSortedByBalanceDesc(),
                budgetAnalyticsService.getAllStatuses()
        );
    }

    public String buildMonthlySummaryText(YearMonth yearMonth) {
        MonthlySummaryDTO summary = buildMonthlySummary(yearMonth);
        return """
                Monthly Summary
                Year: %d
                Month: %d
                Total Credits: %s
                Total Debits: %s
                Net Cash Flow: %s
                Transaction Count: %d
                """.formatted(
                summary.year(),
                summary.month(),
                summary.totalCredits(),
                summary.totalDebits(),
                summary.netCashFlow(),
                summary.transactionCount()
        );
    }
}
