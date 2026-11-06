package com.monetra.core.service;

import com.monetra.core.dto.RecurringTransactionCreateDto;
import com.monetra.core.dto.RecurringTransactionDto;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.model.Account;
import com.monetra.core.model.RecurringTransaction;
import com.monetra.core.model.Transaction;
import com.monetra.core.model.TransactionType;
import com.monetra.core.repository.AccountRepository;
import com.monetra.core.repository.CategoryRepository;
import com.monetra.core.repository.RecurringTransactionRepository;
import com.monetra.core.repository.TransactionRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public RecurringTransactionService(RecurringTransactionRepository recurringTransactionRepository,
                                       TransactionRepository transactionRepository,
                                       AccountRepository accountRepository,
                                       CategoryRepository categoryRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<RecurringTransactionDto> getUserRecurringTransactions(Long userId) {
        return recurringTransactionRepository.findByUserId(userId)
                .stream()
                .map(RecurringTransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public RecurringTransactionDto getById(Long id, Long userId) {
        RecurringTransaction r = recurringTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring Transaction", id));
        return RecurringTransactionDto.fromEntity(r);
    }

    @Transactional
    public RecurringTransactionDto createRecurringTransaction(Long userId, RecurringTransactionCreateDto dto) {
        FinancialValidator.validatePositiveAmount(dto.getAmount(), "Amount");
        FinancialValidator.validateNotBlank(dto.getDescription(), "Description");
        FinancialValidator.validateDate(dto.getStartDate(), "Start Date");
        FinancialValidator.validateDateRange(dto.getStartDate(), dto.getEndDate());

        accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", dto.getAccountId()));
        categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        RecurringTransaction recurring = new RecurringTransaction();
        recurring.setUserId(userId);
        recurring.setAccountId(dto.getAccountId());
        recurring.setCategoryId(dto.getCategoryId());
        recurring.setAmount(dto.getAmount());
        recurring.setType(dto.getType());
        recurring.setFrequency(dto.getFrequency());
        recurring.setStartDate(dto.getStartDate());
        recurring.setEndDate(dto.getEndDate());
        recurring.setNextDueDate(dto.getStartDate());
        recurring.setLastProcessedDate(null);
        recurring.setDescription(dto.getDescription().trim());
        recurring.setActive(true);

        RecurringTransaction saved = recurringTransactionRepository.save(recurring);
        return getById(saved.getId(), userId);
    }

    @Transactional
    public List<Transaction> processDueTransactions(LocalDate processUpToDate) {
        LocalDate targetDate = processUpToDate != null ? processUpToDate : LocalDate.now();
        List<RecurringTransaction> dueList = recurringTransactionRepository.findDueRecurringTransactions(targetDate);
        List<Transaction> createdTransactions = new ArrayList<>();

        for (RecurringTransaction rule : dueList) {
            LocalDate dueDate = rule.getNextDueDate();

            // Prevent duplicate generation for same date
            if (rule.getLastProcessedDate() != null && rule.getLastProcessedDate().equals(dueDate)) {
                continue;
            }

            // Create Transaction instance
            Transaction t = new Transaction();
            t.setUserId(rule.getUserId());
            t.setAccountId(rule.getAccountId());
            t.setCategoryId(rule.getCategoryId());
            t.setAmount(rule.getAmount());
            t.setType(rule.getType());
            t.setTransactionDate(dueDate);
            t.setDescription("[Recurring] " + rule.getDescription());
            t.setNotes("Generated automatically from recurring rule #" + rule.getId());
            t.setRecurringInstance(true);
            t.setRecurringTransactionId(rule.getId());

            // Adjust account balance
            Account acc = accountRepository.findByIdAndUserId(rule.getAccountId(), rule.getUserId()).orElse(null);
            if (acc != null) {
                if (rule.getType() == TransactionType.INCOME) {
                    accountRepository.updateBalance(acc.getId(), acc.getBalance().add(rule.getAmount()));
                } else {
                    accountRepository.updateBalance(acc.getId(), acc.getBalance().subtract(rule.getAmount()));
                }
            }

            Transaction saved = transactionRepository.save(t);
            createdTransactions.add(saved);

            // Compute next due date
            LocalDate nextDue = rule.getFrequency().calculateNextDate(dueDate);
            rule.setLastProcessedDate(dueDate);
            rule.setNextDueDate(nextDue);

            if (rule.getEndDate() != null && nextDue.isAfter(rule.getEndDate())) {
                rule.setActive(false);
            }

            recurringTransactionRepository.update(rule);
        }

        return createdTransactions;
    }

    @Transactional
    public void deleteRecurringTransaction(Long id, Long userId) {
        recurringTransactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring Transaction", id));
        recurringTransactionRepository.delete(id, userId);
    }
}

