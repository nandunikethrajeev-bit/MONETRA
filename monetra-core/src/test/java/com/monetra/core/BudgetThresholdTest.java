package com.monetra.core;

import com.monetra.core.dto.BudgetStatusDto;
import com.monetra.core.model.Budget;
import com.monetra.core.model.BudgetStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetThresholdTest {

    @Test
    @DisplayName("Budget status is OK when usage is below 80%")
    void testBudgetStatusOk() {
        Budget budget = new Budget(1L, 100L, 10L, "Groceries", new BigDecimal("1000.00"), 5, 2026, null, null);
        BigDecimal spent = new BigDecimal("750.00"); // 75%

        BudgetStatusDto status = BudgetStatusDto.calculate(budget, spent);

        assertThat(status.getStatus()).isEqualTo(BudgetStatus.OK);
        assertThat(status.getPercentageUsed()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(status.getRemainingAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test
    @DisplayName("Budget status triggers WARNING when usage reaches exactly 80%")
    void testBudgetStatusWarningThresholdAt80() {
        Budget budget = new Budget(1L, 100L, 10L, "Dining Out", new BigDecimal("500.00"), 5, 2026, null, null);
        BigDecimal spent = new BigDecimal("400.00"); // 80%

        BudgetStatusDto status = BudgetStatusDto.calculate(budget, spent);

        assertThat(status.getStatus()).isEqualTo(BudgetStatus.WARNING);
        assertThat(status.getPercentageUsed()).isEqualByComparingTo(new BigDecimal("80.00"));
        assertThat(status.getRemainingAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(status.getAlertMessage()).contains("Warning: 80.0% of budget consumed");
    }

    @Test
    @DisplayName("Budget status triggers EXCEEDED when usage is greater than 100%")
    void testBudgetStatusExceeded() {
        Budget budget = new Budget(1L, 100L, 10L, "Shopping", new BigDecimal("800.00"), 5, 2026, null, null);
        BigDecimal spent = new BigDecimal("950.00"); // 118.75%

        BudgetStatusDto status = BudgetStatusDto.calculate(budget, spent);

        assertThat(status.getStatus()).isEqualTo(BudgetStatus.EXCEEDED);
        assertThat(status.getPercentageUsed()).isEqualByComparingTo(new BigDecimal("118.75"));
        assertThat(status.getRemainingAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(status.getAlertMessage()).contains("Exceeded by 150.00");
    }
}

