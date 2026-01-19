package com.pfm.model;

import java.util.Objects;

public class Account {

    private final String id;
    private final String name;
    private Money balance;
    private final AccountType type;     // CHECKING, SAVINGS, etc.
    private final Currency currency;

    // Constructor
    public Account(String id, String name, AccountType type, Currency currency, Money initialBalance) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.currency = Objects.requireNonNull(currency);
        this.balance = initialBalance == null ? Money.ZERO : initialBalance;
    }

    // Getters
    public String id() { return id; }
    public String name() { return name; }
    public AccountType type() { return type; }
    public Money balance() { return balance; }
    public Currency currency() { return currency; }

    // Account operations
    public void credit(Money amount) {
        balance = balance.add(amount);
    }

    public void debit(Money amount) {
        balance = balance.subtract(amount);
    }
}
