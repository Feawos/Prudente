package com.pfm.dto;

import java.math.BigDecimal;

public record TransferDTO(
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        String currency
) {}
