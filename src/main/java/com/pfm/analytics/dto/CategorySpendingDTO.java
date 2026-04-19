package com.pfm.analytics.dto;

import java.math.BigDecimal;

public record CategorySpendingDTO(
        String category,
        BigDecimal totalAmount,
        long transactionCount
) {}
