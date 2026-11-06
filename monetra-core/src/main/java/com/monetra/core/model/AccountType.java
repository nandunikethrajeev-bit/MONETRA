package com.monetra.core.model;

public enum AccountType {
    CHECKING("Checking Account"),
    SAVINGS("Savings Account"),
    CREDIT_CARD("Credit Card"),
    INVESTMENT("Investment Account"),
    CASH("Cash Wallet");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
