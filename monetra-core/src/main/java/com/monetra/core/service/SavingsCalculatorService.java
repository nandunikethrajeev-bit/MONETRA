package com.monetra.core.service;

import com.monetra.core.dto.WhatIfSavingsCalculationRequest;
import com.monetra.core.dto.WhatIfSavingsCalculationResult;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class SavingsCalculatorService {

    private static final MathContext MC = MathContext.DECIMAL128;

    public WhatIfSavingsCalculationResult calculateRequiredSavings(WhatIfSavingsCalculationRequest request) {
        FinancialValidator.validatePositiveAmount(request.getTargetAmount(), "Target Amount");
        if (request.getDurationMonths() == null || request.getDurationMonths() <= 0) {
            throw new IllegalArgumentException("Duration in months must be at least 1");
        }

        BigDecimal targetAmount = request.getTargetAmount().setScale(2, RoundingMode.HALF_UP);
        int months = request.getDurationMonths();
        BigDecimal initialDeposit = request.getInitialDeposit() != null
                ? request.getInitialDeposit().setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal annualRate = request.getAnnualInterestRatePercentage() != null
                ? request.getAnnualInterestRatePercentage()
                : BigDecimal.ZERO;

        if (initialDeposit.compareTo(targetAmount) >= 0) {
            return new WhatIfSavingsCalculationResult(
                    request.getGoalName(),
                    targetAmount,
                    months,
                    initialDeposit,
                    annualRate,
                    BigDecimal.ZERO,
                    initialDeposit,
                    BigDecimal.ZERO,
                    targetAmount
            );
        }

        BigDecimal requiredMonthly;
        BigDecimal totalPrincipal;
        BigDecimal totalInterest;
        BigDecimal finalAccumulated;

        if (annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            // Zero interest calculation
            BigDecimal remainingToSave = targetAmount.subtract(initialDeposit);
            requiredMonthly = remainingToSave.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
            totalPrincipal = initialDeposit.add(requiredMonthly.multiply(BigDecimal.valueOf(months)));
            totalInterest = BigDecimal.ZERO;
            finalAccumulated = totalPrincipal;
        } else {
            // Compound monthly interest calculation
            // monthlyRate = annualRate / 100 / 12
            BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), MC);

            // (1 + r)^n
            BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate, MC);
            BigDecimal compoundFactor = onePlusR.pow(months, MC);

            // Future value of initial deposit
            BigDecimal fvInitial = initialDeposit.multiply(compoundFactor, MC);

            // Remaining target to cover with annuity
            BigDecimal remainingFv = targetAmount.subtract(fvInitial, MC);

            if (remainingFv.compareTo(BigDecimal.ZERO) <= 0) {
                requiredMonthly = BigDecimal.ZERO;
                totalPrincipal = initialDeposit;
                finalAccumulated = fvInitial.setScale(2, RoundingMode.HALF_UP);
                totalInterest = finalAccumulated.subtract(totalPrincipal);
            } else {
                // Annuity factor: ((1 + r)^n - 1) / r
                BigDecimal annuityFactor = compoundFactor.subtract(BigDecimal.ONE, MC).divide(monthlyRate, MC);

                requiredMonthly = remainingFv.divide(annuityFactor, 2, RoundingMode.HALF_UP);
                totalPrincipal = initialDeposit.add(requiredMonthly.multiply(BigDecimal.valueOf(months))).setScale(2, RoundingMode.HALF_UP);

                finalAccumulated = targetAmount;
                totalInterest = finalAccumulated.subtract(totalPrincipal);
                if (totalInterest.compareTo(BigDecimal.ZERO) < 0) {
                    totalInterest = BigDecimal.ZERO;
                }
            }
        }

        return new WhatIfSavingsCalculationResult(
                request.getGoalName(),
                targetAmount,
                months,
                initialDeposit,
                annualRate,
                requiredMonthly,
                totalPrincipal,
                totalInterest,
                finalAccumulated
        );
    }
}

