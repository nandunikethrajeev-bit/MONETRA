package com.monetra.desktop.view;

import com.monetra.core.dto.*;
import com.monetra.core.model.Account;
import com.monetra.core.model.Category;
import com.monetra.core.model.TransactionType;
import com.monetra.desktop.component.*;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionsView extends JPanel {

    private final ModernTable table;
    private final DefaultTableModel tableModel;
    private final List<TransactionDto> currentTransactions = new ArrayList<>();

    private JComboBox<String> typeFilter;
    private JComboBox<CategoryItem> categoryFilter;
    private StyledTextField searchField;

    public TransactionsView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // 1. Header Toolbar Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JLabel title = new JLabel("All Transactions");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);

        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionButtons.setOpaque(false);

        PrimaryButton addIncomeBtn = new PrimaryButton("+ Add Income");
        addIncomeBtn.addActionListener(e -> showTransactionDialog(null, TransactionType.INCOME));

        PrimaryButton addExpenseBtn = new PrimaryButton("+ Add Expense");
        addExpenseBtn.addActionListener(e -> showTransactionDialog(null, TransactionType.EXPENSE));

        SecondaryButton editBtn = new SecondaryButton("Edit Selected");
        editBtn.addActionListener(e -> editSelectedTransaction());

        DangerButton deleteBtn = new DangerButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelectedTransaction());

        actionButtons.add(addIncomeBtn);
        actionButtons.add(addExpenseBtn);
        actionButtons.add(editBtn);
        actionButtons.add(deleteBtn);

        headerRow.add(title, BorderLayout.WEST);
        headerRow.add(actionButtons, BorderLayout.EAST);
        topPanel.add(headerRow);
        topPanel.add(Box.createVerticalStrut(16));

        // 2. Filter Bar
        CardPanel filterBar = new CardPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));

        searchField = new StyledTextField(14);
        searchField.putClientProperty("JTextField.placeholderText", "Search description...");

        typeFilter = new JComboBox<>(new String[]{"All Types", "INCOME", "EXPENSE"});
        typeFilter.setBackground(MonetraTheme.BG_INPUT);
        typeFilter.setForeground(MonetraTheme.TEXT_PRIMARY);

        categoryFilter = new JComboBox<>();
        categoryFilter.setBackground(MonetraTheme.BG_INPUT);
        categoryFilter.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton applyFilterBtn = new PrimaryButton("Filter");
        applyFilterBtn.addActionListener(e -> loadData());

        SecondaryButton clearFilterBtn = new SecondaryButton("Reset");
        clearFilterBtn.addActionListener(e -> {
            searchField.setText("");
            typeFilter.setSelectedIndex(0);
            if (categoryFilter.getItemCount() > 0) categoryFilter.setSelectedIndex(0);
            loadData();
        });

        filterBar.add(new JLabel("Type:"));
        filterBar.add(typeFilter);
        filterBar.add(new JLabel("Category:"));
        filterBar.add(categoryFilter);
        filterBar.add(new JLabel("Search:"));
        filterBar.add(searchField);
        filterBar.add(applyFilterBtn);
        filterBar.add(clearFilterBtn);

        topPanel.add(filterBar);
        add(topPanel, BorderLayout.NORTH);

        // 3. Transactions Table
        String[] columns = {"ID", "Date", "Description", "Category", "Account", "Type", "Amount"};
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

        // Populate category filter dropdown if empty
        if (categoryFilter.getItemCount() == 0) {
            categoryFilter.addItem(new CategoryItem(null, "All Categories"));
            List<CategoryDto> cats = DesktopServiceManager.getInstance().getCategoryService().getCategoriesForUser(userId);
            for (CategoryDto c : cats) {
                categoryFilter.addItem(new CategoryItem(c.getId(), c.getName()));
            }
        }

        TransactionFilterDto filter = new TransactionFilterDto();
        String selectedType = (String) typeFilter.getSelectedItem();
        if ("INCOME".equals(selectedType)) filter.setType(TransactionType.INCOME);
        if ("EXPENSE".equals(selectedType)) filter.setType(TransactionType.EXPENSE);

        CategoryItem catItem = (CategoryItem) categoryFilter.getSelectedItem();
        if (catItem != null && catItem.id != null) {
            filter.setCategoryId(catItem.id);
        }

        String search = searchField.getText().trim();
        if (!search.isEmpty()) {
            filter.setSearchQuery(search);
        }

        currentTransactions.clear();
        currentTransactions.addAll(DesktopServiceManager.getInstance().getTransactionService().getFilteredTransactions(userId, filter));

        tableModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (TransactionDto t : currentTransactions) {
            tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getTransactionDate() != null ? t.getTransactionDate().format(dtf) : "",
                    t.getDescription(),
                    t.getCategoryName() != null ? t.getCategoryName() : "General",
                    t.getAccountName() != null ? t.getAccountName() : "Account",
                    t.getType().name(),
                    t.getAmount()
            });
        }
    }

    private void editSelectedTransaction() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentTransactions.size()) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TransactionDto t = currentTransactions.get(row);
        showTransactionDialog(t, t.getType());
    }

    private void deleteSelectedTransaction() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentTransactions.size()) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TransactionDto t = currentTransactions.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete transaction:\n\"" + t.getDescription() + "\" ($" + t.getAmount() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            Long userId = SessionManager.getInstance().getCurrentUserId();
            DesktopServiceManager.getInstance().getTransactionService().deleteTransaction(t.getId(), userId);
            loadData();
        }
    }

    private void showTransactionDialog(TransactionDto existing, TransactionType defaultType) {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Transaction" : "Edit Transaction", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(440, 480);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form Fields
        StyledTextField descField = new StyledTextField(existing != null ? existing.getDescription() : "");
        StyledTextField amountField = new StyledTextField(existing != null ? existing.getAmount().toPlainString() : "");
        StyledTextField dateField = new StyledTextField(existing != null ? existing.getTransactionDate().toString() : LocalDate.now().toString());

        JComboBox<TransactionType> typeBox = new JComboBox<>(TransactionType.values());
        typeBox.setSelectedItem(existing != null ? existing.getType() : defaultType);

        List<AccountDto> accounts = DesktopServiceManager.getInstance().getAccountService().getUserAccounts(userId);
        JComboBox<AccountItem> accountBox = new JComboBox<>();
        for (AccountDto a : accounts) {
            accountBox.addItem(new AccountItem(a.getId(), a.getName() + " (" + MonetraTheme.formatCurrency(a.getBalance()) + ")"));
            if (existing != null && a.getId().equals(existing.getAccountId())) {
                accountBox.setSelectedIndex(accountBox.getItemCount() - 1);
            }
        }

        List<CategoryDto> categories = DesktopServiceManager.getInstance().getCategoryService().getCategoriesForUser(userId);
        JComboBox<CategoryItem> catBox = new JComboBox<>();
        for (CategoryDto c : categories) {
            catBox.addItem(new CategoryItem(c.getId(), c.getName()));
            if (existing != null && c.getId().equals(existing.getCategoryId())) {
                catBox.setSelectedIndex(catBox.getItemCount() - 1);
            }
        }

        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Amount ($):"));
        form.add(amountField);
        form.add(new JLabel("Type:"));
        form.add(typeBox);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Account:"));
        form.add(accountBox);
        form.add(new JLabel("Category:"));
        form.add(catBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton saveBtn = new PrimaryButton("Save");
        SecondaryButton cancelBtn = new SecondaryButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                String desc = descField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                LocalDate date = LocalDate.parse(dateField.getText().trim());
                TransactionType type = (TransactionType) typeBox.getSelectedItem();
                AccountItem acc = (AccountItem) accountBox.getSelectedItem();
                CategoryItem cat = (CategoryItem) catBox.getSelectedItem();

                if (acc == null || cat == null) {
                    JOptionPane.showMessageDialog(dialog, "Account and Category are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TransactionCreateDto dto = new TransactionCreateDto(acc.id, cat.id, amount, type, date, desc, null);

                if (existing == null) {
                    DesktopServiceManager.getInstance().getTransactionService().createTransaction(userId, dto);
                } else {
                    DesktopServiceManager.getInstance().getTransactionService().updateTransaction(existing.getId(), userId, dto);
                }

                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Failed to save: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
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

