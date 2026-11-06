package com.monetra.desktop.view;

import com.monetra.core.dto.AccountDto;
import com.monetra.core.dto.SavingsContributionDto;
import com.monetra.core.dto.SavingsGoalCreateDto;
import com.monetra.core.dto.SavingsGoalDto;
import com.monetra.core.model.GoalStatus;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SavingsGoalsView extends JPanel {

    private final JPanel goalsContainer;

    public SavingsGoalsView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Savings Goals");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton newGoalBtn = new PrimaryButton("+ New Savings Goal");
        newGoalBtn.addActionListener(e -> showCreateGoalDialog());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(newGoalBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Goals Container
        goalsContainer = new JPanel();
        goalsContainer.setLayout(new BoxLayout(goalsContainer, BoxLayout.Y_AXIS));
        goalsContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(goalsContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        List<SavingsGoalDto> goals = DesktopServiceManager.getInstance().getSavingsGoalService().getUserSavingsGoals(userId);
        goalsContainer.removeAll();

        if (goals.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(false);
            JLabel lbl = new JLabel("No savings goals yet. Click '+ New Savings Goal' to plan a vacation, emergency fund, or car!");
            lbl.setFont(MonetraTheme.FONT_BODY);
            lbl.setForeground(MonetraTheme.TEXT_MUTED);
            empty.add(lbl);
            goalsContainer.add(empty);
        } else {
            for (SavingsGoalDto g : goals) {
                goalsContainer.add(createGoalCard(g));
                goalsContainer.add(Box.createVerticalStrut(14));
            }
        }

        goalsContainer.revalidate();
        goalsContainer.repaint();
    }

    private JPanel createGoalCard(SavingsGoalDto g) {
        CardPanel card = new CardPanel(new BorderLayout(16, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        // Top Row: Goal Name, Target Date, Status Badge
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(g.getName());
        nameLabel.setFont(MonetraTheme.FONT_TITLE);
        nameLabel.setForeground(MonetraTheme.TEXT_PRIMARY);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        JLabel dateLabel = new JLabel("Target: " + (g.getTargetDate() != null ? g.getTargetDate().format(dtf) : "None"));
        dateLabel.setFont(MonetraTheme.FONT_SMALL);
        dateLabel.setForeground(MonetraTheme.TEXT_MUTED);

        JLabel statusBadge = new JLabel(" " + g.getStatus().name() + " ");
        statusBadge.setFont(MonetraTheme.FONT_SMALL);
        statusBadge.setOpaque(true);
        if (g.getStatus() == GoalStatus.COMPLETED) {
            statusBadge.setBackground(new Color(0x13, 0x4E, 0x30));
            statusBadge.setForeground(MonetraTheme.COLOR_INCOME);
        } else {
            statusBadge.setBackground(MonetraTheme.ACCENT_GOLD_MUTED);
            statusBadge.setForeground(MonetraTheme.ACCENT_GOLD);
        }

        rightTop.add(dateLabel);
        rightTop.add(statusBadge);
        top.add(nameLabel, BorderLayout.WEST);
        top.add(rightTop, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        // Center: Progress Bar
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        JProgressBar pb = new JProgressBar(0, 100);
        int percentInt = Math.min(100, g.getProgressPercentage().setScale(0, RoundingMode.HALF_UP).intValue());
        pb.setValue(percentInt);
        pb.setPreferredSize(new Dimension(0, 10));
        pb.setForeground(g.getStatus() == GoalStatus.COMPLETED ? MonetraTheme.COLOR_INCOME : MonetraTheme.ACCENT_GOLD);
        pb.setBackground(new Color(0x1C, 0x1C, 0x22));
        pb.setBorder(null);

        center.add(pb);
        center.add(Box.createVerticalStrut(8));

        // Bottom Figures & Contribute Button
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel info = new JLabel(MonetraTheme.formatCurrency(g.getCurrentAmount()) + " saved of " + MonetraTheme.formatCurrency(g.getTargetAmount())
                + " (" + g.getProgressPercentage() + "%) • Remaining: " + MonetraTheme.formatCurrency(g.getRemainingAmount()));
        info.setFont(MonetraTheme.FONT_BODY);
        info.setForeground(MonetraTheme.TEXT_SECONDARY);

        PrimaryButton contributeBtn = new PrimaryButton("+ Deposit Funds");
        contributeBtn.setFont(MonetraTheme.FONT_SMALL);
        contributeBtn.addActionListener(e -> showContributeDialog(g));

        bottom.add(info, BorderLayout.WEST);
        if (g.getStatus() != GoalStatus.COMPLETED) {
            bottom.add(contributeBtn, BorderLayout.EAST);
        }
        center.add(bottom);

        card.add(center, BorderLayout.CENTER);
        return card;
    }

    private void showCreateGoalDialog() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Savings Goal", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 14));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField nameField = new StyledTextField();
        StyledTextField targetField = new StyledTextField();
        StyledTextField initialField = new StyledTextField("0.00");
        StyledTextField dateField = new StyledTextField(LocalDate.now().plusMonths(6).toString());

        form.add(new JLabel("Goal Name:")); form.add(nameField);
        form.add(new JLabel("Target Amount ($):")); form.add(targetField);
        form.add(new JLabel("Initial Deposit ($):")); form.add(initialField);
        form.add(new JLabel("Target Date (YYYY-MM-DD):")); form.add(dateField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton save = new PrimaryButton("Create Goal");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                BigDecimal target = new BigDecimal(targetField.getText().trim());
                BigDecimal initial = new BigDecimal(initialField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());

                DesktopServiceManager.getInstance().getSavingsGoalService().createSavingsGoal(userId,
                        new SavingsGoalCreateDto(null, name, target, initial, date, null));
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

    private void showContributeDialog(SavingsGoalDto goal) {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Deposit into " + goal.getName(), true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 14));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField amountField = new StyledTextField();
        List<AccountDto> accounts = DesktopServiceManager.getInstance().getAccountService().getUserAccounts(userId);
        JComboBox<AccountItem> accBox = new JComboBox<>();
        for (AccountDto a : accounts) {
            accBox.addItem(new AccountItem(a.getId(), a.getName() + " (" + MonetraTheme.formatCurrency(a.getBalance()) + ")"));
        }

        form.add(new JLabel("Contribution Amount ($):")); form.add(amountField);
        form.add(new JLabel("Source Account:")); form.add(accBox);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton depositBtn = new PrimaryButton("Confirm Deposit");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        depositBtn.addActionListener(e -> {
            try {
                BigDecimal amt = new BigDecimal(amountField.getText().trim());
                AccountItem acc = (AccountItem) accBox.getSelectedItem();
                Long accId = acc != null ? acc.id : null;

                DesktopServiceManager.getInstance().getSavingsGoalService().recordContribution(goal.getId(), userId,
                        new SavingsContributionDto(amt, accId, "Goal contribution"));
                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(cancel);
        btnPanel.add(depositBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static class AccountItem {
        final Long id;
        final String name;

        AccountItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
