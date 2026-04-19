package com.pfm.report.dto;

import com.pfm.analytics.dto.AccountBalanceDTO;
import com.pfm.analytics.dto.BudgetStatusDTO;

import java.math.BigDecimal;
import java.util.List;

public record MonthlySummaryDTO(
        int year,
        int month,
        BigDecimal totalCredits,
        BigDecimal totalDebits,
        BigDecimal netCashFlow,
        long transactionCount,
        List<CategorySpendingSummaryDTO> categorySpending,
        List<AccountBalanceDTO> accounts,
        List<BudgetStatusDTO> budgets
) {}
