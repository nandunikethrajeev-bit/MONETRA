package com.monetra.desktop.view;

import com.monetra.core.dto.BudgetCreateDto;
import com.monetra.core.dto.BudgetStatusDto;
import com.monetra.core.dto.CategoryDto;
import com.monetra.core.model.BudgetStatus;
import com.monetra.desktop.component.CardPanel;
import com.monetra.desktop.component.PrimaryButton;
import com.monetra.desktop.component.SecondaryButton;
import com.monetra.desktop.component.StyledTextField;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class BudgetsView extends JPanel {

    private final JPanel budgetCardsPanel;
    private final JLabel headerSummaryLabel;

    public BudgetsView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);

        JLabel title = new JLabel("Budget Management");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        headerSummaryLabel = new JLabel("Tracking monthly budget limits and consumption");
        headerSummaryLabel.setFont(MonetraTheme.FONT_BODY);
        headerSummaryLabel.setForeground(MonetraTheme.TEXT_MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(4));
        titles.add(headerSummaryLabel);

        PrimaryButton createBtn = new PrimaryButton("+ Create New Budget");
        createBtn.addActionListener(e -> showCreateBudgetDialog());

        topPanel.add(titles, BorderLayout.WEST);
        topPanel.add(createBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Budget Cards Grid
        budgetCardsPanel = new JPanel();
        budgetCardsPanel.setLayout(new BoxLayout(budgetCardsPanel, BoxLayout.Y_AXIS));
        budgetCardsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(budgetCardsPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        LocalDate now = LocalDate.now();
        List<BudgetStatusDto> budgets = DesktopServiceManager.getInstance().getBudgetService()
                .getBudgetStatusesForPeriod(userId, now.getMonthValue(), now.getYear());

        budgetCardsPanel.removeAll();

        if (budgets.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLabel = new JLabel("No budgets set for this month. Click '+ Create New Budget' above to start tracking.");
            emptyLabel.setFont(MonetraTheme.FONT_BODY);
            emptyLabel.setForeground(MonetraTheme.TEXT_MUTED);
            emptyPanel.add(emptyLabel);
            budgetCardsPanel.add(emptyPanel);
        } else {
            for (BudgetStatusDto b : budgets) {
                budgetCardsPanel.add(createBudgetCard(b));
                budgetCardsPanel.add(Box.createVerticalStrut(14));
            }
        }

        budgetCardsPanel.revalidate();
        budgetCardsPanel.repaint();
    }

    private JPanel createBudgetCard(BudgetStatusDto b) {
        CardPanel card = new CardPanel(new BorderLayout(16, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Top info: Name, Scope, Status Badge
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(b.getName() + (b.getCategoryName() != null ? " (" + b.getCategoryName() + ")" : " (Overall Month)"));
        nameLabel.setFont(MonetraTheme.FONT_TITLE);
        nameLabel.setForeground(MonetraTheme.TEXT_PRIMARY);

        JLabel statusBadge = new JLabel(" " + b.getStatus().name() + " ");
        statusBadge.setFont(MonetraTheme.FONT_SMALL);
        statusBadge.setOpaque(true);
        if (b.getStatus() == BudgetStatus.OK) {
            statusBadge.setBackground(new Color(0x13, 0x4E, 0x30));
            statusBadge.setForeground(MonetraTheme.COLOR_INCOME);
        } else if (b.getStatus() == BudgetStatus.WARNING) {
            statusBadge.setBackground(new Color(0x54, 0x38, 0x05));
            statusBadge.setForeground(MonetraTheme.COLOR_WARNING);
        } else {
            statusBadge.setBackground(new Color(0x5C, 0x18, 0x18));
            statusBadge.setForeground(MonetraTheme.COLOR_EXPENSE);
        }

        top.add(nameLabel, BorderLayout.WEST);
        top.add(statusBadge, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Center: Progress Bar
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JProgressBar pb = new JProgressBar(0, 100);
        int percentInt = Math.min(100, b.getPercentageUsed().setScale(0, RoundingMode.HALF_UP).intValue());
        pb.setValue(percentInt);
        pb.setPreferredSize(new Dimension(0, 10));
        pb.setForeground(b.getStatus() == BudgetStatus.EXCEEDED ? MonetraTheme.COLOR_EXPENSE
                : b.getStatus() == BudgetStatus.WARNING ? MonetraTheme.COLOR_WARNING : MonetraTheme.ACCENT_GOLD);
        pb.setBackground(new Color(0x1C, 0x1C, 0x22));
        pb.setBorder(null);

        center.add(pb);
        center.add(Box.createVerticalStrut(8));

        // Bottom figures: Spent vs Limit and Remaining
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel spentInfo = new JLabel("Spent: " + MonetraTheme.formatCurrency(b.getSpentAmount()) + " of " + MonetraTheme.formatCurrency(b.getBudgetAmount())
                + " (" + b.getPercentageUsed() + "%)");
        spentInfo.setFont(MonetraTheme.FONT_BODY);
        spentInfo.setForeground(MonetraTheme.TEXT_SECONDARY);

        JLabel remainingInfo = new JLabel(b.getStatus() == BudgetStatus.EXCEEDED
                ? "Over limit: " + MonetraTheme.formatCurrency(b.getSpentAmount().subtract(b.getBudgetAmount()))
                : "Remaining: " + MonetraTheme.formatCurrency(b.getRemainingAmount()));
        remainingInfo.setFont(MonetraTheme.FONT_BODY_BOLD);
        remainingInfo.setForeground(b.getStatus() == BudgetStatus.EXCEEDED ? MonetraTheme.COLOR_EXPENSE : MonetraTheme.COLOR_INCOME);

        bottom.add(spentInfo, BorderLayout.WEST);
        bottom.add(remainingInfo, BorderLayout.EAST);
        center.add(bottom);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private void showCreateBudgetDialog() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Budget", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 14));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField nameField = new StyledTextField();
        StyledTextField amountField = new StyledTextField();

        List<CategoryDto> cats = DesktopServiceManager.getInstance().getCategoryService().getCategoriesForUser(userId);
        JComboBox<Object> catBox = new JComboBox<>();
        catBox.addItem(new Object(){
            public String toString() { return "Overall Monthly (All Categories)"; }
        });
        for (CategoryDto c : cats) {
            catBox.addItem(new CategoryItem(c.getId(), c.getName()));
        }

        LocalDate now = LocalDate.now();
        StyledTextField monthField = new StyledTextField(String.valueOf(now.getMonthValue()));

        form.add(new JLabel("Budget Name:")); form.add(nameField);
        form.add(new JLabel("Amount ($):")); form.add(amountField);
        form.add(new JLabel("Category:")); form.add(catBox);
        form.add(new JLabel("Month (1-12):")); form.add(monthField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton save = new PrimaryButton("Save");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                int month = Integer.parseInt(monthField.getText().trim());
                int year = now.getYear();

                Long catId = null;
                Object selected = catBox.getSelectedItem();
                if (selected instanceof CategoryItem ci) {
                    catId = ci.id;
                }

                DesktopServiceManager.getInstance().getBudgetService().createBudget(userId,
                        new BudgetCreateDto(catId, name, amount, month, year));
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(cancel);
        btnPanel.add(save);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static class CategoryItem {
        final Long id;
        final String name;

        CategoryItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
