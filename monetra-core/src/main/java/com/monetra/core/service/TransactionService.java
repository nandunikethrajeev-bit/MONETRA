package com.monetra.core.service;

import com.monetra.core.dto.TransactionCreateDto;
import com.monetra.core.dto.TransactionDto;
import com.monetra.core.dto.TransactionFilterDto;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.model.Account;
import com.monetra.core.model.Transaction;
import com.monetra.core.model.TransactionType;
import com.monetra.core.repository.AccountRepository;
import com.monetra.core.repository.CategoryRepository;
import com.monetra.core.repository.TransactionRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionDto> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getFilteredTransactions(Long userId, TransactionFilterDto filter) {
        return transactionRepository.findFiltered(userId, filter)
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getRecentTransactions(Long userId, int limit) {
        return transactionRepository.findRecent(userId, limit)
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(Long transactionId, Long userId) {
        Transaction t = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
        return TransactionDto.fromEntity(t);
    }

    @Transactional
    public TransactionDto createTransaction(Long userId, TransactionCreateDto dto) {
        FinancialValidator.validatePositiveAmount(dto.getAmount(), "Transaction Amount");
        FinancialValidator.validateNotBlank(dto.getDescription(), "Description");
        FinancialValidator.validateDate(dto.getTransactionDate(), "Transaction Date");

        // Validate Account ownership
        Account account = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", dto.getAccountId()));

        // Validate Category exists
        categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAccountId(dto.getAccountId());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription().trim());
        transaction.setNotes(dto.getNotes());
        transaction.setRecurringInstance(false);
        transaction.setRecurringTransactionId(null);

        // Adjust Account Balance
        if (dto.getType() == TransactionType.INCOME) {
            BigDecimal newBal = account.getBalance().add(dto.getAmount());
            accountRepository.updateBalance(account.getId(), newBal);
        } else {
            BigDecimal newBal = account.getBalance().subtract(dto.getAmount());
            accountRepository.updateBalance(account.getId(), newBal);
        }

        Transaction saved = transactionRepository.save(transaction);
        return getTransactionById(saved.getId(), userId);
    }

    @Transactional
    public TransactionDto updateTransaction(Long transactionId, Long userId, TransactionCreateDto dto) {
        Transaction existing = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        FinancialValidator.validatePositiveAmount(dto.getAmount(), "Transaction Amount");
        FinancialValidator.validateNotBlank(dto.getDescription(), "Description");
        FinancialValidator.validateDate(dto.getTransactionDate(), "Transaction Date");

        Account oldAccount = accountRepository.findByIdAndUserId(existing.getAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", existing.getAccountId()));

        // Revert old transaction from old account balance
        if (existing.getType() == TransactionType.INCOME) {
            accountRepository.updateBalance(oldAccount.getId(), oldAccount.getBalance().subtract(existing.getAmount()));
        } else {
            accountRepository.updateBalance(oldAccount.getId(), oldAccount.getBalance().add(existing.getAmount()));
        }

        // Apply to target account
        Account targetAccount;
        if (dto.getAccountId().equals(oldAccount.getId())) {
            // Re-fetch old account with reverted balance
            targetAccount = accountRepository.findByIdAndUserId(oldAccount.getId(), userId).orElseThrow();
        } else {
            targetAccount = accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", dto.getAccountId()));
        }

        if (dto.getType() == TransactionType.INCOME) {
            accountRepository.updateBalance(targetAccount.getId(), targetAccount.getBalance().add(dto.getAmount()));
        } else {
            accountRepository.updateBalance(targetAccount.getId(), targetAccount.getBalance().subtract(dto.getAmount()));
        }

        categoryRepository.findByIdAndUserId(dto.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));

        existing.setAccountId(dto.getAccountId());
        existing.setCategoryId(dto.getCategoryId());
        existing.setAmount(dto.getAmount());
        existing.setType(dto.getType());
        existing.setTransactionDate(dto.getTransactionDate());
        existing.setDescription(dto.getDescription().trim());
        existing.setNotes(dto.getNotes());

        transactionRepository.update(existing);
        return getTransactionById(transactionId, userId);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction existing = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));

        Account account = accountRepository.findByIdAndUserId(existing.getAccountId(), userId).orElse(null);
        if (account != null) {
            if (existing.getType() == TransactionType.INCOME) {
                accountRepository.updateBalance(account.getId(), account.getBalance().subtract(existing.getAmount()));
            } else {
                accountRepository.updateBalance(account.getId(), account.getBalance().add(existing.getAmount()));
            }
        }

        transactionRepository.delete(transactionId, userId);
    }
}

