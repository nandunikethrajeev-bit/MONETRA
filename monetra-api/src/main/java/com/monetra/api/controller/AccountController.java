package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.AccountCreateDto;
import com.monetra.core.dto.AccountDto;
import com.monetra.core.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management (Checking, Savings, Credit Cards, Cash, Investments)")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "List all accounts belonging to authenticated user")
    public ResponseEntity<List<AccountDto>> getAccounts() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(userId, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account details by ID")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(accountService.getAccountById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account name, type, or description")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable Long id, @RequestBody AccountCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(accountService.updateAccount(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete account and its associated transactions")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        accountService.deleteAccount(id, userId);
        return ResponseEntity.noContent().build();
    }
}

