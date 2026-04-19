package com.pfm.web;

import com.pfm.dto.TransactionDTO;
import com.pfm.dto.TransferDTO;
import com.pfm.model.Category;
import com.pfm.model.Currency;
import com.pfm.model.Money;
import com.pfm.model.Transaction;
import com.pfm.service.TransactionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService txService;

    public TransactionController(TransactionService txService) {
        this.txService = txService;
    }

    @GetMapping
    public List<Transaction> all() {
        return txService.all();
    }

    @PostMapping("/debit")
    public Transaction debit(@RequestBody TransactionDTO dto) {
        Money m = new Money(dto.amount(), Currency.valueOf(dto.currency()));
        LocalDate date = dto.date() != null ? dto.date() : LocalDate.now();
        Category category = parseCategory(dto.category());
        return txService.recordDebit(m, date, dto.accountId(), category);
    }

    @PostMapping("/credit")
    public Transaction credit(@RequestBody TransactionDTO dto) {
        Money m = new Money(dto.amount(), Currency.valueOf(dto.currency()));
        LocalDate date = dto.date() != null ? dto.date() : LocalDate.now();
        Category category = parseCategory(dto.category());
        return txService.recordCredit(m, date, dto.accountId(), category);
    }

    @PostMapping("/transfer")
    public Transaction transfer(@RequestBody TransferDTO dto) {
        Money m = new Money(dto.amount(), Currency.valueOf(dto.currency()));
        LocalDate date = LocalDate.now();
        return txService.recordTransfer(m, date, dto.fromAccount(), dto.toAccount());
    }

    @GetMapping("/export")
    public String export() {
        return txService.exportAllTransactions();
    }

    private Category parseCategory(String category) {
        if (category == null || category.isBlank()) {
            return Category.OTHER;
        }
        return Category.valueOf(category.toUpperCase());
    }
}
