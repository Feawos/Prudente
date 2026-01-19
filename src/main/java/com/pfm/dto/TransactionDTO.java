package com.pfm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(
        String type,
        BigDecimal amount,
        String currency,
        LocalDate date,
        String accountId
) {}
