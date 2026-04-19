package com.pfm.analytics.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionViewDTO(
        String type,
        BigDecimal amount,
        String currency,
        LocalDate date,
        String category,
        String fromAccount,
        String toAccount
) {}
