package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.SavingsContributionDto;
import com.monetra.core.dto.SavingsGoalCreateDto;
import com.monetra.core.dto.SavingsGoalDto;
import com.monetra.core.service.SavingsGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savings-goals")
@Tag(name = "Savings Goals", description = "Target savings goals, contributions, and progress tracking")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    @GetMapping
    @Operation(summary = "List all savings goals for authenticated user")
    public ResponseEntity<List<SavingsGoalDto>> getSavingsGoals() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(savingsGoalService.getUserSavingsGoals(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get savings goal by ID")
    public ResponseEntity<SavingsGoalDto> getSavingsGoalById(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(savingsGoalService.getSavingsGoalById(id, userId));
    }

    @PostMapping
    @Operation(summary = "Create a new savings goal")
    public ResponseEntity<SavingsGoalDto> createSavingsGoal(@RequestBody SavingsGoalCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsGoalService.createSavingsGoal(userId, dto));
    }

    @PostMapping("/{id}/contribute")
    @Operation(summary = "Record a contribution towards a savings goal")
    public ResponseEntity<SavingsGoalDto> recordContribution(@PathVariable Long id, @RequestBody SavingsContributionDto contributionDto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(savingsGoalService.recordContribution(id, userId, contributionDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update savings goal parameters")
    public ResponseEntity<SavingsGoalDto> updateSavingsGoal(@PathVariable Long id, @RequestBody SavingsGoalCreateDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(savingsGoalService.updateSavingsGoal(id, userId, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete savings goal")
    public ResponseEntity<Void> deleteSavingsGoal(@PathVariable Long id) {
        Long userId = UserContext.getRequiredUserId();
        savingsGoalService.deleteSavingsGoal(id, userId);
        return ResponseEntity.noContent().build();
    }
}
