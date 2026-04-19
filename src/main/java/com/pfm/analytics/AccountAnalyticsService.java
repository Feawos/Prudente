package com.pfm.analytics;

import com.pfm.analytics.dto.AccountBalanceDTO;
import com.pfm.model.Account;
import com.pfm.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AccountAnalyticsService {

    private final AccountService accountService;

    public AccountAnalyticsService(AccountService accountService) {
        this.accountService = accountService;
    }

    public List<AccountBalanceDTO> getAll() {
        return accountService.getAll().stream()
                .map(this::toBalanceDto)
                .toList();
    }

    public List<AccountBalanceDTO> getSortedByBalanceDesc() {
        return accountService.getAll().stream()
                .sorted(Comparator.comparing((Account account) -> account.balance().amount()).reversed())
                .map(this::toBalanceDto)
                .toList();
    }

    public Optional<AccountBalanceDTO> getHighestBalanceAccount() {
        return accountService.getAll().stream()
                .max(Comparator.comparing(account -> account.balance().amount()))
                .map(this::toBalanceDto);
    }

    public Optional<AccountBalanceDTO> getLowestBalanceAccount() {
        return accountService.getAll().stream()
                .min(Comparator.comparing(account -> account.balance().amount()))
                .map(this::toBalanceDto);
    }

    private AccountBalanceDTO toBalanceDto(Account account) {
        return new AccountBalanceDTO(
                account.id(),
                account.name(),
                account.type().name(),
                account.currency().name(),
                account.balance().amount()
        );
    }
}
