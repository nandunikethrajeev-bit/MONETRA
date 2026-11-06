package com.monetra.core.dto;

import java.math.BigDecimal;

public class SavingsContributionDto {
    private BigDecimal amount;
    private Long sourceAccountId;
    private String note;

    public SavingsContributionDto() {
    }

    public SavingsContributionDto(BigDecimal amount, Long sourceAccountId, String note) {
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.note = note;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
