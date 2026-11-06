package com.monetra.core.dto;

import com.monetra.core.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionCreateDto {
    private Long accountId;
    private Long categoryId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private String description;
    private String notes;

    public TransactionCreateDto() {
        this.transactionDate = LocalDate.now();
        this.type = TransactionType.EXPENSE;
    }

    public TransactionCreateDto(Long accountId, Long categoryId, BigDecimal amount, TransactionType type,
                                LocalDate transactionDate, String description, String notes) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type != null ? type : TransactionType.EXPENSE;
        this.transactionDate = transactionDate != null ? transactionDate : LocalDate.now();
        this.description = description;
        this.notes = notes;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

