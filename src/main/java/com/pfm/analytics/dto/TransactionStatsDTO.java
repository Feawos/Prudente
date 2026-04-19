package com.pfm.analytics.dto;

import java.math.BigDecimal;

public record TransactionStatsDTO(
        long totalCount,
        long debitCount,
        long creditCount,
        long transferCount,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        BigDecimal averageAmount
) {}
