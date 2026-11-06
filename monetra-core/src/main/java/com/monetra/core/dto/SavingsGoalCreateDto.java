package com.monetra.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsGoalCreateDto {
    private Long accountId;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal initialAmount;
    private LocalDate targetDate;
    private String notes;

    public SavingsGoalCreateDto() {
        this.initialAmount = BigDecimal.ZERO;
        this.targetDate = LocalDate.now().plusMonths(6);
    }

    public SavingsGoalCreateDto(Long accountId, String name, BigDecimal targetAmount,
                                BigDecimal initialAmount, LocalDate targetDate, String notes) {
        this.accountId = accountId;
        this.name = name;
        this.targetAmount = targetAmount;
        this.initialAmount = initialAmount != null ? initialAmount : BigDecimal.ZERO;
        this.targetDate = targetDate;
        this.notes = notes;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
