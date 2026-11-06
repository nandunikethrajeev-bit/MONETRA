package com.monetra.desktop.view;

import com.monetra.core.dto.*;
import com.monetra.desktop.chart.ChartFactoryHelper;
import com.monetra.desktop.component.CardPanel;
import com.monetra.desktop.component.ModernTable;
import com.monetra.desktop.component.PrimaryButton;
import com.monetra.desktop.component.StatCard;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView extends JPanel {

    private final StatCard totalBalanceCard;
    private final StatCard monthlyIncomeCard;
    private final StatCard monthlyExpenseCard;
    private final StatCard netSavingsCard;

    private final ModernTable recentTable;
    private final DefaultTableModel recentTableModel;

    private final JPanel insightsContainer;
    private final JPanel chartContainer;

    public DashboardView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(MonetraTheme.BG_MAIN);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // 1. Header with Title and Refresh
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titleLabel = new JLabel("Financial Dashboard");
        titleLabel.setFont(MonetraTheme.FONT_TITLE_LARGE);
        titleLabel.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton refreshBtn = new PrimaryButton("Refresh Data");
        refreshBtn.addActionListener(e -> loadData());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // 2. Metrics Cards Row (4 cards)
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        totalBalanceCard = new StatCard("Total Net Worth", "$0.00", "Across all accounts", MonetraTheme.ACCENT_GOLD);
        monthlyIncomeCard = new StatCard("This Month Income", "$0.00", "Current month earnings", MonetraTheme.COLOR_INCOME);
        monthlyExpenseCard = new StatCard("This Month Expenses", "$0.00", "Current month spending", MonetraTheme.COLOR_EXPENSE);
        netSavingsCard = new StatCard("Net Savings", "$0.00", "0% savings rate", MonetraTheme.COLOR_INFO);

        statsRow.add(totalBalanceCard);
        statsRow.add(monthlyIncomeCard);
        statsRow.add(monthlyExpenseCard);
        statsRow.add(netSavingsCard);

        contentPanel.add(statsRow);
        contentPanel.add(Box.createVerticalStrut(20));

        // 3. Middle Section: Chart on Left, Insights & Goals on Right
        JPanel middleSplit = new JPanel(new GridLayout(1, 2, 16, 0));
        middleSplit.setOpaque(false);
        middleSplit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        // Chart Card
        CardPanel chartCard = new CardPanel(new BorderLayout());
        chartCard.setPreferredSize(new Dimension(500, 300));
        JLabel chartTitle = new JLabel("Spending & Income Overview");
        chartTitle.setFont(MonetraTheme.FONT_TITLE);
        chartTitle.setForeground(MonetraTheme.TEXT_PRIMARY);
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartCard.add(chartContainer, BorderLayout.CENTER);
        middleSplit.add(chartCard);

        // Insights & Goals Tabbed Card
        CardPanel rightCard = new CardPanel(new BorderLayout());
        JLabel rightTitle = new JLabel("Rule-Based Financial Intelligence");
        rightTitle.setFont(MonetraTheme.FONT_TITLE);
        rightTitle.setForeground(MonetraTheme.TEXT_PRIMARY);
        rightCard.add(rightTitle, BorderLayout.NORTH);

        insightsContainer = new JPanel();
        insightsContainer.setLayout(new BoxLayout(insightsContainer, BoxLayout.Y_AXIS));
        insightsContainer.setOpaque(false);
        insightsContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JScrollPane insightsScroll = new JScrollPane(insightsContainer);
        insightsScroll.setOpaque(false);
        insightsScroll.getViewport().setOpaque(false);
        insightsScroll.setBorder(null);
        rightCard.add(insightsScroll, BorderLayout.CENTER);

        middleSplit.add(rightCard);

        contentPanel.add(middleSplit);
        contentPanel.add(Box.createVerticalStrut(20));

        // 4. Bottom Section: Recent Transactions Table
        CardPanel tableCard = new CardPanel(new BorderLayout());
        JLabel tableTitle = new JLabel("Recent Activity");
        tableTitle.setFont(MonetraTheme.FONT_TITLE);
        tableTitle.setForeground(MonetraTheme.TEXT_PRIMARY);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Date", "Description", "Category", "Account", "Type", "Amount"};
        recentTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        recentTable = new ModernTable(recentTableModel);
        JScrollPane tableScroll = new JScrollPane(recentTable);
        tableScroll.setPreferredSize(new Dimension(0, 220));
        tableScroll.setBorder(BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1));
        tableCard.add(tableScroll, BorderLayout.CENTER);

        contentPanel.add(tableCard);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        try {
            FinancialSummaryDto summary = DesktopServiceManager.getInstance()
                    .getFinancialReportService()
                    .getFinancialSummary(userId);

            // Update Metric Cards
            totalBalanceCard.setValue(summary.getTotalBalance());
            monthlyIncomeCard.setValue(summary.getMonthlyIncome());
            monthlyExpenseCard.setValue(summary.getMonthlyExpenses());
            netSavingsCard.setValue(summary.getMonthlySavings());
            netSavingsCard.setSubtitle(String.format("%s%% savings rate", summary.getSavingsRatePercentage()));

            // Update Chart
            chartContainer.removeAll();
            LocalDate now = LocalDate.now();
            List<MonthlyTrendDto> trends = DesktopServiceManager.getInstance()
                    .getFinancialReportService()
                    .getMonthlyTrends(userId, 5);
            JFreeChart chart = ChartFactoryHelper.createIncomeExpenseBarChart(trends);
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setOpaque(false);
            chartPanel.setBackground(MonetraTheme.BG_CARD);
            chartContainer.add(chartPanel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();

            // Update Insights
            insightsContainer.removeAll();
            List<FinancialInsightDto> insights = summary.getInsights();
            for (FinancialInsightDto insight : insights) {
                JPanel card = new JPanel(new BorderLayout(8, 4));
                card.setBackground(new Color(0x1A, 0x1A, 0x1E));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

                JLabel title = new JLabel(insight.getTitle());
                title.setFont(MonetraTheme.FONT_BODY_BOLD);
                if (insight.getType() == FinancialInsightDto.InsightType.POSITIVE) {
                    title.setForeground(MonetraTheme.COLOR_INCOME);
                } else if (insight.getType() == FinancialInsightDto.InsightType.WARNING) {
                    title.setForeground(MonetraTheme.COLOR_WARNING);
                } else if (insight.getType() == FinancialInsightDto.InsightType.DANGER) {
                    title.setForeground(MonetraTheme.COLOR_EXPENSE);
                } else {
                    title.setForeground(MonetraTheme.ACCENT_GOLD);
                }

                JLabel desc = new JLabel("<html>" + insight.getDescription() + "</html>");
                desc.setFont(MonetraTheme.FONT_SMALL);
                desc.setForeground(MonetraTheme.TEXT_SECONDARY);

                card.add(title, BorderLayout.NORTH);
                card.add(desc, BorderLayout.CENTER);

                insightsContainer.add(card);
                insightsContainer.add(Box.createVerticalStrut(6));
            }
            insightsContainer.revalidate();
            insightsContainer.repaint();

            // Update Recent Transactions Table
            recentTableModel.setRowCount(0);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            for (TransactionDto t : summary.getRecentTransactions()) {
                recentTableModel.addRow(new Object[]{
                        t.getTransactionDate() != null ? t.getTransactionDate().format(dtf) : "",
                        t.getDescription(),
                        t.getCategoryName() != null ? t.getCategoryName() : "General",
                        t.getAccountName() != null ? t.getAccountName() : "Account",
                        t.getType().name(),
                        t.getAmount()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error refreshing dashboard: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
