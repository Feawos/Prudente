package com.pfm.util;

import com.pfm.model.Transaction;

import java.util.HashMap;
import java.util.Map;

public final class TransactionMapper {

    private TransactionMapper() {}

    public static Map<String, String> toMap(Transaction tx) {
        Map<String, String> row = new HashMap<>();

        row.put("date", tx.date().toString());
        row.put("amount", tx.money().amount().toString());
        row.put("currency", tx.money().currency().code());
        row.put("category", tx.category().map(Enum::name).orElse(""));

        if (tx instanceof Transaction.Debit) {
            row.put("type", "debit");
            row.put("fromAccount", "");
            row.put("toAccount", "");
        }
        else if (tx instanceof Transaction.Credit) {
            row.put("type", "credit");
            row.put("fromAccount", "");
            row.put("toAccount", "");
        }
        else if (tx instanceof Transaction.Transfer t) {
            row.put("type", "transfer");
            row.put("fromAccount", t.fromAccount());
            row.put("toAccount", t.toAccount());
        }

        return row;
    }
}
