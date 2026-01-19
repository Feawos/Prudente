package com.pfm.web;

import com.pfm.api.AccountApi;
import com.pfm.dto.AccountDTO;
import com.pfm.model.Account;
import com.pfm.model.Money;
import com.pfm.model.Currency;
import com.pfm.service.AccountService;
import com.pfm.util.AccountMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController implements AccountApi {

    private final AccountService accounts;

    public AccountController(AccountService accounts) {
        this.accounts = accounts;
    }

    @Override
    @GetMapping
    public List<AccountDTO> list() {
        return accounts.getAll().stream()
                .map(AccountMapper::toDTO)
                .toList();
    }

    @Override
    @PostMapping
    public ResponseEntity<AccountDTO> create(@RequestBody AccountDTO dto) {
        // default to EUR if currency is null/empty
        String currencyCode = (dto.currency() == null || dto.currency().isEmpty()) ? "EUR" : dto.currency();
        Currency currencyEnum;
        try {
            currencyEnum = Currency.valueOf(currencyCode); // lookup enum constant
        } catch (IllegalArgumentException e) {
            currencyEnum = Currency.EUR; // fallback to EUR
        }

        BigDecimal amount = (dto.balance() == null) ? BigDecimal.ZERO : dto.balance();

        Account acc = accounts.create(
                dto.id(),
                dto.name(),
                new Money(amount, currencyEnum)
        );

        return ResponseEntity.ok(AccountMapper.toDTO(acc));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> get(@PathVariable String id) {
        return accounts.get(id)
                .map(AccountMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
