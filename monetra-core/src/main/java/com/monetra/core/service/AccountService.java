package com.monetra.core.service;

import com.monetra.core.dto.AccountCreateDto;
import com.monetra.core.dto.AccountDto;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.model.Account;
import com.monetra.core.repository.AccountRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountDto> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountById(Long accountId, Long userId) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        return AccountDto.fromEntity(account);
    }

    @Transactional
    public AccountDto createAccount(Long userId, AccountCreateDto dto) {
        FinancialValidator.validateNotBlank(dto.getName(), "Account Name");
        if (dto.getInitialBalance() != null) {
            FinancialValidator.validateNonNegativeAmount(dto.getInitialBalance(), "Initial Balance");
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setName(dto.getName().trim());
        account.setType(dto.getType());
        account.setBalance(dto.getInitialBalance() != null ? dto.getInitialBalance() : BigDecimal.ZERO);
        account.setCurrency(dto.getCurrency() != null && !dto.getCurrency().trim().isEmpty() ? dto.getCurrency().trim().toUpperCase() : "USD");
        account.setDescription(dto.getDescription());

        Account saved = accountRepository.save(account);
        return AccountDto.fromEntity(saved);
    }

    @Transactional
    public AccountDto updateAccount(Long accountId, Long userId, AccountCreateDto dto) {
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));

        FinancialValidator.validateNotBlank(dto.getName(), "Account Name");
        account.setName(dto.getName().trim());
        if (dto.getType() != null) {
            account.setType(dto.getType());
        }
        if (dto.getCurrency() != null && !dto.getCurrency().trim().isEmpty()) {
            account.setCurrency(dto.getCurrency().trim().toUpperCase());
        }
        account.setDescription(dto.getDescription());

        accountRepository.update(account);
        return AccountDto.fromEntity(account);
    }

    @Transactional
    public void deleteAccount(Long accountId, Long userId) {
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
        accountRepository.delete(accountId, userId);
    }

    public BigDecimal calculateTotalNetWorth(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

