package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.RecurringTransactionCreateDto;
import com.monetra.core.dto.RecurringTransactionDto;
import com.monetra.core.model.Transaction;
import com.monetra.core.service.RecurringTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recurring-transactions")
@Tag(name = "Recurring Payments", description = "Scheduled recurring income/expenses (weekly, monthly, yearly)")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @GetMapping
    @Operation(summary = "List all recurring transaction rules for user")
    public ResponseEntity<List<RecurringTransactionDto>> getRecurringTransactions() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(recurringTransactionService.getUserRecurringTransactions(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recurring transaction rule by ID")
    public ResponseEntity<RecurringTransactionDto> getById(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(recurringTransactionService.getById(id, userId));
    }

    @PostMapping
    @Operation(summary = "Create a recurring transaction rule")
    public ResponseEntity<RecurringTransactionDto> createRecurring(@RequestBody RecurringTransactionCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(recurringTransactionService.createRecurringTransaction(userId, dto));
    }

    @PostMapping("/process-due")
    @Operation(summary = "Process all due recurring transactions up to specified date (default today)")
    public ResponseEntity<List<Transaction>> processDue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate processDate) {
        return ResponseEntity.ok(recurringTransactionService.processDueTransactions(processDate));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete recurring transaction rule")
    public ResponseEntity<Void> deleteRecurring(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        recurringTransactionService.deleteRecurringTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }
}

