package com.pfm.service;

import com.pfm.domain.Budget;
import com.pfm.model.Category;
import com.pfm.model.Money;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Service to manage budgets.
 */
@Service
public class BudgetService {

    private final Map<String, Budget> budgets = new HashMap<>();

    // Create a new budget
    public Budget create(Category category, Money limit, LocalDate start, LocalDate end) {
        Budget budget = new Budget(category, limit, start, end);
        budgets.put(budget.id(), budget);
        return budget;
    }

    // Get budget by ID
    public Optional<Budget> get(String id) {
        return Optional.ofNullable(budgets.get(id));
    }

    // Get all budgets
    public List<Budget> getAll() {
        return List.copyOf(budgets.values());
    }

    // Apply an expense to a category budget
    public void applyExpense(Category category, Money expense) {
        budgets.values().stream()
                .filter(b -> b.category() == category)
                .findFirst()
                .ifPresent(b -> b.applyExpense(expense));
    }

    // Check if a budget exceeded
    public boolean isExceeded(Category category) {
        return budgets.values().stream()
                .filter(b -> b.category() == category)
                .findFirst()
                .map(Budget::isExceeded)
                .orElse(false);
    }
}
