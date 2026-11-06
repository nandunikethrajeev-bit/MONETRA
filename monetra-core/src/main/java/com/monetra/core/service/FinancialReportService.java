package com.monetra.core.service;

import com.monetra.core.dto.*;
import com.monetra.core.model.RecurringTransaction;
import com.monetra.core.repository.AccountRepository;
import com.monetra.core.repository.RecurringTransactionRepository;
import com.monetra.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialReportService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final BudgetService budgetService;
    private final SavingsGoalService savingsGoalService;
    private final FinancialInsightService financialInsightService;

    public FinancialReportService(AccountRepository accountRepository,
                                  TransactionRepository transactionRepository,
                                  RecurringTransactionRepository recurringTransactionRepository,
                                  BudgetService budgetService,
                                  SavingsGoalService savingsGoalService,
                                  FinancialInsightService financialInsightService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.budgetService = budgetService;
        this.savingsGoalService = savingsGoalService;
        this.financialInsightService = financialInsightService;
    }

    public FinancialSummaryDto getFinancialSummary(Long userId) {
        LocalDate now = LocalDate.now();
        return getFinancialSummary(userId, now.getMonthValue(), now.getYear());
    }

    public FinancialSummaryDto getFinancialSummary(Long userId, int month, int year) {
        FinancialSummaryDto summary = new FinancialSummaryDto();
        LocalDate now = LocalDate.now();

        // Total Net Balance across accounts
        BigDecimal totalBalance = accountRepository.findByUserId(userId)
                .stream()
                .map(a -> a.getBalance() != null ? a.getBalance() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalBalance(totalBalance);

        // All-Time Totals
        BigDecimal totalIncome = transactionRepository.calculateTotalIncome(userId);
        BigDecimal totalExpenses = transactionRepository.calculateTotalExpense(userId);
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpenses(totalExpenses);
        summary.setTotalSavings(totalIncome.subtract(totalExpenses));

        // Current Month Totals
        BigDecimal monthlyIncome = transactionRepository.calculateMonthlyIncome(userId, month, year);
        BigDecimal monthlyExpenses = transactionRepository.calculateMonthlyExpense(userId, month, year);
        summary.setMonthlyIncome(monthlyIncome);
        summary.setMonthlyExpenses(monthlyExpenses);
        summary.setMonthlySavings(monthlyIncome.subtract(monthlyExpenses));

        // Savings Rate
        if (monthlyIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rate = monthlyIncome.subtract(monthlyExpenses)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(monthlyIncome, 2, RoundingMode.HALF_UP);
            summary.setSavingsRatePercentage(rate);
        } else {
            summary.setSavingsRatePercentage(BigDecimal.ZERO);
        }

        // Current Month Budgets & Utilization
        List<BudgetStatusDto> budgets = budgetService.getBudgetStatusesForPeriod(userId, month, year);
        summary.setCurrentBudgets(budgets);

        BigDecimal totalBudgetLimit = budgets.stream()
                .map(BudgetStatusDto::getBudgetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalBudgetSpent = budgets.stream()
                .map(BudgetStatusDto::getSpentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBudgetLimit.compareTo(BigDecimal.ZERO) > 0) {
            summary.setOverallBudgetUtilizationPercentage(
                    totalBudgetSpent.multiply(BigDecimal.valueOf(100)).divide(totalBudgetLimit, 2, RoundingMode.HALF_UP)
            );
        } else {
            summary.setOverallBudgetUtilizationPercentage(BigDecimal.ZERO);
        }

        // Recent Transactions (latest 8)
        List<TransactionDto> recent = transactionRepository.findRecent(userId, 8)
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
        summary.setRecentTransactions(recent);

        // Upcoming Recurring Payments within 30 days
        LocalDate in30Days = now.plusDays(30);
        List<RecurringTransactionDto> upcoming = recurringTransactionRepository.findByUserId(userId)
                .stream()
                .filter(RecurringTransaction::isActive)
                .filter(r -> !r.getNextDueDate().isBefore(now) && !r.getNextDueDate().isAfter(in30Days))
                .map(RecurringTransactionDto::fromEntity)
                .collect(Collectors.toList());
        summary.setUpcomingRecurringPayments(upcoming);

        // Savings Goals
        summary.setSavingsGoals(savingsGoalService.getUserSavingsGoals(userId));

        // Top Spending Categories
        summary.setTopSpendingCategories(transactionRepository.getCategorySpendingBreakdown(userId, month, year));

        // Financial Insights
        summary.setInsights(financialInsightService.generateInsights(userId));

        return summary;
    }

    public List<CategorySpendingDto> getCategorySpending(Long userId, int month, int year) {
        return transactionRepository.getCategorySpendingBreakdown(userId, month, year);
    }

    public List<MonthlyTrendDto> getMonthlyTrends(Long userId, int months) {
        return transactionRepository.getMonthlyTrends(userId, months > 0 ? months : 6);
    }
}

