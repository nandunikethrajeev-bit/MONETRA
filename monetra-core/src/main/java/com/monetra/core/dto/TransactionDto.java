package com.monetra.core.dto;

import com.monetra.core.model.Transaction;
import com.monetra.core.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDto {
    private Long id;
    private Long userId;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private String description;
    private String notes;
    private boolean isRecurringInstance;
    private Long recurringTransactionId;
    private LocalDateTime createdAt;

    public TransactionDto() {
    }

    public static TransactionDto fromEntity(Transaction t) {
        if (t == null) return null;
        TransactionDto dto = new TransactionDto();
        dto.setId(t.getId());
        dto.setUserId(t.getUserId());
        dto.setAccountId(t.getAccountId());
        dto.setAccountName(t.getAccountName());
        dto.setCategoryId(t.getCategoryId());
        dto.setCategoryName(t.getCategoryName());
        dto.setCategoryColor(t.getCategoryColor());
        dto.setCategoryIcon(t.getCategoryIcon());
        dto.setAmount(t.getAmount());
        dto.setType(t.getType());
        dto.setTransactionDate(t.getTransactionDate());
        dto.setDescription(t.getDescription());
        dto.setNotes(t.getNotes());
        dto.setRecurringInstance(t.isRecurringInstance());
        dto.setRecurringTransactionId(t.getRecurringTransactionId());
        dto.setCreatedAt(t.getCreatedAt());
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

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
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

    public boolean isRecurringInstance() {
        return isRecurringInstance;
    }

    public void setRecurringInstance(boolean recurringInstance) {
        isRecurringInstance = recurringInstance;
    }

    public Long getRecurringTransactionId() {
        return recurringTransactionId;
    }

    public void setRecurringTransactionId(Long recurringTransactionId) {
        this.recurringTransactionId = recurringTransactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
