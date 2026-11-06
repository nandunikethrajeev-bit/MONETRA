package com.monetra.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monetra.api.controller.*;
import com.monetra.api.exception.GlobalExceptionHandler;
import com.monetra.api.security.AuthInterceptor;
import com.monetra.api.security.JwtTokenProvider;
import com.monetra.core.config.DatabaseConfig;
import com.monetra.core.dto.AuthResponse;
import com.monetra.core.dto.RegisterRequest;
import com.monetra.core.dto.TransactionCreateDto;
import com.monetra.core.model.TransactionType;
import com.monetra.core.repository.*;
import com.monetra.core.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MonetraApiIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    void setup() {
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
        UserService userService = new UserService(userRepository, accountRepository, passwordService);
        AccountService accountService = new AccountService(accountRepository);
        CategoryService categoryService = new CategoryService(categoryRepository);
        TransactionService transactionService = new TransactionService(transactionRepository, accountRepository, categoryRepository);
        BudgetService budgetService = new BudgetService(budgetRepository, transactionRepository, categoryRepository);
        SavingsGoalService savingsGoalService = new SavingsGoalService(savingsGoalRepository, accountRepository);
        RecurringTransactionService recurringTransactionService = new RecurringTransactionService(recurringTransactionRepository, transactionRepository, accountRepository, categoryRepository);
        FinancialInsightService financialInsightService = new FinancialInsightService(transactionRepository, budgetRepository);
        SavingsCalculatorService savingsCalculatorService = new SavingsCalculatorService();
        FinancialReportService financialReportService = new FinancialReportService(accountRepository, transactionRepository, recurringTransactionRepository, budgetService, savingsGoalService, financialInsightService);

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("monetra_test_secure_jwt_secret_key_1234567890_test_suite", 86400);
        AuthInterceptor authInterceptor = new AuthInterceptor(jwtTokenProvider);

        AuthController authController = new AuthController(userService, jwtTokenProvider);
        AccountController accountController = new AccountController(accountService);
        TransactionController transactionController = new TransactionController(transactionService);
        CategoryController categoryController = new CategoryController(categoryService);
        BudgetController budgetController = new BudgetController(budgetService);
        SavingsGoalController savingsGoalController = new SavingsGoalController(savingsGoalService);
        RecurringTransactionController recurringTransactionController = new RecurringTransactionController(recurringTransactionService);
        ReportController reportController = new ReportController(financialReportService, financialInsightService, savingsCalculatorService);
        UserController userController = new UserController(userService);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(
                        authController,
                        accountController,
                        transactionController,
                        categoryController,
                        budgetController,
                        savingsGoalController,
                        recurringTransactionController,
                        reportController,
                        userController
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(authInterceptor)
                .build();
    }

    @Test
    @DisplayName("REST API: Register, authenticate, create transaction, fetch reports, and verify user isolation")
    void testRestApiLifecycleAndIsolation() throws Exception {
        // 1. Register User A
        String userA = "alice_" + System.currentTimeMillis();
        RegisterRequest regA = new RegisterRequest(userA, userA + "@test.com", "Password123!", "Alice Wonderland", "USD");

        MvcResult resultA = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regA)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.username").value(userA))
                .andReturn();

        AuthResponse authA = objectMapper.readValue(resultA.getResponse().getContentAsString(), AuthResponse.class);
        String tokenA = authA.getToken();

        // 2. Fetch User A Accounts (auto-created Primary Checking)
        MvcResult accountsResultA = mockMvc.perform(get("/api/v1/accounts")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Primary Checking"))
                .andReturn();

        // 3. Register User B
        String userB = "bob_" + System.currentTimeMillis();
        RegisterRequest regB = new RegisterRequest(userB, userB + "@test.com", "Password123!", "Bob Builder", "USD");

        MvcResult resultB = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regB)))
                .andExpect(status().isCreated())
                .andReturn();

        AuthResponse authB = objectMapper.readValue(resultB.getResponse().getContentAsString(), AuthResponse.class);
        String tokenB = authB.getToken();

        // 4. Test User Data Isolation: User B cannot access User A's profile or account
        com.fasterxml.jackson.databind.JsonNode accountsNode = objectMapper.readTree(accountsResultA.getResponse().getContentAsString());
        long userAAccountId = accountsNode.get(0).get("id").asLong();

        mockMvc.perform(get("/api/v1/accounts/" + userAAccountId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // 5. Unauthenticated request to /api/v1/reports/summary must return 401 Unauthorized
        mockMvc.perform(get("/api/v1/reports/summary"))
                .andExpect(status().isUnauthorized());

        // 6. Authenticated request to /api/v1/reports/summary succeeds
        mockMvc.perform(get("/api/v1/reports/summary")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").exists())
                .andExpect(jsonPath("$.insights").isArray());
    }
}
