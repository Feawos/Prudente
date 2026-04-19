package com.pfm.service;

import com.pfm.model.Category;
import com.pfm.model.Money;
import com.pfm.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private final List<Transaction> store = new ArrayList<>();

    public List<Transaction> all() {
        return List.copyOf(store);
    }

    public Transaction recordDebit(Money money, LocalDate date, String accountId, Category category) {
        Transaction t = new Transaction.Debit(money, date, category);
        store.add(t);
        return t;
    }

    public Transaction recordCredit(Money money, LocalDate date, String accountId, Category category) {
        Transaction t = new Transaction.Credit(money, date, category);
        store.add(t);
        return t;
    }

    public Transaction recordTransfer(Money money, LocalDate date, String from, String to) {
        Transaction t = new Transaction.Transfer(money, date, from, to);
        store.add(t);
        return t;
    }

    public String exportAllTransactions() {
        StringBuilder sb = new StringBuilder();
        sb.append("TYPE,AMOUNT,CURRENCY,DATE,CATEGORY,FROM,TO\n");

        for (Transaction tx : store) {

            if (tx instanceof Transaction.Debit d) {
                sb.append("DEBIT,")
                        .append(d.money().amount()).append(",")
                        .append(d.money().currency()).append(",")
                        .append(d.date()).append(",")
                        .append(d.category().map(Enum::name).orElse("")).append(",")
                        .append(",") // from
                        .append("\n"); // to
            }

            if (tx instanceof Transaction.Credit c) {
                sb.append("CREDIT,")
                        .append(c.money().amount()).append(",")
                        .append(c.money().currency()).append(",")
                        .append(c.date()).append(",")
                        .append(c.category().map(Enum::name).orElse("")).append(",")
                        .append(",") // from
                        .append("\n"); // to
            }

            if (tx instanceof Transaction.Transfer t) {
                sb.append("TRANSFER,")
                        .append(t.money().amount()).append(",")
                        .append(t.money().currency()).append(",")
                        .append(t.date()).append(",")
                        .append(",")
                        .append(t.fromAccount()).append(",")
                        .append(t.toAccount())
                        .append("\n");
            }
        }

        return sb.toString();
    }
}
