package com.monetra.core.dto;

import java.math.BigDecimal;

public class CategorySpendingDto {
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private BigDecimal amount;
    private BigDecimal percentage;

    public CategorySpendingDto() {
        this.amount = BigDecimal.ZERO;
        this.percentage = BigDecimal.ZERO;
    }

    public CategorySpendingDto(Long categoryId, String categoryName, String categoryColor,
                               String categoryIcon, BigDecimal amount, BigDecimal percentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.categoryIcon = categoryIcon;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.percentage = percentage != null ? percentage : BigDecimal.ZERO;
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

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
