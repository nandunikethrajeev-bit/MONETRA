package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.TransactionCreateDto;
import com.monetra.core.dto.TransactionDto;
import com.monetra.core.dto.TransactionFilterDto;
import com.monetra.core.model.TransactionType;
import com.monetra.core.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Income and expense transactions CRUD, filtering, and search")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "Get filtered transactions", description = "Filter by account, category, transaction type, date range, and text search")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search) {
        Long userId = UserContext.getRequiredUserId();
        TransactionFilterDto filter = new TransactionFilterDto(accountId, categoryId, type, startDate, endDate, search);
        return ResponseEntity.ok(transactionService.getFilteredTransactions(userId, filter));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent transactions", description = "Retrieves the most recent transactions up to the specified limit")
    public ResponseEntity<List<TransactionDto>> getRecentTransactions(@RequestParam(defaultValue = "10") int limit) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(transactionService.getRecentTransactions(userId, limit));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(transactionService.getTransactionById(id, userId));
    }

    @PostMapping
    @Operation(summary = "Record a new transaction (Income or Expense)", description = "Atomically updates account balance and validates parameters")
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(userId, dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing transaction", description = "Adjusts balances across old and new accounts atomically")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @RequestBody TransactionCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(transactionService.updateTransaction(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction", description = "Reverts account balance changes")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }
}
