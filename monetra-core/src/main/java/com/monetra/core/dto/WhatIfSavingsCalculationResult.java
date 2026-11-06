package com.monetra.core.dto;

import java.math.BigDecimal;

public class WhatIfSavingsCalculationResult {
    private String goalName;
    private BigDecimal targetAmount;
    private int durationMonths;
    private BigDecimal initialDeposit;
    private BigDecimal annualInterestRatePercentage;
    private BigDecimal requiredMonthlySavings;
    private BigDecimal totalPrincipalContributed;
    private BigDecimal totalInterestEarned;
    private BigDecimal finalAccumulatedAmount;

    public WhatIfSavingsCalculationResult() {
    }

    public WhatIfSavingsCalculationResult(String goalName, BigDecimal targetAmount, int durationMonths,
                                          BigDecimal initialDeposit, BigDecimal annualInterestRatePercentage,
                                          BigDecimal requiredMonthlySavings, BigDecimal totalPrincipalContributed,
                                          BigDecimal totalInterestEarned, BigDecimal finalAccumulatedAmount) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.durationMonths = durationMonths;
        this.initialDeposit = initialDeposit;
        this.annualInterestRatePercentage = annualInterestRatePercentage;
        this.requiredMonthlySavings = requiredMonthlySavings;
        this.totalPrincipalContributed = totalPrincipalContributed;
        this.totalInterestEarned = totalInterestEarned;
        this.finalAccumulatedAmount = finalAccumulatedAmount;
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

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
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

    public BigDecimal getRequiredMonthlySavings() {
        return requiredMonthlySavings;
    }

    public void setRequiredMonthlySavings(BigDecimal requiredMonthlySavings) {
        this.requiredMonthlySavings = requiredMonthlySavings;
    }

    public BigDecimal getTotalPrincipalContributed() {
        return totalPrincipalContributed;
    }

    public void setTotalPrincipalContributed(BigDecimal totalPrincipalContributed) {
        this.totalPrincipalContributed = totalPrincipalContributed;
    }

    public BigDecimal getTotalInterestEarned() {
        return totalInterestEarned;
    }

    public void setTotalInterestEarned(BigDecimal totalInterestEarned) {
        this.totalInterestEarned = totalInterestEarned;
    }

    public BigDecimal getFinalAccumulatedAmount() {
        return finalAccumulatedAmount;
    }

    public void setFinalAccumulatedAmount(BigDecimal finalAccumulatedAmount) {
        this.finalAccumulatedAmount = finalAccumulatedAmount;
    }
}

