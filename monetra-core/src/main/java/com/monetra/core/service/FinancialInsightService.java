package com.monetra.core.service;

import com.monetra.core.dto.CategorySpendingDto;
import com.monetra.core.dto.FinancialInsightDto;
import com.monetra.core.dto.FinancialInsightDto.InsightType;
import com.monetra.core.model.Budget;
import com.monetra.core.repository.BudgetRepository;
import com.monetra.core.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialInsightService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public FinancialInsightService(TransactionRepository transactionRepository, BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<FinancialInsightDto> generateInsights(Long userId) {
        List<FinancialInsightDto> insights = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        LocalDate prevDate = now.minusMonths(1);
        int prevMonth = prevDate.getMonthValue();
        int prevYear = prevDate.getYear();

        BigDecimal curIncome = transactionRepository.calculateMonthlyIncome(userId, currentMonth, currentYear);
        BigDecimal curExpense = transactionRepository.calculateMonthlyExpense(userId, currentMonth, currentYear);
        BigDecimal prevExpense = transactionRepository.calculateMonthlyExpense(userId, prevMonth, prevYear);

        // 1. Month-over-Month Spending Comparison
        if (prevExpense.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = curExpense.subtract(prevExpense);
            BigDecimal percentageChange = diff.multiply(BigDecimal.valueOf(100)).divide(prevExpense, 1, RoundingMode.HALF_UP);

            if (percentageChange.compareTo(BigDecimal.ZERO) < 0) {
                insights.add(new FinancialInsightDto(
                        "Spending Reduced",
                        String.format("Your expenses this month ($%s) are %s%% lower than last month ($%s). You are on track for higher savings!",
                                curExpense.setScale(2, RoundingMode.HALF_UP),
                                percentageChange.abs().toPlainString(),
                                prevExpense.setScale(2, RoundingMode.HALF_UP)),
                        InsightType.POSITIVE,
                        "-" + percentageChange.abs().toPlainString() + "%"
                ));
            } else if (percentageChange.compareTo(BigDecimal.valueOf(15)) > 0) {
                insights.add(new FinancialInsightDto(
                        "Spending Spike Detected",
                        String.format("Your spending this month is %s%% higher than last month. Review your non-essential discretionary expenses.",
                                percentageChange.toPlainString()),
                        InsightType.WARNING,
                        "+" + percentageChange.toPlainString() + "%"
                ));
            } else {
                insights.add(new FinancialInsightDto(
                        "Stable Monthly Spending",
                        String.format("Spending is consistent with last month (%s%% change). Good consistency in your budget discipline.",
                                (percentageChange.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + percentageChange.toPlainString()),
                        InsightType.INFO,
                        percentageChange.toPlainString() + "%"
                ));
            }
        }

        // 2. Savings Rate Insight
        if (curIncome.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal savings = curIncome.subtract(curExpense);
            BigDecimal savingsRate = savings.multiply(BigDecimal.valueOf(100)).divide(curIncome, 1, RoundingMode.HALF_UP);

            if (savingsRate.compareTo(BigDecimal.valueOf(20)) >= 0) {
                insights.add(new FinancialInsightDto(
                        "Strong Savings Rate",
                        String.format("You are currently saving %s%% of your income this month ($%s saved), exceeding the recommended 20%% benchmark.",
                                savingsRate.toPlainString(),
                                savings.setScale(2, RoundingMode.HALF_UP)),
                        InsightType.POSITIVE,
                        savingsRate.toPlainString() + "% Saved"
                ));
            } else if (savingsRate.compareTo(BigDecimal.ZERO) > 0) {
                insights.add(new FinancialInsightDto(
                        "Positive Savings Rate",
                        String.format("You are saving %s%% of your income this month ($%s). Aim to reach 20%% by cutting small discretionary purchases.",
                                savingsRate.toPlainString(),
                                savings.setScale(2, RoundingMode.HALF_UP)),
                        InsightType.INFO,
                        savingsRate.toPlainString() + "% Saved"
                ));
            } else {
                insights.add(new FinancialInsightDto(
                        "Deficit Warning",
                        String.format("Expenses ($%s) exceed income ($%s) by $%s this month. Consider freezing non-essential expenses.",
                                curExpense.setScale(2, RoundingMode.HALF_UP),
                                curIncome.setScale(2, RoundingMode.HALF_UP),
                                curExpense.subtract(curIncome).setScale(2, RoundingMode.HALF_UP)),
                        InsightType.DANGER,
                        "Deficit"
                ));
            }
        }

        // 3. Highest Spending Category
        List<CategorySpendingDto> categoryBreakdown = transactionRepository.getCategorySpendingBreakdown(userId, currentMonth, currentYear);
        if (!categoryBreakdown.isEmpty()) {
            CategorySpendingDto topCat = categoryBreakdown.get(0);
            if (topCat.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                insights.add(new FinancialInsightDto(
                        "Top Expense Category",
                        String.format("%s represents %s%% of all your spending this month ($%s).",
                                topCat.getCategoryName(),
                                topCat.getPercentage().toPlainString(),
                                topCat.getAmount().setScale(2, RoundingMode.HALF_UP)),
                        topCat.getPercentage().compareTo(BigDecimal.valueOf(40)) > 0 ? InsightType.WARNING : InsightType.INFO,
                        topCat.getCategoryName()
                ));
            }
        }

        // 4. Budget Overrun Checks
        List<Budget> budgets = budgetRepository.findByUserIdAndPeriod(userId, currentMonth, currentYear);
        for (Budget b : budgets) {
            BigDecimal spent = b.getCategoryId() != null
                    ? transactionRepository.calculateCategorySpending(userId, b.getCategoryId(), currentMonth, currentYear)
                    : curExpense;

            if (spent.compareTo(b.getAmount()) > 0) {
                insights.add(new FinancialInsightDto(
                        "Budget Overrun: " + b.getName(),
                        String.format("You exceeded your budget of $%s by $%s (Current spent: $%s).",
                                b.getAmount().setScale(2, RoundingMode.HALF_UP),
                                spent.subtract(b.getAmount()).setScale(2, RoundingMode.HALF_UP),
                                spent.setScale(2, RoundingMode.HALF_UP)),
                        InsightType.DANGER,
                        "Exceeded"
                ));
            } else if (b.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usage = spent.multiply(BigDecimal.valueOf(100)).divide(b.getAmount(), 1, RoundingMode.HALF_UP);
                if (usage.compareTo(BigDecimal.valueOf(80)) >= 0) {
                    insights.add(new FinancialInsightDto(
                            "Budget Warning: " + b.getName(),
                            String.format("You have consumed %s%% of your $%s budget limit ($%s remaining).",
                                    usage.toPlainString(),
                                    b.getAmount().setScale(2, RoundingMode.HALF_UP),
                                    b.getAmount().subtract(spent).setScale(2, RoundingMode.HALF_UP)),
                            InsightType.WARNING,
                            usage.toPlainString() + "%"
                    ));
                }
            }
        }

        // If no transactions yet, give a welcome insight
        if (insights.isEmpty()) {
            insights.add(new FinancialInsightDto(
                    "Welcome to MONETRA",
                    "Add your first income and expense transactions to generate personalized rule-based financial insights and analysis.",
                    InsightType.INFO,
                    "Getting Started"
            ));
        }

        return insights;
    }
}

