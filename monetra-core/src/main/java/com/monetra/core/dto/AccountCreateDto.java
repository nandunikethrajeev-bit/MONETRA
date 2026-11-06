package com.monetra.core.dto;

import com.monetra.core.model.AccountType;
import java.math.BigDecimal;

public class AccountCreateDto {
    private String name;
    private AccountType type;
    private BigDecimal initialBalance;
    private String currency;
    private String description;

    public AccountCreateDto() {
        this.type = AccountType.CHECKING;
        this.initialBalance = BigDecimal.ZERO;
        this.currency = "USD";
    }

    public AccountCreateDto(String name, AccountType type, BigDecimal initialBalance, String currency, String description) {
        this.name = name;
        this.type = type != null ? type : AccountType.CHECKING;
        this.initialBalance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
        this.currency = currency != null ? currency : "USD";
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

