package com.monetra.core;

import com.monetra.core.dto.WhatIfSavingsCalculationRequest;
import com.monetra.core.dto.WhatIfSavingsCalculationResult;
import com.monetra.core.service.SavingsCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

class FinancialCalculationTest {

    private SavingsCalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new SavingsCalculatorService();
    }

    @Test
    @DisplayName("Calculate simple required savings with zero interest rate")
    void testZeroInterestSavingsCalculation() {
        // Target: $12,000 in 12 months with $0 initial deposit
        WhatIfSavingsCalculationRequest req = new WhatIfSavingsCalculationRequest(
                "Car Fund",
                new BigDecimal("12000.00"),
                12,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        WhatIfSavingsCalculationResult result = calculatorService.calculateRequiredSavings(req);

        assertThat(result.getRequiredMonthlySavings())
                .isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.getTotalPrincipalContributed())
                .isEqualByComparingTo(new BigDecimal("12000.00"));
        assertThat(result.getTotalInterestEarned())
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Calculate required savings with initial deposit and zero interest")
    void testSavingsWithInitialDeposit() {
        // Target: $10,000 in 10 months with $2,000 initial deposit
        // Remaining to save: $8,000 / 10 = $800.00 per month
        WhatIfSavingsCalculationRequest req = new WhatIfSavingsCalculationRequest(
                "Vacation Fund",
                new BigDecimal("10000.00"),
                10,
                new BigDecimal("2000.00"),
                BigDecimal.ZERO
        );

        WhatIfSavingsCalculationResult result = calculatorService.calculateRequiredSavings(req);

        assertThat(result.getRequiredMonthlySavings())
                .isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(result.getTotalPrincipalContributed())
                .isEqualByComparingTo(new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Calculate required monthly savings with compound interest")
    void testCompoundInterestSavingsCalculation() {
        // Target: $20,000 in 24 months with $2,000 initial deposit at 6% annual interest
        WhatIfSavingsCalculationRequest req = new WhatIfSavingsCalculationRequest(
                "Emergency Fund",
                new BigDecimal("20000.00"),
                24,
                new BigDecimal("2000.00"),
                new BigDecimal("6.0")
        );

        WhatIfSavingsCalculationResult result = calculatorService.calculateRequiredSavings(req);

        // Required monthly savings with compound interest should be less than without interest
        // Without interest: (20000 - 2000) / 24 = 750.00
        assertThat(result.getRequiredMonthlySavings()).isLessThan(new BigDecimal("750.00"));
        assertThat(result.getTotalInterestEarned()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.getFinalAccumulatedAmount()).isEqualByComparingTo(new BigDecimal("20000.00"));
    }

    @Test
    @DisplayName("Balance and Net Savings calculations using BigDecimal")
    void testBalanceAndSavingsArithmetic() {
        BigDecimal income1 = new BigDecimal("4500.50");
        BigDecimal income2 = new BigDecimal("1250.25");
        BigDecimal totalIncome = income1.add(income2);
        assertThat(totalIncome).isEqualByComparingTo(new BigDecimal("5750.75"));

        BigDecimal expense1 = new BigDecimal("1200.00");
        BigDecimal expense2 = new BigDecimal("350.45");
        BigDecimal expense3 = new BigDecimal("149.30");
        BigDecimal totalExpense = expense1.add(expense2).add(expense3);
        assertThat(totalExpense).isEqualByComparingTo(new BigDecimal("1699.75"));

        BigDecimal netSavings = totalIncome.subtract(totalExpense);
        assertThat(netSavings).isEqualByComparingTo(new BigDecimal("4051.00"));

        BigDecimal savingsRate = netSavings.multiply(BigDecimal.valueOf(100))
                .divide(totalIncome, 2, RoundingMode.HALF_UP);

        // 4051.00 * 100 / 5750.75 = 70.44%
        assertThat(savingsRate).isEqualByComparingTo(new BigDecimal("70.44"));
    }
}

