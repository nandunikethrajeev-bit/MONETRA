package com.monetra.desktop.view;

import com.monetra.core.dto.RegisterRequest;
import com.monetra.core.dto.UserDto;
import com.monetra.core.exception.MonetraException;
import com.monetra.desktop.component.*;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JPanel {

    private final Runnable onRegisterSuccess;
    private final Runnable onNavigateToLogin;

    private StyledTextField fullNameField;
    private StyledTextField usernameField;
    private StyledTextField emailField;
    private StyledPasswordField passwordField;
    private JComboBox<String> currencyBox;
    private JLabel errorLabel;

    public RegisterView(Runnable onRegisterSuccess, Runnable onNavigateToLogin) {
        this.onRegisterSuccess = onRegisterSuccess;
        this.onNavigateToLogin = onNavigateToLogin;

        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new GridBagLayout());

        CardPanel card = new CardPanel(16);
        card.setPreferredSize(new Dimension(460, 640));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel brandLabel = new JLabel("MONETRA");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        brandLabel.setForeground(MonetraTheme.ACCENT_GOLD);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(MonetraTheme.FONT_TITLE);
        titleLabel.setForeground(MonetraTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fullNameField = new StyledTextField();
        fullNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        usernameField = new StyledTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        emailField = new StyledTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        passwordField = new StyledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        currencyBox = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "INR", "CAD", "AUD", "JPY"});
        currencyBox.setFont(MonetraTheme.FONT_BODY);
        currencyBox.setBackground(MonetraTheme.BG_INPUT);
        currencyBox.setForeground(MonetraTheme.TEXT_PRIMARY);
        currencyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(MonetraTheme.FONT_SMALL);
        errorLabel.setForeground(MonetraTheme.COLOR_EXPENSE);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        PrimaryButton registerBtn = new PrimaryButton("Create Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> performRegister());

        JButton loginLink = new JButton("Already have an account? Sign In");
        loginLink.setFont(MonetraTheme.FONT_BODY);
        loginLink.setForeground(MonetraTheme.ACCENT_GOLD);
        loginLink.setContentAreaFilled(false);
        loginLink.setBorderPainted(false);
        loginLink.setFocusPainted(false);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addActionListener(e -> {
            if (onNavigateToLogin != null) {
                onNavigateToLogin.run();
            }
        });

        // Add to card
        card.add(brandLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(18));

        addField(card, "Full Name", fullNameField);
        addField(card, "Username", usernameField);
        addField(card, "Email Address", emailField);
        addField(card, "Password (min 6 characters)", passwordField);
        addField(card, "Preferred Currency", currencyBox);

        card.add(errorLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(loginLink);

        add(card);
    }

    private void addField(JPanel card, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(MonetraTheme.FONT_SMALL);
        label.setForeground(MonetraTheme.TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(label);
        card.add(Box.createVerticalStrut(4));
        card.add(field);
        card.add(Box.createVerticalStrut(10));
    }

    private void performRegister() {
        errorLabel.setText(" ");
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String currency = (String) currencyBox.getSelectedItem();

        try {
            RegisterRequest req = new RegisterRequest(username, email, password, fullName, currency);
            UserDto user = DesktopServiceManager.getInstance().getUserService().register(req);
            SessionManager.getInstance().login(user, "local-session-" + user.getId());
            if (onRegisterSuccess != null) {
                onRegisterSuccess.run();
            }
        } catch (MonetraException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("Registration error: " + ex.getMessage());
        }
    }
}
