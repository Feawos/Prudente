package com.pfm.service;

import com.pfm.model.Account;
import com.pfm.model.AccountType;
import com.pfm.model.Money;
import com.pfm.model.Currency;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountService {

    private final Map<String, Account> accounts = new HashMap<>();

    // Create new account
    public Account create(String id, String name, Money initialBalance) {
        // Default account type CHECKING if not otherwise specified
        AccountType defaultType = AccountType.CHECKING;
        Currency defaultCurrency = initialBalance.currency(); // use currency from Money

        Account acc = new Account(id, name, defaultType, defaultCurrency, initialBalance);
        accounts.put(id, acc);
        return acc;
    }

    // Retrieve account by ID
    public Optional<Account> get(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    // Get all accounts
    public List<Account> getAll() {
        return List.copyOf(accounts.values());
    }
}
