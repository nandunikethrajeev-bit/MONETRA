package com.monetra.core.dto;

import com.monetra.core.model.Account;
import com.monetra.core.model.AccountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto {
    private Long id;
    private Long userId;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private String currency;
    private String description;
    private LocalDateTime createdAt;

    public AccountDto() {
    }

    public AccountDto(Long id, Long userId, String name, AccountType type, BigDecimal balance,
                      String currency, String description, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.currency = currency;
        this.description = description;
        this.createdAt = createdAt;
    }

    public static AccountDto fromEntity(Account account) {
        if (account == null) return null;
        return new AccountDto(
                account.getId(),
                account.getUserId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getCurrency(),
                account.getDescription(),
                account.getCreatedAt()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

