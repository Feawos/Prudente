package com.pfm.analytics;

import com.pfm.analytics.dto.BudgetStatusDTO;
import com.pfm.domain.Budget;
import com.pfm.service.BudgetService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetAnalyticsService {

    private final BudgetService budgetService;

    public BudgetAnalyticsService(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    public List<BudgetStatusDTO> getAllStatuses() {
        return budgetService.getAll().stream()
                .map(this::toStatus)
                .toList();
    }

    public Optional<BudgetStatusDTO> getStatusByCategory(String category) {
        return budgetService.getAll().stream()
                .filter(budget -> budget.category().name().equalsIgnoreCase(category))
                .findFirst()
                .map(this::toStatus);
    }

    public List<BudgetStatusDTO> getExceededBudgets() {
        return budgetService.getAll().stream()
                .filter(Budget::isExceeded)
                .map(this::toStatus)
                .toList();
    }

    public List<BudgetStatusDTO> getSortedByRemainingAmountDesc() {
        return budgetService.getAll().stream()
                .map(this::toStatus)
                .sorted(Comparator.comparing(BudgetStatusDTO::remainingAmount).reversed())
                .toList();
    }

    private BudgetStatusDTO toStatus(Budget budget) {
        BigDecimal remainingAmount = budget.limit().amount().subtract(budget.spent().amount());
        return new BudgetStatusDTO(
                budget.id(),
                budget.category().name(),
                budget.limit().amount(),
                budget.spent().amount(),
                remainingAmount,
                budget.isExceeded(),
                budget.startDate(),
                budget.endDate()
        );
    }
}
