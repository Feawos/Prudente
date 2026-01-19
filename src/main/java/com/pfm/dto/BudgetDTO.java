package com.pfm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetDTO(
        String id,
        String category,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        String currency,
        LocalDate startDate,
        LocalDate endDate
) {}
