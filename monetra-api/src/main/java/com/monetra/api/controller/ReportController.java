package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.*;
import com.monetra.core.service.FinancialInsightService;
import com.monetra.core.service.FinancialReportService;
import com.monetra.core.service.SavingsCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports & Analytics", description = "Dashboard summary metrics, visualizations data, rule-based insights, and what-if calculator")
public class ReportController {

    private final FinancialReportService financialReportService;
    private final FinancialInsightService financialInsightService;
    private final SavingsCalculatorService savingsCalculatorService;

    public ReportController(FinancialReportService financialReportService,
                            FinancialInsightService financialInsightService,
                            SavingsCalculatorService savingsCalculatorService) {
        this.financialReportService = financialReportService;
        this.financialInsightService = financialInsightService;
        this.savingsCalculatorService = savingsCalculatorService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get full dashboard financial summary", description = "Returns net worth, total income/expenses, monthly figures, budget usage, recent transactions, and insights")
    public ResponseEntity<FinancialSummaryDto> getSummary() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(financialReportService.getFinancialSummary(userId));
    }

    @GetMapping("/category-spending")
    @Operation(summary = "Get breakdown of spending by category for specific month/year")
    public ResponseEntity<List<CategorySpendingDto>> getCategorySpending(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        Long userId = UserContext.getRequiredUserId();
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        return ResponseEntity.ok(financialReportService.getCategorySpending(userId, m, y));
    }

    @GetMapping("/monthly-trends")
    @Operation(summary = "Get historical monthly trends of income, expenses, and net savings")
    public ResponseEntity<List<MonthlyTrendDto>> getMonthlyTrends(@RequestParam(defaultValue = "6") int months) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(financialReportService.getMonthlyTrends(userId, months));
    }

    @GetMapping("/insights")
    @Operation(summary = "Get rule-based financial insights derived from actual stored database transactions")
    public ResponseEntity<List<FinancialInsightDto>> getInsights() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(financialInsightService.generateInsights(userId));
    }

    @PostMapping("/what-if-calculator")
    @Operation(summary = "Calculate required monthly savings using BigDecimal compounding")
    public ResponseEntity<WhatIfSavingsCalculationResult> calculateWhatIfSavings(
            @RequestBody WhatIfSavingsCalculationRequest request) {
        return ResponseEntity.ok(savingsCalculatorService.calculateRequiredSavings(request));
    }
}

