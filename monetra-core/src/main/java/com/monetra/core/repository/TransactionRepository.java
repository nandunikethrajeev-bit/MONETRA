package com.monetra.core.repository;

import com.monetra.core.dto.CategorySpendingDto;
import com.monetra.core.dto.MonthlyTrendDto;
import com.monetra.core.dto.TransactionFilterDto;
import com.monetra.core.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Optional<Transaction> findById(Long id);
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findFiltered(Long userId, TransactionFilterDto filter);
    List<Transaction> findRecent(Long userId, int limit);
    Transaction save(Transaction transaction);
    void update(Transaction transaction);
    void delete(Long id, Long userId);

    BigDecimal calculateTotalIncome(Long userId);
    BigDecimal calculateTotalExpense(Long userId);
    BigDecimal calculateMonthlyIncome(Long userId, int month, int year);
    BigDecimal calculateMonthlyExpense(Long userId, int month, int year);
    BigDecimal calculateCategorySpending(Long userId, Long categoryId, int month, int year);
    List<CategorySpendingDto> getCategorySpendingBreakdown(Long userId, int month, int year);
    List<MonthlyTrendDto> getMonthlyTrends(Long userId, int monthsCount);
}

