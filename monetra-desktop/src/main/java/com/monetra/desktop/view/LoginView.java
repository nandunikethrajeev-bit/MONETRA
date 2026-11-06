package com.monetra.desktop.view;

import com.monetra.core.dto.LoginRequest;
import com.monetra.core.dto.UserDto;
import com.monetra.core.exception.MonetraException;
import com.monetra.desktop.component.*;
import com.monetra.desktop.service.DesktopServiceManager;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {

    private final Runnable onLoginSuccess;
    private final Runnable onNavigateToRegister;

    private StyledTextField usernameField;
    private StyledPasswordField passwordField;
    private JLabel errorLabel;

    public LoginView(Runnable onLoginSuccess, Runnable onNavigateToRegister) {
        this.onLoginSuccess = onLoginSuccess;
        this.onNavigateToRegister = onNavigateToRegister;

        setBackground(MonetraTheme.BG_MAIN);
        setLayout(new GridBagLayout());

        CardPanel card = new CardPanel(16);
        card.setPreferredSize(new Dimension(420, 520));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));

        // Brand
        JLabel brandLabel = new JLabel("MONETRA");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brandLabel.setForeground(MonetraTheme.ACCENT_GOLD);
        brandLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Personal Finance Management");
        subLabel.setFont(MonetraTheme.FONT_BODY);
        subLabel.setForeground(MonetraTheme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Sign In to Your Account");
        welcomeLabel.setFont(MonetraTheme.FONT_TITLE);
        welcomeLabel.setForeground(MonetraTheme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(MonetraTheme.FONT_BODY_BOLD);
        userLabel.setForeground(MonetraTheme.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = new StyledTextField();
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(MonetraTheme.FONT_BODY_BOLD);
        passLabel.setForeground(MonetraTheme.TEXT_SECONDARY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new StyledPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(MonetraTheme.FONT_SMALL);
        errorLabel.setForeground(MonetraTheme.COLOR_EXPENSE);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        PrimaryButton signInBtn = new PrimaryButton("Sign In");
        signInBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInBtn.addActionListener(e -> performLogin());

        // Also login on Enter key
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> performLogin());

        JButton registerLink = new JButton("Create a new account");
        registerLink.setFont(MonetraTheme.FONT_BODY);
        registerLink.setForeground(MonetraTheme.ACCENT_GOLD);
        registerLink.setContentAreaFilled(false);
        registerLink.setBorderPainted(false);
        registerLink.setFocusPainted(false);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.addActionListener(e -> {
            if (onNavigateToRegister != null) {
                onNavigateToRegister.run();
            }
        });

        // Assemble
        card.add(brandLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(28));
        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(20));

        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));

        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        card.add(errorLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(signInBtn);
        card.add(Box.createVerticalStrut(16));
        card.add(registerLink);

        add(card);
    }

    private void performLogin() {
        errorLabel.setText(" ");
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        try {
            UserDto user = DesktopServiceManager.getInstance().getUserService()
                    .authenticate(new LoginRequest(username, password));
            SessionManager.getInstance().login(user, "local-session-" + user.getId());
            passwordField.setText("");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } catch (MonetraException ex) {
            errorLabel.setText(ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("Authentication failed: " + ex.getMessage());
        }
    }
}
