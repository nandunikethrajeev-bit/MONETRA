package com.monetra.core.dto;

import com.monetra.core.model.RecurrenceFrequency;
import com.monetra.core.model.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RecurringTransactionCreateDto {
    private Long accountId;
    private Long categoryId;
    private BigDecimal amount;
    private TransactionType type;
    private RecurrenceFrequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    public RecurringTransactionCreateDto() {
        this.frequency = RecurrenceFrequency.MONTHLY;
        this.startDate = LocalDate.now();
        this.type = TransactionType.EXPENSE;
    }

    public RecurringTransactionCreateDto(Long accountId, Long categoryId, BigDecimal amount,
                                         TransactionType type, RecurrenceFrequency frequency,
                                         LocalDate startDate, LocalDate endDate, String description) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.type = type != null ? type : TransactionType.EXPENSE;
        this.frequency = frequency != null ? frequency : RecurrenceFrequency.MONTHLY;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.endDate = endDate;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
