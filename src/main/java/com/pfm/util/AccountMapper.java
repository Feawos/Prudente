package com.pfm.util;

import com.pfm.model.Account;
import com.pfm.dto.AccountDTO;

public final class AccountMapper {

    private AccountMapper() {}

    public static AccountDTO toDTO(Account account) {
        return new AccountDTO(
                account.id(),
                account.name(),
                account.type().name(),
                account.balance().amount(),
                account.balance().currency().name() // <- use .name() instead of .code()
        );
    }
}
