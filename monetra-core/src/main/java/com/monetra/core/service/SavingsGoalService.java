package com.monetra.core.service;

import com.monetra.core.dto.SavingsContributionDto;
import com.monetra.core.dto.SavingsGoalCreateDto;
import com.monetra.core.dto.SavingsGoalDto;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.model.Account;
import com.monetra.core.model.GoalStatus;
import com.monetra.core.model.SavingsGoal;
import com.monetra.core.repository.AccountRepository;
import com.monetra.core.repository.SavingsGoalRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final AccountRepository accountRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository, AccountRepository accountRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.accountRepository = accountRepository;
    }

    public List<SavingsGoalDto> getUserSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserId(userId)
                .stream()
                .map(SavingsGoalDto::fromEntity)
                .collect(Collectors.toList());
    }

    public SavingsGoalDto getSavingsGoalById(Long goalId, Long userId) {
        SavingsGoal g = savingsGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", goalId));
        return SavingsGoalDto.fromEntity(g);
    }

    @Transactional
    public SavingsGoalDto createSavingsGoal(Long userId, SavingsGoalCreateDto dto) {
        FinancialValidator.validateNotBlank(dto.getName(), "Goal Name");
        FinancialValidator.validatePositiveAmount(dto.getTargetAmount(), "Target Amount");
        FinancialValidator.validateFutureOrToday(dto.getTargetDate(), "Target Date");

        if (dto.getAccountId() != null) {
            accountRepository.findByIdAndUserId(dto.getAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", dto.getAccountId()));
        }

        SavingsGoal goal = new SavingsGoal();
        goal.setUserId(userId);
        goal.setAccountId(dto.getAccountId());
        goal.setName(dto.getName().trim());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getInitialAmount() != null ? dto.getInitialAmount() : BigDecimal.ZERO);
        goal.setTargetDate(dto.getTargetDate());
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goal.setNotes(dto.getNotes());

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        SavingsGoal saved = savingsGoalRepository.save(goal);
        return getSavingsGoalById(saved.getId(), userId);
    }

    @Transactional
    public SavingsGoalDto recordContribution(Long goalId, Long userId, SavingsContributionDto contributionDto) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", goalId));

        FinancialValidator.validatePositiveAmount(contributionDto.getAmount(), "Contribution Amount");

        // If a source account is specified, deduct the contribution from that account
        if (contributionDto.getSourceAccountId() != null) {
            Account sourceAccount = accountRepository.findByIdAndUserId(contributionDto.getSourceAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", contributionDto.getSourceAccountId()));

            BigDecimal newBal = sourceAccount.getBalance().subtract(contributionDto.getAmount());
            accountRepository.updateBalance(sourceAccount.getId(), newBal);
        }

        BigDecimal newAmount = goal.getCurrentAmount().add(contributionDto.getAmount());
        GoalStatus status = goal.getStatus();
        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            status = GoalStatus.COMPLETED;
        }

        savingsGoalRepository.updateAmount(goalId, newAmount, status);
        return getSavingsGoalById(goalId, userId);
    }

    @Transactional
    public SavingsGoalDto updateSavingsGoal(Long goalId, Long userId, SavingsGoalCreateDto dto) {
        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", goalId));

        FinancialValidator.validateNotBlank(dto.getName(), "Goal Name");
        FinancialValidator.validatePositiveAmount(dto.getTargetAmount(), "Target Amount");
        FinancialValidator.validateFutureOrToday(dto.getTargetDate(), "Target Date");

        goal.setName(dto.getName().trim());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setTargetDate(dto.getTargetDate());
        goal.setAccountId(dto.getAccountId());
        goal.setNotes(dto.getNotes());

        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        } else if (goal.getStatus() == GoalStatus.COMPLETED) {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        savingsGoalRepository.update(goal);
        return getSavingsGoalById(goalId, userId);
    }

    @Transactional
    public void deleteSavingsGoal(Long goalId, Long userId) {
        savingsGoalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Savings Goal", goalId));
        savingsGoalRepository.delete(goalId, userId);
    }
}
