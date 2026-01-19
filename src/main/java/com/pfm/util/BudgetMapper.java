package com.pfm.util;

import com.pfm.domain.Budget;
import com.pfm.dto.BudgetDTO;

public final class BudgetMapper {

    private BudgetMapper() {}

    public static BudgetDTO toDTO(Budget budget) {
        return new BudgetDTO(
                budget.id(),
                budget.category().name(),
                budget.limit().amount(),
                budget.spent().amount(),
                budget.limit().currency().name(),
                budget.startDate(),
                budget.endDate()
        );
    }
}
