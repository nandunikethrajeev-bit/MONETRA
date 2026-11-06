package com.monetra.core.dto;

import com.monetra.core.model.Budget;
import com.monetra.core.model.BudgetStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetDto {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal amount;
    private int month;
    private int year;

    public BudgetDto() {
    }

    public static BudgetDto fromEntity(Budget b) {
        if (b == null) return null;
        BudgetDto dto = new BudgetDto();
        dto.setId(b.getId());
        dto.setUserId(b.getUserId());
        dto.setCategoryId(b.getCategoryId());
        dto.setCategoryName(b.getCategoryName());
        dto.setName(b.getName());
        dto.setAmount(b.getAmount());
        dto.setMonth(b.getMonth());
        dto.setYear(b.getYear());
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
