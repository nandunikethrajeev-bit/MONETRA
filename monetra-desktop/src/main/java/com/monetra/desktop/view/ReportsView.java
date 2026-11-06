package com.monetra.desktop.view;

import com.monetra.core.dto.*;
import com.monetra.desktop.chart.ChartFactoryHelper;
import com.monetra.desktop.component.CardPanel;
import com.monetra.desktop.component.PrimaryButton;
import com.monetra.desktop.component.StyledTextField;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReportsView extends JPanel {

    private final JPanel barChartContainer;
    private final JPanel pieChartContainer;
    private final JPanel lineChartContainer;
    private final JPanel budgetChartContainer;

    // What-If Calculator Fields
    private StyledTextField targetAmountField;
    private StyledTextField durationField;
    private StyledTextField initialDepositField;
    private StyledTextField interestRateField;
    private JLabel requiredMonthlyResult;
    private JLabel totalPrincipalResult;
    private JLabel totalInterestResult;

    public ReportsView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(MonetraTheme.BG_MAIN);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Reports, Analytics & What-If Calculator");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton refreshBtn = new PrimaryButton("Refresh Charts");
        refreshBtn.addActionListener(e -> loadData());
        header.add(title, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        content.add(header);
        content.add(Box.createVerticalStrut(20));

        // 1. Charts Grid (2x2)
        JPanel chartsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        chartsGrid.setOpaque(false);
        chartsGrid.setPreferredSize(new Dimension(900, 640));

        barChartContainer = createChartHolder("Monthly Income vs. Expenses");
        pieChartContainer = createChartHolder("Category Spending Breakdown");
        lineChartContainer = createChartHolder("Savings Growth Trend");
        budgetChartContainer = createChartHolder("Budget Utilization");

        chartsGrid.add(barChartContainer);
        chartsGrid.add(pieChartContainer);
        chartsGrid.add(lineChartContainer);
        chartsGrid.add(budgetChartContainer);

        content.add(chartsGrid);
        content.add(Box.createVerticalStrut(24));

        // 2. What-If Savings Calculator Section
        CardPanel calcCard = new CardPanel(new BorderLayout(16, 16));
        calcCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel calcTitle = new JLabel("What-If Savings Goal Simulator");
        calcTitle.setFont(MonetraTheme.FONT_TITLE);
        calcTitle.setForeground(MonetraTheme.ACCENT_GOLD);
        calcCard.add(calcTitle, BorderLayout.NORTH);

        JPanel calcBody = new JPanel(new GridLayout(1, 2, 24, 0));
        calcBody.setOpaque(false);

        // Inputs Panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 8, 10));
        inputPanel.setOpaque(false);

        targetAmountField = new StyledTextField("10000.00");
        durationField = new StyledTextField("12");
        initialDepositField = new StyledTextField("1000.00");
        interestRateField = new StyledTextField("4.5");

        inputPanel.add(new JLabel("Financial Target ($):")); inputPanel.add(targetAmountField);
        inputPanel.add(new JLabel("Duration (Months):")); inputPanel.add(durationField);
        inputPanel.add(new JLabel("Initial Deposit ($):")); inputPanel.add(initialDepositField);
        inputPanel.add(new JLabel("Annual Interest Rate (%):")); inputPanel.add(interestRateField);
        calcBody.add(inputPanel);

        // Results Panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        PrimaryButton calculateBtn = new PrimaryButton("Calculate Required Savings");
        calculateBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateBtn.addActionListener(e -> performWhatIfCalculation());

        requiredMonthlyResult = new JLabel("Required Monthly Savings: $0.00");
        requiredMonthlyResult.setFont(MonetraTheme.FONT_TITLE);
        requiredMonthlyResult.setForeground(MonetraTheme.ACCENT_GOLD);

        totalPrincipalResult = new JLabel("Total Contributions: $0.00");
        totalPrincipalResult.setFont(MonetraTheme.FONT_BODY);
        totalPrincipalResult.setForeground(MonetraTheme.TEXT_SECONDARY);

        totalInterestResult = new JLabel("Estimated Interest Earned: $0.00");
        totalInterestResult.setFont(MonetraTheme.FONT_BODY);
        totalInterestResult.setForeground(MonetraTheme.COLOR_INCOME);

        resultsPanel.add(calculateBtn);
        resultsPanel.add(Box.createVerticalStrut(12));
        resultsPanel.add(requiredMonthlyResult);
        resultsPanel.add(Box.createVerticalStrut(6));
        resultsPanel.add(totalPrincipalResult);
        resultsPanel.add(Box.createVerticalStrut(6));
        resultsPanel.add(totalInterestResult);

        calcBody.add(resultsPanel);
        calcCard.add(calcBody, BorderLayout.CENTER);
        content.add(calcCard);

        JScrollPane mainScroll = new JScrollPane(content);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createChartHolder(String title) {
        CardPanel card = new CardPanel(new BorderLayout());
        JLabel t = new JLabel(title);
        t.setFont(MonetraTheme.FONT_TITLE);
        t.setForeground(MonetraTheme.TEXT_PRIMARY);
        t.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        card.add(t, BorderLayout.NORTH);
        return card;
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        // 1. Bar Chart: Income vs Expense
        List<MonthlyTrendDto> trends = DesktopServiceManager.getInstance()
                .getFinancialReportService().getMonthlyTrends(userId, 6);
        updateChartContainer(barChartContainer, ChartFactoryHelper.createIncomeExpenseBarChart(trends));

        // 2. Pie Chart: Category Spending
        List<CategorySpendingDto> categories = DesktopServiceManager.getInstance()
                .getFinancialReportService().getCategorySpending(userId, month, year);
        updateChartContainer(pieChartContainer, ChartFactoryHelper.createCategoryPieChart(categories));

        // 3. Line Chart: Savings Trend
        updateChartContainer(lineChartContainer, ChartFactoryHelper.createSavingsTrendLineChart(trends));

        // 4. Budget Utilization
        List<BudgetStatusDto> budgets = DesktopServiceManager.getInstance()
                .getBudgetService().getBudgetStatusesForPeriod(userId, month, year);
        updateChartContainer(budgetChartContainer, ChartFactoryHelper.createBudgetUtilizationBarChart(budgets));

        // Initial Calculation
        performWhatIfCalculation();
    }

    private void updateChartContainer(JPanel container, JFreeChart chart) {
        // Keep header title at NORTH, replace CENTER
        BorderLayout layout = (BorderLayout) container.getLayout();
        Component oldCenter = layout.getLayoutComponent(BorderLayout.CENTER);
        if (oldCenter != null) container.remove(oldCenter);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(MonetraTheme.BG_CARD);
        container.add(chartPanel, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }

    private void performWhatIfCalculation() {
        try {
            BigDecimal target = new BigDecimal(targetAmountField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());
            BigDecimal initial = new BigDecimal(initialDepositField.getText().trim());
            BigDecimal rate = new BigDecimal(interestRateField.getText().trim());

            WhatIfSavingsCalculationRequest req = new WhatIfSavingsCalculationRequest(
                    "Goal Simulation", target, duration, initial, rate);

            WhatIfSavingsCalculationResult res = DesktopServiceManager.getInstance()
                    .getSavingsCalculatorService().calculateRequiredSavings(req);

            requiredMonthlyResult.setText("Required Monthly Savings: " + MonetraTheme.formatCurrency(res.getRequiredMonthlySavings()));
            totalPrincipalResult.setText("Total Contributions: " + MonetraTheme.formatCurrency(res.getTotalPrincipalContributed()));
            totalInterestResult.setText("Estimated Interest Earned: " + MonetraTheme.formatCurrency(res.getTotalInterestEarned()));
        } catch (Exception ex) {
            requiredMonthlyResult.setText("Invalid calculation input: " + ex.getMessage());
        }
    }
}
