package com.monetra.desktop.component;

import com.monetra.core.dto.UserDto;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SidebarNavigation extends JPanel {

    private final Consumer<String> onNavigate;
    private final Runnable onLogout;
    private final List<NavItemButton> navButtons = new ArrayList<>();
    private JLabel userNameLabel;

    public SidebarNavigation(Consumer<String> onNavigate, Runnable onLogout) {
        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        setPreferredSize(new Dimension(240, 0));
        setBackground(MonetraTheme.BG_SIDEBAR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, MonetraTheme.BORDER_COLOR));

        // Brand Logo Panel
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(24, 20, 20, 20));

        JLabel logoLabel = new JLabel("MONETRA");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(MonetraTheme.ACCENT_GOLD);

        JLabel tagLabel = new JLabel("Personal Finance OS");
        tagLabel.setFont(MonetraTheme.FONT_SMALL);
        tagLabel.setForeground(MonetraTheme.TEXT_MUTED);

        brandPanel.add(logoLabel);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(tagLabel);

        add(brandPanel, BorderLayout.NORTH);

        // Navigation Items Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        addNavItem(menuPanel, "Dashboard", "DASHBOARD");
        addNavItem(menuPanel, "Transactions", "TRANSACTIONS");
        addNavItem(menuPanel, "Income", "INCOME");
        addNavItem(menuPanel, "Expenses", "EXPENSES");
        addNavItem(menuPanel, "Categories", "CATEGORIES");
        addNavItem(menuPanel, "Budgets", "BUDGETS");
        addNavItem(menuPanel, "Savings Goals", "SAVINGS_GOALS");
        addNavItem(menuPanel, "Recurring Payments", "RECURRING");
        addNavItem(menuPanel, "Reports & Charts", "REPORTS");
        addNavItem(menuPanel, "Settings", "SETTINGS");

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom User & Logout Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, MonetraTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)
        ));

        userNameLabel = new JLabel("User");
        userNameLabel.setFont(MonetraTheme.FONT_BODY_BOLD);
        userNameLabel.setForeground(MonetraTheme.TEXT_PRIMARY);

        SecondaryButton logoutBtn = new SecondaryButton("Logout");
        logoutBtn.setFont(MonetraTheme.FONT_SMALL);
        logoutBtn.addActionListener(e -> {
            if (this.onLogout != null) {
                this.onLogout.run();
            }
        });

        bottomPanel.add(userNameLabel, BorderLayout.CENTER);
        bottomPanel.add(logoutBtn, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Set initial active
        if (!navButtons.isEmpty()) {
            setActive("DASHBOARD");
        }
    }

    public void updateUser(UserDto user) {
        if (user != null) {
            userNameLabel.setText(user.getFullName() != null ? user.getFullName() : user.getUsername());
        }
    }

    private void addNavItem(JPanel container, String title, String actionCommand) {
        NavItemButton btn = new NavItemButton(title, actionCommand);
        btn.addActionListener(e -> {
            setActive(actionCommand);
            if (onNavigate != null) {
                onNavigate.accept(actionCommand);
            }
        });
        navButtons.add(btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(4));
    }

    public void setActive(String actionCommand) {
        for (NavItemButton b : navButtons) {
            b.setActive(b.getActionCommand().equals(actionCommand));
        }
    }

    private static class NavItemButton extends JButton {
        private boolean isActive = false;

        public NavItemButton(String text, String actionCommand) {
            super(text);
            setActionCommand(actionCommand);
            setFont(MonetraTheme.FONT_BODY);
            setForeground(MonetraTheme.TEXT_SECONDARY);
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setPreferredSize(new Dimension(210, 40));
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setForeground(active ? MonetraTheme.ACCENT_GOLD : MonetraTheme.TEXT_SECONDARY);
            setFont(active ? MonetraTheme.FONT_BODY_BOLD : MonetraTheme.FONT_BODY);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(MonetraTheme.ACCENT_GOLD_MUTED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(MonetraTheme.ACCENT_GOLD);
                g2.fillRect(0, 8, 4, getHeight() - 16);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
