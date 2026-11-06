package com.monetra.desktop.view;

import com.monetra.desktop.component.SidebarNavigation;
import com.monetra.desktop.service.SessionManager;
import com.monetra.desktop.theme.MonetraTheme;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final CardLayout rootCardLayout;
    private final JPanel rootPanel;

    private final CardLayout appCardLayout;
    private final JPanel appContentPanel;

    private final SidebarNavigation sidebar;
    private final DashboardView dashboardView;
    private final TransactionsView transactionsView;
    private final IncomeView incomeView;
    private final ExpensesView expensesView;
    private final CategoriesView categoriesView;
    private final BudgetsView budgetsView;
    private final SavingsGoalsView savingsGoalsView;
    private final RecurringTransactionsView recurringView;
    private final ReportsView reportsView;
    private final SettingsView settingsView;

    public MainFrame() {
        setTitle("MONETRA — Personal Finance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 820);
        setMinimumSize(new Dimension(1020, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(MonetraTheme.BG_MAIN);

        rootCardLayout = new CardLayout();
        rootPanel = new JPanel(rootCardLayout);
        rootPanel.setBackground(MonetraTheme.BG_MAIN);

        // 1. Login View
        LoginView loginView = new LoginView(this::onLoginSuccess, () -> showRootCard("REGISTER"));

        // 2. Register View
        RegisterView registerView = new RegisterView(this::onLoginSuccess, () -> showRootCard("LOGIN"));

        // 3. Main App Container (Sidebar + Content Area)
        JPanel mainAppContainer = new JPanel(new BorderLayout());
        mainAppContainer.setBackground(MonetraTheme.BG_MAIN);

        sidebar = new SidebarNavigation(this::showAppCard, this::onLogout);
        mainAppContainer.add(sidebar, BorderLayout.WEST);

        appCardLayout = new CardLayout();
        appContentPanel = new JPanel(appCardLayout);
        appContentPanel.setBackground(MonetraTheme.BG_MAIN);

        // Views
        dashboardView = new DashboardView();
        transactionsView = new TransactionsView();
        incomeView = new IncomeView();
        expensesView = new ExpensesView();
        categoriesView = new CategoriesView();
        budgetsView = new BudgetsView();
        savingsGoalsView = new SavingsGoalsView();
        recurringView = new RecurringTransactionsView();
        reportsView = new ReportsView();
        settingsView = new SettingsView();

        appContentPanel.add(dashboardView, "DASHBOARD");
        appContentPanel.add(transactionsView, "TRANSACTIONS");
        appContentPanel.add(incomeView, "INCOME");
        appContentPanel.add(expensesView, "EXPENSES");
        appContentPanel.add(categoriesView, "CATEGORIES");
        appContentPanel.add(budgetsView, "BUDGETS");
        appContentPanel.add(savingsGoalsView, "SAVINGS_GOALS");
        appContentPanel.add(recurringView, "RECURRING");
        appContentPanel.add(reportsView, "REPORTS");
        appContentPanel.add(settingsView, "SETTINGS");

        mainAppContainer.add(appContentPanel, BorderLayout.CENTER);

        // Assemble Root
        rootPanel.add(loginView, "LOGIN");
        rootPanel.add(registerView, "REGISTER");
        rootPanel.add(mainAppContainer, "APP");

        add(rootPanel);
        showRootCard("LOGIN");
    }

    private void showRootCard(String name) {
        rootCardLayout.show(rootPanel, name);
    }

    public void showAppCard(String name) {
        appCardLayout.show(appContentPanel, name);
        switch (name) {
            case "DASHBOARD" -> dashboardView.loadData();
            case "TRANSACTIONS" -> transactionsView.loadData();
            case "INCOME" -> incomeView.loadData();
            case "EXPENSES" -> expensesView.loadData();
            case "CATEGORIES" -> categoriesView.loadData();
            case "BUDGETS" -> budgetsView.loadData();
            case "SAVINGS_GOALS" -> savingsGoalsView.loadData();
            case "RECURRING" -> recurringView.loadData();
            case "REPORTS" -> reportsView.loadData();
            case "SETTINGS" -> settingsView.loadData();
        }
    }

    private void onLoginSuccess() {
        sidebar.updateUser(SessionManager.getInstance().getCurrentUser());
        showRootCard("APP");
        sidebar.setActive("DASHBOARD");
        showAppCard("DASHBOARD");
    }

    private void onLogout() {
        SessionManager.getInstance().logout();
        showRootCard("LOGIN");
    }
}
