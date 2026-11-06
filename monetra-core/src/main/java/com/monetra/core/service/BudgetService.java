package com.monetra.core.service;

import com.monetra.core.dto.BudgetCreateDto;
import com.monetra.core.dto.BudgetDto;
import com.monetra.core.dto.BudgetStatusDto;
import com.monetra.core.exception.DuplicateResourceException;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.model.Budget;
import com.monetra.core.repository.BudgetRepository;
import com.monetra.core.repository.CategoryRepository;
import com.monetra.core.repository.TransactionRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository,
                         CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<BudgetStatusDto> getBudgetStatusesForPeriod(Long userId, int month, int year) {
        FinancialValidator.validateMonthAndYear(month, year);
        List<Budget> budgets = budgetRepository.findByUserIdAndPeriod(userId, month, year);
        List<BudgetStatusDto> statuses = new ArrayList<>();

        for (Budget b : budgets) {
            BigDecimal spent;
            if (b.getCategoryId() != null) {
                spent = transactionRepository.calculateCategorySpending(userId, b.getCategoryId(), month, year);
            } else {
                spent = transactionRepository.calculateMonthlyExpense(userId, month, year);
            }
            statuses.add(BudgetStatusDto.calculate(b, spent));
        }

        return statuses;
    }

    public BudgetStatusDto getBudgetStatusById(Long budgetId, Long userId) {
        Budget b = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));

        BigDecimal spent;
        if (b.getCategoryId() != null) {
            spent = transactionRepository.calculateCategorySpending(userId, b.getCategoryId(), b.getMonth(), b.getYear());
        } else {
            spent = transactionRepository.calculateMonthlyExpense(userId, b.getMonth(), b.getYear());
        }
        return BudgetStatusDto.calculate(b, spent);
    }

    @Transactional
    public BudgetDto createBudget(Long userId, BudgetCreateDto dto) {
        FinancialValidator.validatePositiveAmount(dto.getAmount(), "Budget Amount");
        FinancialValidator.validateNotBlank(dto.getName(), "Budget Name");
        int month = dto.getMonth() != null ? dto.getMonth() : LocalDate.now().getMonthValue();
        int year = dto.getYear() != null ? dto.getYear() : LocalDate.now().getYear();
        FinancialValidator.validateMonthAndYear(month, year);

        if (dto.getCategoryId() != null) {
            categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
        }

        // Check if a budget already exists for this category/overall in the given period
        Optional<Budget> existing = budgetRepository.findByUserCategoryAndPeriod(userId, dto.getCategoryId(), month, year);
        if (existing.isPresent()) {
            throw new DuplicateResourceException("A budget for this category and period already exists");
        }

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategoryId(dto.getCategoryId());
        budget.setName(dto.getName().trim());
        budget.setAmount(dto.getAmount());
        budget.setMonth(month);
        budget.setYear(year);

        Budget saved = budgetRepository.save(budget);
        return BudgetDto.fromEntity(saved);
    }

    @Transactional
    public BudgetDto updateBudget(Long budgetId, Long userId, BudgetCreateDto dto) {
        Budget existing = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));

        FinancialValidator.validatePositiveAmount(dto.getAmount(), "Budget Amount");
        FinancialValidator.validateNotBlank(dto.getName(), "Budget Name");

        existing.setName(dto.getName().trim());
        existing.setAmount(dto.getAmount());
        if (dto.getCategoryId() != null) {
            categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
            existing.setCategoryId(dto.getCategoryId());
        } else {
            existing.setCategoryId(null);
        }

        budgetRepository.update(existing);
        return BudgetDto.fromEntity(existing);
    }

    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", budgetId));
        budgetRepository.delete(budgetId, userId);
    }
}

