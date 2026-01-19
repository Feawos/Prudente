package com.pfm.model;

import java.time.LocalDate;

public sealed interface Transaction
        permits Transaction.Debit, Transaction.Credit, Transaction.Transfer {

    Money money();
    LocalDate date();
    Money signedAmount();

    record Debit(Money money, LocalDate date) implements Transaction {
        @Override public Money signedAmount() { return money().negate(); }
    }

    record Credit(Money money, LocalDate date) implements Transaction {
        @Override public Money signedAmount() { return money(); }
    }

    record Transfer(Money money, LocalDate date,
                    String fromAccount,
                    String toAccount) implements Transaction {
        @Override public Money signedAmount() { return money(); }
    }
}
