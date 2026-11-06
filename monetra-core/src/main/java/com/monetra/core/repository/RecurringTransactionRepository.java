package com.monetra.core.repository;

import com.monetra.core.model.RecurringTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository {
    Optional<RecurringTransaction> findById(Long id);
    Optional<RecurringTransaction> findByIdAndUserId(Long id, Long userId);
    List<RecurringTransaction> findByUserId(Long userId);
    List<RecurringTransaction> findDueRecurringTransactions(LocalDate date);
    RecurringTransaction save(RecurringTransaction recurring);
    void update(RecurringTransaction recurring);
    void updateNextDueDate(Long id, LocalDate nextDueDate, LocalDate lastProcessedDate);
    void delete(Long id, Long userId);
}

