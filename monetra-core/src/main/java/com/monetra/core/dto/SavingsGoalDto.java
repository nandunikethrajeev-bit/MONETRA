package com.monetra.core.dto;

import com.monetra.core.model.GoalStatus;
import com.monetra.core.model.SavingsGoal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SavingsGoalDto {
    private Long id;
    private Long userId;
    private Long accountId;
    private String accountName;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal progressPercentage;
    private LocalDate targetDate;
    private GoalStatus status;
    private String notes;
    private LocalDateTime createdAt;

    public SavingsGoalDto() {
    }

    public static SavingsGoalDto fromEntity(SavingsGoal g) {
        if (g == null) return null;
        SavingsGoalDto dto = new SavingsGoalDto();
        dto.setId(g.getId());
        dto.setUserId(g.getUserId());
        dto.setAccountId(g.getAccountId());
        dto.setAccountName(g.getAccountName());
        dto.setName(g.getName());
        dto.setTargetAmount(g.getTargetAmount());
        dto.setCurrentAmount(g.getCurrentAmount());
        dto.setRemainingAmount(g.getRemainingAmount());
        dto.setProgressPercentage(g.getProgressPercentage());
        dto.setTargetDate(g.getTargetDate());
        dto.setStatus(g.getStatus());
        dto.setNotes(g.getNotes());
        dto.setCreatedAt(g.getCreatedAt());
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

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

