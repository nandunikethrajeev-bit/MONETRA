package com.monetra.core.dto;

import com.monetra.core.model.TransactionType;
import java.time.LocalDate;

public class TransactionFilterDto {
    private Long accountId;
    private Long categoryId;
    private TransactionType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String searchQuery;

    public TransactionFilterDto() {
    }

    public TransactionFilterDto(Long accountId, Long categoryId, TransactionType type,
                                LocalDate startDate, LocalDate endDate, String searchQuery) {
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.searchQuery = searchQuery;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
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

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}

