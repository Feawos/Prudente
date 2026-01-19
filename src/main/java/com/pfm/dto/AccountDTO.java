package com.pfm.dto;

import java.math.BigDecimal;

public record AccountDTO(
        String id,
        String name,
        String type,
        BigDecimal balance,
        String currency
) {}
