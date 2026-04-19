package com.pfm.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetStatusDTO(
        String budgetId,
        String category,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        boolean exceeded,
        LocalDate startDate,
        LocalDate endDate
) {}
