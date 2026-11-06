package com.monetra.core.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinancialSummaryDto {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalSavings;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlySavings;
    private BigDecimal savingsRatePercentage;
    private BigDecimal overallBudgetUtilizationPercentage;

    private List<TransactionDto> recentTransactions = new ArrayList<>();
    private List<RecurringTransactionDto> upcomingRecurringPayments = new ArrayList<>();
    private List<SavingsGoalDto> savingsGoals = new ArrayList<>();
    private List<BudgetStatusDto> currentBudgets = new ArrayList<>();
    private List<CategorySpendingDto> topSpendingCategories = new ArrayList<>();
    private List<FinancialInsightDto> insights = new ArrayList<>();

    public FinancialSummaryDto() {
        this.totalBalance = BigDecimal.ZERO;
        this.totalIncome = BigDecimal.ZERO;
        this.totalExpenses = BigDecimal.ZERO;
        this.totalSavings = BigDecimal.ZERO;
        this.monthlyIncome = BigDecimal.ZERO;
        this.monthlyExpenses = BigDecimal.ZERO;
        this.monthlySavings = BigDecimal.ZERO;
        this.savingsRatePercentage = BigDecimal.ZERO;
        this.overallBudgetUtilizationPercentage = BigDecimal.ZERO;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalSavings() {
        return totalSavings;
    }

    public void setTotalSavings(BigDecimal totalSavings) {
        this.totalSavings = totalSavings;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(BigDecimal monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public BigDecimal getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(BigDecimal monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public BigDecimal getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(BigDecimal monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public BigDecimal getSavingsRatePercentage() {
        return savingsRatePercentage;
    }

    public void setSavingsRatePercentage(BigDecimal savingsRatePercentage) {
        this.savingsRatePercentage = savingsRatePercentage;
    }

    public BigDecimal getOverallBudgetUtilizationPercentage() {
        return overallBudgetUtilizationPercentage;
    }

    public void setOverallBudgetUtilizationPercentage(BigDecimal overallBudgetUtilizationPercentage) {
        this.overallBudgetUtilizationPercentage = overallBudgetUtilizationPercentage;
    }

    public List<TransactionDto> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionDto> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<RecurringTransactionDto> getUpcomingRecurringPayments() {
        return upcomingRecurringPayments;
    }

    public void setUpcomingRecurringPayments(List<RecurringTransactionDto> upcomingRecurringPayments) {
        this.upcomingRecurringPayments = upcomingRecurringPayments;
    }

    public List<SavingsGoalDto> getSavingsGoals() {
        return savingsGoals;
    }

    public void setSavingsGoals(List<SavingsGoalDto> savingsGoals) {
        this.savingsGoals = savingsGoals;
    }

    public List<BudgetStatusDto> getCurrentBudgets() {
        return currentBudgets;
    }

    public void setCurrentBudgets(List<BudgetStatusDto> currentBudgets) {
        this.currentBudgets = currentBudgets;
    }

    public List<CategorySpendingDto> getTopSpendingCategories() {
        return topSpendingCategories;
    }

    public void setTopSpendingCategories(List<CategorySpendingDto> topSpendingCategories) {
        this.topSpendingCategories = topSpendingCategories;
    }

    public List<FinancialInsightDto> getInsights() {
        return insights;
    }

    public void setInsights(List<FinancialInsightDto> insights) {
        this.insights = insights;
    }
}

