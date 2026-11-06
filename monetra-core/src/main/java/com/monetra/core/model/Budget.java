package com.monetra.core.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Budget {
    private Long id;
    private Long userId;
    private Long categoryId; // NULL means overall monthly budget
    private String name;
    private BigDecimal amount;
    private int month; // 1-12
    private int year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined helper field
    private String categoryName;

    public Budget() {
        this.amount = BigDecimal.ZERO;
    }

    public Budget(Long id, Long userId, Long categoryId, String name, BigDecimal amount,
                  int month, int year, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.name = name;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.month = month;
        this.year = year;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
        this.amount = amount != null ? amount : BigDecimal.ZERO;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Budget budget)) return false;
        return Objects.equals(id, budget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}

