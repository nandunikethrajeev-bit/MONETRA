package com.monetra.core.repository;

import com.monetra.core.model.Budget;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository {
    Optional<Budget> findById(Long id);
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    List<Budget> findByUserIdAndPeriod(Long userId, int month, int year);
    Optional<Budget> findByUserCategoryAndPeriod(Long userId, Long categoryId, int month, int year);
    Budget save(Budget budget);
    void update(Budget budget);
    void delete(Long id, Long userId);
}

