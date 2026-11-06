package com.monetra.core.model;

public enum BudgetStatus {
    OK("On Track"),
    WARNING("Approaching Limit (>=80%)"),
    EXCEEDED("Budget Exceeded");

    private final String displayName;

    BudgetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
