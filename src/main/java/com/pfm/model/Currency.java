package com.pfm.model;

public enum Currency {
    EUR,
    USD,
    GBP,
    NGN;

    public String code() {
        return this.name(); // returns "EUR", "USD", etc.
    }
}
