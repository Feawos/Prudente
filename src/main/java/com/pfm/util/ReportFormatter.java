package com.pfm.util;

import com.pfm.model.*;

public class ReportFormatter {
    public String format(Transaction tx) {
        return switch(tx) {
            case Transaction.Debit d -> "DEBIT: " + d.signedAmount();
            case Transaction.Credit c -> "CREDIT: " + c.signedAmount();
            case Transaction.Transfer t -> "TRANSFER from:" + t.fromAccount() + " to:" + t.toAccount();
        };
    }

    public String format(Money money) {
        return money.amount().toPlainString() + " EUR";
    }
}
