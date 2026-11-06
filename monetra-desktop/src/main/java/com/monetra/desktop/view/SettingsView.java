package com.monetra.desktop.view;

import com.monetra.core.dto.AccountCreateDto;
import com.monetra.core.dto.AccountDto;
import com.monetra.core.dto.UserDto;
import com.monetra.core.model.AccountType;
import com.monetra.desktop.component.*;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class SettingsView extends JPanel {

    private final StyledTextField fullNameField;
    private final StyledTextField emailField;
    private final JComboBox<String> currencyBox;
    private final JLabel usernameDisplay;

    private final ModernTable accountTable;
    private final DefaultTableModel accountTableModel;

    public SettingsView() {
        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(MonetraTheme.BG_MAIN);
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Header
        JLabel title = new JLabel("System Settings & Accounts");
        title.setFont(MonetraTheme.FONT_TITLE_LARGE);
        title.setForeground(MonetraTheme.TEXT_PRIMARY);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // 1. Profile Section
        CardPanel profileCard = new CardPanel(new BorderLayout(16, 16));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel profTitle = new JLabel("User Profile Settings");
        profTitle.setFont(MonetraTheme.FONT_TITLE);
        profTitle.setForeground(MonetraTheme.TEXT_PRIMARY);
        profileCard.add(profTitle, BorderLayout.NORTH);

        JPanel profForm = new JPanel(new GridLayout(4, 2, 12, 12));
        profForm.setOpaque(false);

        usernameDisplay = new JLabel();
        usernameDisplay.setFont(MonetraTheme.FONT_BODY_BOLD);
        usernameDisplay.setForeground(MonetraTheme.ACCENT_GOLD);

        fullNameField = new StyledTextField();
        emailField = new StyledTextField();
        currencyBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "INR", "CAD", "AUD", "JPY"});

        profForm.add(new JLabel("Username (Read-Only):")); profForm.add(usernameDisplay);
        profForm.add(new JLabel("Full Name:")); profForm.add(fullNameField);
        profForm.add(new JLabel("Email Address:")); profForm.add(emailField);
        profForm.add(new JLabel("Default Currency:")); profForm.add(currencyBox);
        profileCard.add(profForm, BorderLayout.CENTER);

        JPanel profActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profActions.setOpaque(false);
        PrimaryButton saveProfBtn = new PrimaryButton("Save Profile Changes");
        saveProfBtn.addActionListener(e -> saveProfile());
        profActions.add(saveProfBtn);
        profileCard.add(profActions, BorderLayout.SOUTH);

        content.add(profileCard);
        content.add(Box.createVerticalStrut(20));

        // 2. Financial Accounts Section
        CardPanel accountsCard = new CardPanel(new BorderLayout(16, 14));
        accountsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JPanel accHeader = new JPanel(new BorderLayout());
        accHeader.setOpaque(false);
        JLabel accTitle = new JLabel("Managed Financial Accounts");
        accTitle.setFont(MonetraTheme.FONT_TITLE);
        accTitle.setForeground(MonetraTheme.TEXT_PRIMARY);

        PrimaryButton addAccBtn = new PrimaryButton("+ Add Financial Account");
        addAccBtn.addActionListener(e -> showAddAccountDialog());
        accHeader.add(accTitle, BorderLayout.WEST);
        accHeader.add(addAccBtn, BorderLayout.EAST);
        accountsCard.add(accHeader, BorderLayout.NORTH);

        String[] cols = {"ID", "Account Name", "Type", "Balance", "Currency", "Description"};
        accountTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        accountTable = new ModernTable(accountTableModel);
        JScrollPane accScroll = new JScrollPane(accountTable);
        accScroll.setBorder(BorderFactory.createLineBorder(MonetraTheme.BORDER_COLOR, 1));
        accountsCard.add(accScroll, BorderLayout.CENTER);

        content.add(accountsCard);
        content.add(Box.createVerticalStrut(20));

        // 3. System Architecture & Diagnostics Card
        CardPanel diagCard = new CardPanel();
        diagCard.setLayout(new BoxLayout(diagCard, BoxLayout.Y_AXIS));
        diagCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel diagTitle = new JLabel("MONETRA System Architecture");
        diagTitle.setFont(MonetraTheme.FONT_TITLE);
        diagTitle.setForeground(MonetraTheme.ACCENT_GOLD);

        JLabel info1 = new JLabel("• Java 17 LTS Multi-Module Core Engine (monetra-core, monetra-api, monetra-desktop)");
        JLabel info2 = new JLabel("• Monetary Calculations: Strict IEEE-754 BigDecimal with MathContext.DECIMAL128");
        JLabel info3 = new JLabel("• Persistence: Normalized MySQL Schema with Flyway Database Versioning & Auto-Migration");
        JLabel info4 = new JLabel("• Data Visualization: Native JFreeChart with Dynamic Vector Palette");

        info1.setFont(MonetraTheme.FONT_BODY); info1.setForeground(MonetraTheme.TEXT_SECONDARY);
        info2.setFont(MonetraTheme.FONT_BODY); info2.setForeground(MonetraTheme.TEXT_SECONDARY);
        info3.setFont(MonetraTheme.FONT_BODY); info3.setForeground(MonetraTheme.TEXT_SECONDARY);
        info4.setFont(MonetraTheme.FONT_BODY); info4.setForeground(MonetraTheme.TEXT_SECONDARY);

        diagCard.add(diagTitle);
        diagCard.add(Box.createVerticalStrut(8));
        diagCard.add(info1);
        diagCard.add(Box.createVerticalStrut(4));
        diagCard.add(info2);
        diagCard.add(Box.createVerticalStrut(4));
        diagCard.add(info3);
        diagCard.add(Box.createVerticalStrut(4));
        diagCard.add(info4);

        content.add(diagCard);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        UserDto user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            usernameDisplay.setText(user.getUsername());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            currencyBox.setSelectedItem(user.getCurrency());
        }

        List<AccountDto> accounts = DesktopServiceManager.getInstance().getAccountService().getUserAccounts(userId);
        accountTableModel.setRowCount(0);
        for (AccountDto a : accounts) {
            accountTableModel.addRow(new Object[]{
                    a.getId(),
                    a.getName(),
                    a.getType().getDisplayName(),
                    a.getBalance(),
                    a.getCurrency(),
                    a.getDescription() != null ? a.getDescription() : ""
            });
        }
    }

    private void saveProfile() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) return;

        try {
            UserDto updated = DesktopServiceManager.getInstance().getUserService().updateProfile(
                    userId,
                    fullNameField.getText().trim(),
                    emailField.getText().trim(),
                    (String) currencyBox.getSelectedItem()
            );
            SessionManager.getInstance().setCurrentUser(updated);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAccountDialog() {
        Long userId = SessionManager.getInstance().getCurrentUserId();
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Account", true);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.setSize(400, 360);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StyledTextField nameField = new StyledTextField();
        JComboBox<AccountType> typeBox = new JComboBox<>(AccountType.values());
        StyledTextField balanceField = new StyledTextField("0.00");
        StyledTextField descField = new StyledTextField();

        form.add(new JLabel("Account Name:")); form.add(nameField);
        form.add(new JLabel("Account Type:")); form.add(typeBox);
        form.add(new JLabel("Opening Balance ($):")); form.add(balanceField);
        form.add(new JLabel("Description:")); form.add(descField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PrimaryButton save = new PrimaryButton("Create");
        SecondaryButton cancel = new SecondaryButton("Cancel");
        cancel.addActionListener(e -> dialog.dispose());

        save.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                AccountType type = (AccountType) typeBox.getSelectedItem();
                BigDecimal bal = new BigDecimal(balanceField.getText().trim());
                String desc = descField.getText().trim();

                DesktopServiceManager.getInstance().getAccountService().createAccount(userId,
                        new AccountCreateDto(name, type, bal, "USD", desc));
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
}
