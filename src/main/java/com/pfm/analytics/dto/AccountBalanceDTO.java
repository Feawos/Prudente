package com.pfm.analytics.dto;

import java.math.BigDecimal;

public record AccountBalanceDTO(
        String id,
        String name,
        String type,
        String currency,
        BigDecimal balance
) {}
