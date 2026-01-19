package com.pfm.domain;

import com.pfm.model.Money;
import com.pfm.model.AccountType;
import com.pfm.model.Transaction;

import java.util.Objects;
import java.util.Currency;

public class Account {

    private final String id;
    private final String name;
    private Money balance;
    private final AccountType type;
    private final Currency currency;


    public Account(String id, String name) {
        this(id, name, AccountType.GENERAL, Money.ZERO, Currency.getInstance("EUR"));
    }

    public Account(String id, String name, Money initial) {
        this(id, name, AccountType.GENERAL, initial, Currency.getInstance("EUR"));
    }

    public Account(String id, AccountType type) {
        this(id, null, type, Money.ZERO, Currency.getInstance("EUR"));
    }

    // Full constructor
    public Account(String id, String name, AccountType type, Money initial, Currency currency) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = name != null ? name : type.name() + "-" + id;
        this.type = Objects.requireNonNull(type, "account type cannot be null");
        this.currency = Objects.requireNonNull(currency, "currency cannot be null");
        this.balance = Objects.requireNonNull(initial, "initial Money cannot be null");
    }

    public void credit(Money amount) {
        Objects.requireNonNull(amount, "credit amount cannot be null");
        this.balance = this.balance.add(amount);
    }

    public void debit(Money amount) {
        Objects.requireNonNull(amount, "debit amount cannot be null");
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Apply a transaction using sealed Transaction hierarchy.
     */
    public void apply(Transaction tx) {
        Objects.requireNonNull(tx, "transaction cannot be null");

        switch (tx) {

            case Transaction.Debit d -> debit(d.money());
            case Transaction.Credit c -> credit(c.money());

            case Transaction.Transfer t -> {
                if (t.fromAccount().equals(this.id)) {
                    debit(t.money());
                }
                if (t.toAccount().equals(this.id)) {
                    credit(t.money());
                }
            }

            default -> throw new IllegalStateException("Unknown transaction type: " + tx);
        }
    }

    // -------------------------------------------------------------
    // Getters — required for services & API
    // -------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getBalance() {
        return balance;
    }

    public AccountType getType() {
        return type;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{id='%s', name='%s', type=%s, currency=%s, balance=%s}"
                .formatted(id, name, type, currency, balance);
    }
}
