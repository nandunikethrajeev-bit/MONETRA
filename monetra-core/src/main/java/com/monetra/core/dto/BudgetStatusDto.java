package com.monetra.core.dto;

import com.monetra.core.model.Budget;
import com.monetra.core.model.BudgetStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetStatusDto {
    private Long budgetId;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private BigDecimal percentageUsed;
    private BudgetStatus status;
    private String alertMessage;
    private int month;
    private int year;

    public BudgetStatusDto() {
    }

    public static BudgetStatusDto calculate(Budget budget, BigDecimal spent) {
        BudgetStatusDto dto = new BudgetStatusDto();
        dto.setBudgetId(budget.getId());
        dto.setUserId(budget.getUserId());
        dto.setCategoryId(budget.getCategoryId());
        dto.setCategoryName(budget.getCategoryName());
        dto.setName(budget.getName());
        dto.setMonth(budget.getMonth());
        dto.setYear(budget.getYear());

        BigDecimal limit = budget.getAmount() != null ? budget.getAmount() : BigDecimal.ZERO;
        BigDecimal actualSpent = spent != null ? spent : BigDecimal.ZERO;

        dto.setBudgetAmount(limit);
        dto.setSpentAmount(actualSpent);

        BigDecimal remaining = limit.subtract(actualSpent);
        dto.setRemainingAmount(remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO);

        BigDecimal percentage;
        if (limit.compareTo(BigDecimal.ZERO) > 0) {
            percentage = actualSpent.multiply(BigDecimal.valueOf(100)).divide(limit, 2, RoundingMode.HALF_UP);
        } else {
            percentage = actualSpent.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        dto.setPercentageUsed(percentage);

        // Status thresholds: 80% warning, >100% exceeded
        if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            dto.setStatus(BudgetStatus.EXCEEDED);
            dto.setAlertMessage(String.format("Exceeded by %s!", actualSpent.subtract(limit).setScale(2, RoundingMode.HALF_UP)));
        } else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            dto.setStatus(BudgetStatus.WARNING);
            dto.setAlertMessage(String.format("Warning: %s%% of budget consumed!", percentage.setScale(1, RoundingMode.HALF_UP)));
        } else {
            dto.setStatus(BudgetStatus.OK);
            dto.setAlertMessage("On track");
        }

        return dto;
    }

    public Long getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Long budgetId) {
        this.budgetId = budgetId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public BigDecimal getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(BigDecimal percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public BudgetStatus getStatus() {
        return status;
    }

    public void setStatus(BudgetStatus status) {
        this.status = status;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

