package com.monetra.desktop.view;

import com.monetra.core.dto.AccountDto;
import com.monetra.core.dto.CategoryDto;
import com.monetra.core.dto.TransactionCreateDto;
import com.monetra.core.dto.TransactionDto;
import com.monetra.core.dto.TransactionFilterDto;
import com.monetra.core.model.TransactionType;
import com.monetra.desktop.component.CardPanel;
import com.monetra.desktop.component.ModernTable;
import com.monetra.desktop.component.PrimaryButton;
import com.monetra.desktop.component.SecondaryButton;
import com.monetra.desktop.component.StatCard;
import com.monetra.desktop.component.StyledTextField;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IncomeView extends JPanel {

    private final ModernTable table;
    private final DefaultTableModel tableModel;
    private final StatCard totalIncomeCard;
    private final StatCard monthlyIncomeCard;

    public IncomeView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // Header Title
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JLabel title = new JLabel("Income Management");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton addBtn = new PrimaryButton("+ Record New Income");
        addBtn.addActionListener(e -> showAddIncomeDialog());
        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(addBtn, BorderLayout.EAST);
        topPanel.add(headerRow);
        topPanel.add(Box.createVerticalStrut(16));

        // Income Stat Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        totalIncomeCard = new StatCard("All-Time Total Income", "$0.00", "Total earnings recorded", MonetraTheme.COLOR_INCOME);
        monthlyIncomeCard = new StatCard("Current Month Income", "$0.00", "Earnings this month", MonetraTheme.COLOR_INCOME);
        statsRow.add(totalIncomeCard);
        statsRow.add(monthlyIncomeCard);
        topPanel.add(statsRow);
        topPanel.add(Box.createVerticalStrut(16));

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Date", "Description", "Source / Category", "Account", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new ModernTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        LocalDate now = LocalDate.now();
        BigDecimal total = DesktopServiceManager.getInstance().getFinancialReportService().getFinancialSummary(userId).getTotalIncome();
        BigDecimal monthly = DesktopServiceManager.getInstance().getTransactionService().getFilteredTransactions(userId,
                new TransactionFilterDto(null, null, TransactionType.INCOME, now.withDayOfMonth(1), now, null))
                .stream().map(TransactionDto::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        totalIncomeCard.setValue(total);
        monthlyIncomeCard.setValue(monthly);

        TransactionFilterDto filter = new TransactionFilterDto();
        filter.setType(TransactionType.INCOME);
        List<TransactionDto> list = DesktopServiceManager.getInstance().getTransactionService().getFilteredTransactions(userId, filter);

        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for (TransactionDto t : list) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTransactionDate() != null ? t.getTransactionDate().format(dtf) : "",
                    t.getDescription(),
                    t.getCategoryName() != null ? t.getCategoryName() : "Income",
                    t.getAccountName() != null ? t.getAccountName() : "Account",
                    t.getAmount()
            });
        }
    }

    private void showAddIncomeDialog() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Record Income", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField descField = new StyledTextField();
        StyledTextField amountField = new StyledTextField();
        StyledTextField dateField = new StyledTextField(LocalDate.now().toString());

        List<AccountDto> accounts = DesktopServiceManager.getInstance().getAccountService().getUserAccounts(userId);
        JComboBox<ComboItem> accBox = new JComboBox<>();
        for (AccountDto a : accounts) {
            accBox.addItem(new ComboItem(a.getId(), a.getName()));
        }

        List<CategoryDto> categories = DesktopServiceManager.getInstance().getCategoryService().getCategoriesForUser(userId);
        JComboBox<ComboItem> catBox = new JComboBox<>();
        for (CategoryDto c : categories) {
            if (c.getType() == TransactionType.INCOME) {
                catBox.addItem(new ComboItem(c.getId(), c.getName()));
            }
        }
        if (catBox.getItemCount() == 0) {
            for (CategoryDto c : categories) {
                catBox.addItem(new ComboItem(c.getId(), c.getName()));
            }
        }

        form.add(new JLabel("Description:")); form.add(descField);
        form.add(new JLabel("Amount ($):")); form.add(amountField);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);
        form.add(new JLabel("Account:")); form.add(accBox);
        form.add(new JLabel("Category:")); form.add(catBox);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton save = new PrimaryButton("Save");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                BigDecimal amt = new BigDecimal(amountField.getText().trim());
                LocalDate d = LocalDate.parse(dateField.getText().trim());
                String desc = descField.getText().trim();
                ComboItem acc = (ComboItem) accBox.getSelectedItem();
                ComboItem cat = (ComboItem) catBox.getSelectedItem();

                if (acc == null || cat == null) {
                    JOptionPane.showMessageDialog(dialog, "Account and Category are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                DesktopServiceManager.getInstance().getTransactionService().createTransaction(userId,
                        new TransactionCreateDto(acc.id, cat.id, amt, TransactionType.INCOME, d, desc, null));
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

    private static class ComboItem {
        final Long id;
        final String name;

        ComboItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
