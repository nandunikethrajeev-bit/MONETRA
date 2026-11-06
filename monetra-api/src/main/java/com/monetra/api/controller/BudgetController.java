package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.BudgetCreateDto;
import com.monetra.core.dto.BudgetDto;
import com.monetra.core.dto.BudgetStatusDto;
import com.monetra.core.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@Tag(name = "Budgets", description = "Monthly and category budgets, utilization tracking, and overrun warnings")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    @Operation(summary = "Get budgets with consumption status for a specific month and year")
    public ResponseEntity<List<BudgetStatusDto>> getBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Long userId = UserContext.getRequiredUserId();
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        return ResponseEntity.ok(budgetService.getBudgetStatusesForPeriod(userId, m, y));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget status by ID")
    public ResponseEntity<BudgetStatusDto> getBudgetById(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(budgetService.getBudgetStatusById(id, userId));
    }

    @PostMapping
    @Operation(summary = "Create a monthly budget (overall or category-specific)")
    public ResponseEntity<BudgetDto> createBudget(@RequestBody BudgetCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(userId, dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update budget limit or details")
    public ResponseEntity<BudgetDto> updateBudget(@PathVariable Long id, @RequestBody BudgetCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(budgetService.updateBudget(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        budgetService.deleteBudget(id, userId);
        return ResponseEntity.noContent().build();
    }
}

