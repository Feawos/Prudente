package com.pfm.web;

import com.pfm.analytics.AccountAnalyticsService;
import com.pfm.analytics.BudgetAnalyticsService;
import com.pfm.analytics.TransactionAnalyticsService;
import com.pfm.analytics.dto.AccountBalanceDTO;
import com.pfm.analytics.dto.BudgetStatusDTO;
import com.pfm.analytics.dto.CategorySpendingDTO;
import com.pfm.analytics.dto.DailyTotalDTO;
import com.pfm.analytics.dto.SortDirection;
import com.pfm.analytics.dto.SortField;
import com.pfm.analytics.dto.TransactionStatsDTO;
import com.pfm.analytics.dto.TransactionViewDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final TransactionAnalyticsService transactionAnalyticsService;
    private final BudgetAnalyticsService budgetAnalyticsService;
    private final AccountAnalyticsService accountAnalyticsService;

    public AnalyticsController(
            TransactionAnalyticsService transactionAnalyticsService,
            BudgetAnalyticsService budgetAnalyticsService,
            AccountAnalyticsService accountAnalyticsService
    ) {
        this.transactionAnalyticsService = transactionAnalyticsService;
        this.budgetAnalyticsService = budgetAnalyticsService;
        this.accountAnalyticsService = accountAnalyticsService;
    }

    @GetMapping("/transactions")
    public List<TransactionViewDTO> transactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) SortField sortBy,
            @RequestParam(required = false) SortDirection direction
    ) {
        return transactionAnalyticsService.getFiltered(type, currency, category, fromDate, toDate, limit, sortBy, direction);
    }

    @GetMapping("/transactions/stats")
    public TransactionStatsDTO stats() {
        return transactionAnalyticsService.getStats();
    }

    @GetMapping("/transactions/daily-totals")
    public List<DailyTotalDTO> dailyTotals(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return transactionAnalyticsService.getDailyTotals(fromDate, toDate);
    }

    @GetMapping("/transactions/category-spending")
    public List<CategorySpendingDTO> categorySpending(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return transactionAnalyticsService.getCategorySpending(fromDate, toDate);
    }

    @GetMapping("/transactions/category-spending/top")
    public ResponseEntity<CategorySpendingDTO> topCategorySpending(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return transactionAnalyticsService.getTopSpendingCategory(fromDate, toDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/accounts")
    public List<AccountBalanceDTO> accounts(@RequestParam(required = false) String sort) {
        if ("balanceDesc".equalsIgnoreCase(sort)) {
            return accountAnalyticsService.getSortedByBalanceDesc();
        }
        return accountAnalyticsService.getAll();
    }

    @GetMapping("/accounts/highest-balance")
    public ResponseEntity<AccountBalanceDTO> highestBalance() {
        return accountAnalyticsService.getHighestBalanceAccount()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/budgets/status")
    public List<BudgetStatusDTO> budgetStatuses() {
        return budgetAnalyticsService.getAllStatuses();
    }

    @GetMapping("/budgets/{category}/status")
    public ResponseEntity<BudgetStatusDTO> budgetStatus(@PathVariable String category) {
        return budgetAnalyticsService.getStatusByCategory(category)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/budgets/exceeded")
    public List<BudgetStatusDTO> exceededBudgets() {
        return budgetAnalyticsService.getExceededBudgets();
    }
}
