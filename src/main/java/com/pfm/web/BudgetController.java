package com.pfm.web;

import com.pfm.dto.BudgetDTO;
import com.pfm.model.Category;
import com.pfm.model.Money;
import com.pfm.service.BudgetService;
import com.pfm.util.BudgetMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for managing budgets.
 */
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Get all budgets
    @GetMapping
    public List<BudgetDTO> list() {
        return budgetService.getAll().stream()
                .map(BudgetMapper::toDTO)
                .toList();
    }

    // Create a new budget
    @PostMapping
    public ResponseEntity<BudgetDTO> create(@RequestBody BudgetDTO dto) {
        Category category;
        try {
            category = Category.valueOf(dto.category().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Money limit = new Money(dto.limitAmount(), Money.ZERO.currency()); // default EUR
        LocalDate start = dto.startDate() != null ? dto.startDate() : LocalDate.now();
        LocalDate end = dto.endDate() != null ? dto.endDate() : start.plusMonths(1);

        var budget = budgetService.create(category, limit, start, end);
        return ResponseEntity.ok(BudgetMapper.toDTO(budget));
    }

    // Apply an expense to a category
    @PostMapping("/{category}/expense")
    public ResponseEntity<BudgetDTO> applyExpense(@PathVariable String category,
                                                  @RequestParam BigDecimal amount) {
        Category cat;
        try {
            cat = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Money expense = new Money(amount, Money.ZERO.currency());
        budgetService.applyExpense(cat, expense);

        var budgetOpt = budgetService.getAll().stream()
                .filter(b -> b.category() == cat)
                .findFirst();

        return budgetOpt.map(b -> ResponseEntity.ok(BudgetMapper.toDTO(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Check if a budget is exceeded
    @GetMapping("/{category}/status")
    public ResponseEntity<Boolean> isExceeded(@PathVariable String category) {
        Category cat;
        try {
            cat = Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        boolean exceeded = budgetService.isExceeded(cat);
        return ResponseEntity.ok(exceeded);
    }
}
