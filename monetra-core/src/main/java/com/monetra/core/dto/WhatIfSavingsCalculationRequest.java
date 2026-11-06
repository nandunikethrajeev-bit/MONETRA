package com.monetra.core.dto;

import java.math.BigDecimal;

public class WhatIfSavingsCalculationRequest {
    private String goalName;
    private BigDecimal targetAmount;
    private Integer durationMonths;
    private BigDecimal initialDeposit;
    private BigDecimal annualInterestRatePercentage; // e.g. 5.0 for 5%

    public WhatIfSavingsCalculationRequest() {
        this.initialDeposit = BigDecimal.ZERO;
        this.annualInterestRatePercentage = BigDecimal.ZERO;
    }

    public WhatIfSavingsCalculationRequest(String goalName, BigDecimal targetAmount, Integer durationMonths,
                                           BigDecimal initialDeposit, BigDecimal annualInterestRatePercentage) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.durationMonths = durationMonths;
        this.initialDeposit = initialDeposit != null ? initialDeposit : BigDecimal.ZERO;
        this.annualInterestRatePercentage = annualInterestRatePercentage != null ? annualInterestRatePercentage : BigDecimal.ZERO;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(BigDecimal initialDeposit) {
        this.initialDeposit = initialDeposit;
    }

    public BigDecimal getAnnualInterestRatePercentage() {
        return annualInterestRatePercentage;
    }

    public void setAnnualInterestRatePercentage(BigDecimal annualInterestRatePercentage) {
        this.annualInterestRatePercentage = annualInterestRatePercentage;
    }
}

