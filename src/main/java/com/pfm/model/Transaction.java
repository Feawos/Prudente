package com.pfm.model;

import java.time.LocalDate;
import java.util.Optional;

public sealed interface Transaction
        permits Transaction.Debit, Transaction.Credit, Transaction.Transfer {

    Money money();
    LocalDate date();
    Money signedAmount();
    Optional<Category> category();

    record Debit(Money money, LocalDate date, Category categoryValue) implements Transaction {
        @Override public Money signedAmount() { return money().negate(); }
        @Override public Optional<Category> category() { return Optional.ofNullable(categoryValue); }
    }

    record Credit(Money money, LocalDate date, Category categoryValue) implements Transaction {
        @Override public Money signedAmount() { return money(); }
        @Override public Optional<Category> category() { return Optional.ofNullable(categoryValue); }
    }

    record Transfer(Money money, LocalDate date,
                    String fromAccount,
                    String toAccount) implements Transaction {
        @Override public Money signedAmount() { return money(); }
        @Override public Optional<Category> category() { return Optional.empty(); }
    }
}
