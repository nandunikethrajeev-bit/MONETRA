package com.monetra.desktop.view;

import com.monetra.core.dto.AccountDto;
import com.monetra.core.dto.CategoryDto;
import com.monetra.core.dto.RecurringTransactionCreateDto;
import com.monetra.core.dto.RecurringTransactionDto;
import com.monetra.core.model.RecurrenceFrequency;
import com.monetra.core.model.Transaction;
import com.monetra.core.model.TransactionType;
import com.monetra.desktop.component.ModernTable;
import com.monetra.desktop.component.PrimaryButton;
import com.monetra.desktop.component.SecondaryButton;
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

public class RecurringTransactionsView extends JPanel {

    private final ModernTable table;
    private final DefaultTableModel tableModel;

    public RecurringTransactionsView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Recurring Payments & Subscriptions");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        PrimaryButton processDueBtn = new PrimaryButton("⚡ Process Due Now");
        processDueBtn.addActionListener(e -> processDueNow());

        PrimaryButton newBtn = new PrimaryButton("+ New Recurring Schedule");
        newBtn.addActionListener(e -> showCreateRecurringDialog());

        actions.add(processDueBtn);
        actions.add(newBtn);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(actions, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Description", "Frequency", "Type", "Amount", "Next Due Date", "Last Processed", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new ModernTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1));
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        List<RecurringTransactionDto> list = DesktopServiceManager.getInstance()
                .getRecurringTransactionService().getUserRecurringTransactions(userId);

        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        for (RecurringTransactionDto r : list) {
            tableModel.addRow(new Object[]{
                    r.getId(),
                    r.getDescription(),
                    r.getFrequency().getDisplayName(),
                    r.getType().name(),
                    r.getAmount(),
                    r.getNextDueDate() != null ? r.getNextDueDate().format(dtf) : "",
                    r.getLastProcessedDate() != null ? r.getLastProcessedDate().format(dtf) : "Never",
                    r.isActive() ? "Active" : "Completed"
            });
        }
    }

    private void processDueNow() {
        try {
            List<Transaction> created = DesktopServiceManager.getInstance()
                    .getRecurringTransactionService().processDueTransactions(LocalDate.now());

            if (created.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No recurring payments are currently due for execution.", "Up to Date", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        String.format("Successfully executed and generated %d transaction instance(s) without duplication!", created.size()),
                        "Processed Successfully", JOptionPane.INFORMATION_MESSAGE);
            }
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Execution error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCreateRecurringDialog() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Recurring Rule", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 14));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField descField = new StyledTextField();
        StyledTextField amountField = new StyledTextField();
        JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
        JComboBox<RecurrenceFrequency> freqBox = new JComboBox<>(RecurrenceFrequency.values());
        StyledTextField startDateField = new StyledTextField(LocalDate.now().toString());

        List<AccountDto> accounts = DesktopServiceManager.getInstance().getAccountService().getUserAccounts(userId);
        JComboBox<ComboItem> accBox = new JComboBox<>();
        for (AccountDto a : accounts) accBox.addItem(new ComboItem(a.getId(), a.getName()));

        List<CategoryDto> categories = DesktopServiceManager.getInstance().getCategoryService().getCategoriesForUser(userId);
        JComboBox<ComboItem> catBox = new JComboBox<>();
        for (CategoryDto c : categories) catBox.addItem(new ComboItem(c.getId(), c.getName()));

        form.add(new JLabel("Description:")); form.add(descField);
        form.add(new JLabel("Amount ($):")); form.add(amountField);
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Frequency:")); form.add(freqBox);
        form.add(new JLabel("First Due Date:")); form.add(startDateField);
        form.add(new JLabel("Account:")); form.add(accBox);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton save = new PrimaryButton("Save Rule");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                String desc = descField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                TransactionType type = (TransactionType) typeBox.getSelectedItem();
                RecurrenceFrequency freq = (RecurrenceFrequency) freqBox.getSelectedItem();
                LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
                ComboItem acc = (ComboItem) accBox.getSelectedItem();
                ComboItem cat = (ComboItem) catBox.getSelectedItem();

                if (acc == null || cat == null) {
                    JOptionPane.showMessageDialog(dialog, "Account and Category are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                DesktopServiceManager.getInstance().getRecurringTransactionService().createRecurringTransaction(userId,
                        new RecurringTransactionCreateDto(acc.id, cat.id, amount, type, freq, startDate, null, desc));
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

