package com.monetra.core.dto;

import com.monetra.core.model.RecurrenceFrequency;
import com.monetra.core.model.RecurringTransaction;
import com.monetra.core.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RecurringTransactionDto {
    private Long id;
    private Long userId;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private TransactionType type;
    private RecurrenceFrequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextDueDate;
    private LocalDate lastProcessedDate;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;

    public RecurringTransactionDto() {
    }

    public static RecurringTransactionDto fromEntity(RecurringTransaction r) {
        if (r == null) return null;
        RecurringTransactionDto dto = new RecurringTransactionDto();
        dto.setId(r.getId());
        dto.setUserId(r.getUserId());
        dto.setAccountId(r.getAccountId());
        dto.setAccountName(r.getAccountName());
        dto.setCategoryId(r.getCategoryId());
        dto.setCategoryName(r.getCategoryName());
        dto.setAmount(r.getAmount());
        dto.setType(r.getType());
        dto.setFrequency(r.getFrequency());
        dto.setStartDate(r.getStartDate());
        dto.setEndDate(r.getEndDate());
        dto.setNextDueDate(r.getNextDueDate());
        dto.setLastProcessedDate(r.getLastProcessedDate());
        dto.setDescription(r.getDescription());
        dto.setActive(r.isActive());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurrenceFrequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public LocalDate getLastProcessedDate() {
        return lastProcessedDate;
    }

    public void setLastProcessedDate(LocalDate lastProcessedDate) {
        this.lastProcessedDate = lastProcessedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

