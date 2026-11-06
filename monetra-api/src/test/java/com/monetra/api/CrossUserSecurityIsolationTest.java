package com.monetra.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.monetra.api.controller.*;
import com.monetra.api.exception.GlobalExceptionHandler;
import com.monetra.api.security.AuthInterceptor;
import com.monetra.api.security.JwtTokenProvider;
import com.monetra.core.config.DatabaseConfig;
import com.monetra.core.dto.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrossUserSecurityIsolationTest {

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
        FinancialInsightService financialInsightService = new FinancialInsightService(transactionRepository, budgetRepository);
        SavingsCalculatorService savingsCalculatorService = new SavingsCalculatorService();
        FinancialReportService financialReportService = new FinancialReportService(
                accountRepository, transactionRepository, recurringTransactionRepository,
                budgetService, savingsGoalService, financialInsightService
        );

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
                "SuperSecretJwtSigningKeyForIntegrationTestsMustBeAtLeast256BitsLong!",
                86400000L
        );
        AuthInterceptor authInterceptor = new AuthInterceptor(jwtTokenProvider);

        AuthController authController = new AuthController(userService, jwtTokenProvider);
        TransactionController transactionController = new TransactionController(transactionService);
        AccountController accountController = new AccountController(accountService);
        CategoryController categoryController = new CategoryController(categoryService);
        BudgetController budgetController = new BudgetController(budgetService);
        ReportController reportController = new ReportController(financialReportService, financialInsightService, savingsCalculatorService);

        this.mockMvc = MockMvcBuilders.standaloneSetup(
                        authController, transactionController, accountController, categoryController, budgetController, reportController
                )
                .addInterceptors(authInterceptor)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Security: User B cannot access, read, or delete User A transactions, budgets, or private reports")
    void verifyCrossUserDataIsolation() throws Exception {
        long nonce = System.currentTimeMillis();

        // 1. Register User A
        RegisterRequest userARequest = new RegisterRequest(
                "security_userA_" + nonce,
                "userA_" + nonce + "@sec.com",
                "Password123!",
                "User Alpha",
                "USD"
        );
        MvcResult regResultA = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userARequest)))
                .andExpect(status().isCreated())
                .andReturn();
        AuthResponse authA = objectMapper.readValue(regResultA.getResponse().getContentAsString(), AuthResponse.class);
        String tokenA = authA.getToken();

        // 2. Register User B
        RegisterRequest userBRequest = new RegisterRequest(
                "security_userB_" + nonce,
                "userB_" + nonce + "@sec.com",
                "Password123!",
                "User Beta",
                "USD"
        );
        MvcResult regResultB = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        AuthResponse authB = objectMapper.readValue(regResultB.getResponse().getContentAsString(), AuthResponse.class);
        String tokenB = authB.getToken();

        // 3. User A gets their account ID
        MvcResult accsResultA = mockMvc.perform(get("/api/v1/accounts")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andReturn();
        AccountDto[] accsA = objectMapper.readValue(accsResultA.getResponse().getContentAsString(), AccountDto[].class);
        Long userAAccountId = accsA[0].getId();

        // 4. User A records a $75,000 Income transaction
        TransactionCreateDto txCreateA = new TransactionCreateDto(
                userAAccountId,
                9L, // Salary
                new BigDecimal("75000.00"),
                TransactionType.INCOME,
                LocalDate.now(),
                "Secret Executive Bonus",
                null
        );
        MvcResult txResultA = mockMvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(txCreateA)))
                .andExpect(status().isCreated())
                .andReturn();
        TransactionDto txA = objectMapper.readValue(txResultA.getResponse().getContentAsString(), TransactionDto.class);
        Long userATransactionId = txA.getId();

        // 5. ATTACK: User B tries to view User A's transaction using User B's token
        mockMvc.perform(get("/api/v1/transactions/" + userATransactionId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound()); // ResourceNotFoundException: cannot access another user's transaction

        // 6. ATTACK: User B tries to delete User A's transaction using User B's token
        mockMvc.perform(delete("/api/v1/transactions/" + userATransactionId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        // 7. Verify User A's transaction is completely intact
        mockMvc.perform(get("/api/v1/transactions/" + userATransactionId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(75000.00))
                .andExpect(jsonPath("$.description").value("Secret Executive Bonus"));

        // 8. User B queries reports summary -> must see $0 balance, ZERO leakage of User A's $75,000!
        mockMvc.perform(get("/api/v1/reports/summary")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBalance").value(0))
                .andExpect(jsonPath("$.totalIncome").value(0));
    }
}
