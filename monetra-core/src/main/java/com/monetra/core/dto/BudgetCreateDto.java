package com.monetra.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetCreateDto {
    private Long categoryId; // null for overall monthly budget
    private String name;
    private BigDecimal amount;
    private Integer month;
    private Integer year;

    public BudgetCreateDto() {
        LocalDate now = LocalDate.now();
        this.month = now.getMonthValue();
        this.year = now.getYear();
    }

    public BudgetCreateDto(Long categoryId, String name, BigDecimal amount, Integer month, Integer year) {
        this.categoryId = categoryId;
        this.name = name;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}

