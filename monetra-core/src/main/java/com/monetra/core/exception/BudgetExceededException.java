package com.monetra.core.exception;

import java.math.BigDecimal;

public class BudgetExceededException extends MonetraException {
    private final BigDecimal budgetLimit;
    private final BigDecimal currentSpent;
    private final BigDecimal newAmount;

    public BudgetExceededException(String message, BigDecimal budgetLimit, BigDecimal currentSpent, BigDecimal newAmount) {
        super(message);
        this.budgetLimit = budgetLimit;
        this.currentSpent = currentSpent;
        this.newAmount = newAmount;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }

    public BigDecimal getCurrentSpent() {
        return currentSpent;
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }
}
