package com.monetra.core.repository;

import com.monetra.core.model.GoalStatus;
import com.monetra.core.model.SavingsGoal;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository {
    Optional<SavingsGoal> findById(Long id);
    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
    List<SavingsGoal> findByUserId(Long userId);
    SavingsGoal save(SavingsGoal goal);
    void update(SavingsGoal goal);
    void updateAmount(Long id, BigDecimal newCurrentAmount, GoalStatus newStatus);
    void delete(Long id, Long userId);
}

