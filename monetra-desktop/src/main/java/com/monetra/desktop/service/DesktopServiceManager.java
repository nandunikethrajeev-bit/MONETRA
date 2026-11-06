package com.monetra.desktop.service;

import com.monetra.core.config.DatabaseConfig;
import com.monetra.core.repository.*;
import com.monetra.core.service.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DesktopServiceManager {

    private static DesktopServiceManager instance;

    private final UserService userService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final SavingsGoalService savingsGoalService;
    private final RecurringTransactionService recurringTransactionService;
    private final FinancialReportService financialReportService;
    private final FinancialInsightService financialInsightService;
    private final SavingsCalculatorService savingsCalculatorService;

    private DesktopServiceManager() {
        DatabaseConfig dbConfig = new DatabaseConfig();
        DataSource dataSource = dbConfig.dataSource();
        JdbcTemplate jdbcTemplate = dbConfig.jdbcTemplate(dataSource);

        UserRepository userRepository = new JdbcUserRepository(jdbcTemplate);
        AccountRepository accountRepository = new JdbcAccountRepository(jdbcTemplate);
        CategoryRepository categoryRepository = new JdbcCategoryRepository(jdbcTemplate);
        TransactionRepository transactionRepository = new JdbcTransactionRepository(jdbcTemplate);
        BudgetRepository budgetRepository = new JdbcBudgetRepository(jdbcTemplate);
        SavingsGoalRepository savingsGoalRepository = new JdbcSavingsGoalRepository(jdbcTemplate);
        RecurringTransactionRepository recurringTransactionRepository = new JdbcRecurringTransactionRepository(jdbcTemplate);

        PasswordService passwordService = new PasswordService();

        this.userService = new UserService(userRepository, accountRepository, passwordService);
        this.accountService = new AccountService(accountRepository);
        this.categoryService = new CategoryService(categoryRepository);
        this.transactionService = new TransactionService(transactionRepository, accountRepository, categoryRepository);
        this.budgetService = new BudgetService(budgetRepository, transactionRepository, categoryRepository);
        this.savingsGoalService = new SavingsGoalService(savingsGoalRepository, accountRepository);
        this.recurringTransactionService = new RecurringTransactionService(recurringTransactionRepository, transactionRepository, accountRepository, categoryRepository);
        this.financialInsightService = new FinancialInsightService(transactionRepository, budgetRepository);
        this.financialReportService = new FinancialReportService(
                accountRepository,
                transactionRepository,
                recurringTransactionRepository,
                budgetService,
                savingsGoalService,
                financialInsightService
        );
        this.savingsCalculatorService = new SavingsCalculatorService();
    }

    public static synchronized DesktopServiceManager getInstance() {
        if (instance == null) {
            instance = new DesktopServiceManager();
        }
        return instance;
    }

    public UserService getUserService() {
        return userService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public BudgetService getBudgetService() {
        return budgetService;
    }

    public SavingsGoalService getSavingsGoalService() {
        return savingsGoalService;
    }

    public RecurringTransactionService getRecurringTransactionService() {
        return recurringTransactionService;
    }

    public FinancialReportService getFinancialReportService() {
        return financialReportService;
    }

    public FinancialInsightService getFinancialInsightService() {
        return financialInsightService;
    }

    public SavingsCalculatorService getSavingsCalculatorService() {
        return savingsCalculatorService;
    }
}
