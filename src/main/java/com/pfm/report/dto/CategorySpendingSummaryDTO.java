package com.pfm.report.dto;

import java.math.BigDecimal;

public record CategorySpendingSummaryDTO(
        String category,
        BigDecimal totalAmount,
        long transactionCount
) {}
