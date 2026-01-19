package com.pfm.domain;

import com.pfm.model.Category;
import com.pfm.model.Money;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a budget for a specific category within a time period.
 */
public class Budget {

    private final String id;
    private final Category category;
    private Money monthlyLimit;
    private Money spent = Money.ZERO;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Budget(Category category, Money monthlyLimit, LocalDate startDate, LocalDate endDate) {
        this.id = UUID.randomUUID().toString();
        this.category = Objects.requireNonNull(category);
        this.monthlyLimit = Objects.requireNonNull(monthlyLimit);
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = Objects.requireNonNull(endDate);
    }

    public String id() { return id; }
    public Category category() { return category; }
    public Money limit() { return monthlyLimit; }
    public Money spent() { return spent; }
    public LocalDate startDate() { return startDate; }
    public LocalDate endDate() { return endDate; }

    public void setLimit(Money newLimit) {
        this.monthlyLimit = newLimit;
    }

    public void applyExpense(Money amount) {
        this.spent = this.spent.add(amount);
    }

    public boolean isExceeded() {
        return spent.amount().compareTo(monthlyLimit.amount()) > 0;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id='" + id + '\'' +
                ", category=" + category +
                ", limit=" + monthlyLimit +
                ", spent=" + spent +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
