package com.pfm.analytics;

import com.pfm.analytics.dto.TransactionTypeView;
import com.pfm.analytics.dto.TransactionViewDTO;
import com.pfm.model.Transaction;

public final class TransactionViewMapper {

    private TransactionViewMapper() {}

    public static TransactionViewDTO toView(Transaction transaction) {
        return switch (transaction) {
            case Transaction.Debit debit -> new TransactionViewDTO(
                    TransactionTypeView.DEBIT.name(),
                    debit.money().amount(),
                    debit.money().currency().name(),
                    debit.date(),
                    debit.category().map(Enum::name).orElse(null),
                    null,
                    null
            );
            case Transaction.Credit credit -> new TransactionViewDTO(
                    TransactionTypeView.CREDIT.name(),
                    credit.money().amount(),
                    credit.money().currency().name(),
                    credit.date(),
                    credit.category().map(Enum::name).orElse(null),
                    null,
                    null
            );
            case Transaction.Transfer transfer -> new TransactionViewDTO(
                    TransactionTypeView.TRANSFER.name(),
                    transfer.money().amount(),
                    transfer.money().currency().name(),
                    transfer.date(),
                    null,
                    transfer.fromAccount(),
                    transfer.toAccount()
            );
        };
    }

    public static TransactionTypeView detectType(Transaction transaction) {
        return switch (transaction) {
            case Transaction.Debit ignored -> TransactionTypeView.DEBIT;
            case Transaction.Credit ignored -> TransactionTypeView.CREDIT;
            case Transaction.Transfer ignored -> TransactionTypeView.TRANSFER;
        };
    }
}
