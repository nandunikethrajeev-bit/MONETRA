package com.monetra.core;

import com.monetra.core.config.DatabaseConfig;
import com.monetra.core.dto.*;
import com.monetra.core.model.AccountType;
import com.monetra.core.model.GoalStatus;
import com.monetra.core.model.RecurrenceFrequency;
import com.monetra.core.model.TransactionType;
import com.monetra.core.repository.*;
import com.monetra.core.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RepositoryAndServiceIntegrationTest {

    private UserService userService;
    private AccountService accountService;
    private CategoryService categoryService;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private SavingsGoalService savingsGoalService;
    private RecurringTransactionService recurringTransactionService;
    private FinancialReportService financialReportService;

    @BeforeAll
    void init() {
        DatabaseConfig config = new DatabaseConfig();
        DataSource dataSource = config.dataSource();
        JdbcTemplate jdbcTemplate = config.jdbcTemplate(dataSource);

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
        FinancialInsightService insightService = new FinancialInsightService(transactionRepository, budgetRepository);
        this.financialReportService = new FinancialReportService(
                accountRepository, transactionRepository, recurringTransactionRepository,
                budgetService, savingsGoalService, insightService
        );
    }

    @Test
    @DisplayName("End-to-end integration test: User lifecycle, transactions, budgets, goals, and reports")
    void testCompleteUserFinancialLifecycle() {
        // 1. Register User
        String username = "alex_finance_" + System.currentTimeMillis();
        RegisterRequest registerReq = new RegisterRequest(
                username,
                username + "@example.com",
                "Password123!",
                "Alex Johnson",
                "USD"
        );
        UserDto registeredUser = userService.register(registerReq);
        assertThat(registeredUser.getId()).isNotNull();
        assertThat(registeredUser.getUsername()).isEqualTo(username);

        // Verify primary account was automatically provisioned
        List<AccountDto> accounts = accountService.getUserAccounts(registeredUser.getId());
        assertThat(accounts).hasSize(1);
        AccountDto primaryAccount = accounts.get(0);
        assertThat(primaryAccount.getName()).isEqualTo("Primary Checking");
        assertThat(primaryAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        // 2. Add an additional Savings account
        AccountDto savingsAcc = accountService.createAccount(registeredUser.getId(),
                new AccountCreateDto("High Yield Savings", AccountType.SAVINGS, new BigDecimal("1000.00"), "USD", "Emergency stash"));
        assertThat(savingsAcc.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));

        // 3. Verify Default Categories Seeded
        List<CategoryDto> categories = categoryService.getCategoriesForUser(registeredUser.getId());
        assertThat(categories).isNotEmpty();
        CategoryDto salaryCat = categories.stream().filter(c -> "Salary".equalsIgnoreCase(c.getName())).findFirst().orElseThrow();
        CategoryDto foodCat = categories.stream().filter(c -> "Food & Dining".equalsIgnoreCase(c.getName())).findFirst().orElseThrow();

        // 4. Record Income Transaction
        LocalDate today = LocalDate.now();
        TransactionDto incomeTx = transactionService.createTransaction(registeredUser.getId(),
                new TransactionCreateDto(primaryAccount.getId(), salaryCat.getId(), new BigDecimal("5000.00"),
                        TransactionType.INCOME, today, "Monthly Tech Salary", "Direct deposit"));

        assertThat(incomeTx.getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));

        // Verify Primary Account balance updated: 0 + 5000 = 5000
        AccountDto updatedPrimary = accountService.getAccountById(primaryAccount.getId(), registeredUser.getId());
        assertThat(updatedPrimary.getBalance()).isEqualByComparingTo(new BigDecimal("5000.00"));

        // 5. Record Expense Transaction
        TransactionDto expenseTx = transactionService.createTransaction(registeredUser.getId(),
                new TransactionCreateDto(primaryAccount.getId(), foodCat.getId(), new BigDecimal("250.00"),
                        TransactionType.EXPENSE, today, "Weekly Grocery Run", "Whole Foods"));

        // Verify Primary Account balance updated: 5000 - 250 = 4750
        updatedPrimary = accountService.getAccountById(primaryAccount.getId(), registeredUser.getId());
        assertThat(updatedPrimary.getBalance()).isEqualByComparingTo(new BigDecimal("4750.00"));

        // 6. Create Budget and check utilization
        BudgetDto budget = budgetService.createBudget(registeredUser.getId(),
                new BudgetCreateDto(foodCat.getId(), "Food Budget", new BigDecimal("500.00"), today.getMonthValue(), today.getYear()));

        List<BudgetStatusDto> budgetStatuses = budgetService.getBudgetStatusesForPeriod(registeredUser.getId(), today.getMonthValue(), today.getYear());
        assertThat(budgetStatuses).hasSize(1);
        BudgetStatusDto foodStatus = budgetStatuses.get(0);
        assertThat(foodStatus.getSpentAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(foodStatus.getRemainingAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(foodStatus.getPercentageUsed()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(foodStatus.getStatus()).isEqualTo(com.monetra.core.model.BudgetStatus.OK);

        // 7. Create Savings Goal and contribute
        SavingsGoalDto goal = savingsGoalService.createSavingsGoal(registeredUser.getId(),
                new SavingsGoalCreateDto(savingsAcc.getId(), "Vacation to Tokyo", new BigDecimal("3000.00"),
                        new BigDecimal("500.00"), today.plusMonths(6), "Trip to Japan"));

        assertThat(goal.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(goal.getRemainingAmount()).isEqualByComparingTo(new BigDecimal("2500.00"));

        // Record contribution from primary account
        SavingsGoalDto contributedGoal = savingsGoalService.recordContribution(goal.getId(), registeredUser.getId(),
                new SavingsContributionDto(new BigDecimal("1000.00"), primaryAccount.getId(), "Monthly trip deposit"));

        assertThat(contributedGoal.getCurrentAmount()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(contributedGoal.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));

        // 8. Recurring Transactions: Create and process
        recurringTransactionService.createRecurringTransaction(registeredUser.getId(),
                new RecurringTransactionCreateDto(primaryAccount.getId(), foodCat.getId(), new BigDecimal("15.00"),
                        TransactionType.EXPENSE, RecurrenceFrequency.WEEKLY, today, null, "Streaming Service"));

        List<com.monetra.core.model.Transaction> processed = recurringTransactionService.processDueTransactions(today);
        assertThat(processed).isNotEmpty();

        // 9. Check Dashboard Financial Summary
        FinancialSummaryDto summary = financialReportService.getFinancialSummary(registeredUser.getId());
        assertThat(summary.getTotalBalance()).isGreaterThan(BigDecimal.ZERO);
        assertThat(summary.getMonthlyIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(summary.getRecentTransactions()).isNotEmpty();
        assertThat(summary.getInsights()).isNotEmpty();
    }
}

