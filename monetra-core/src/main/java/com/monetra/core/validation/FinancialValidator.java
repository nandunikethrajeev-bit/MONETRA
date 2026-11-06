package com.monetra.core.validation;

import com.monetra.core.exception.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public final class FinancialValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private FinancialValidator() {
    }

    public static void validatePositiveAmount(BigDecimal amount, String fieldName) {
        if (amount == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero");
        }
    }

    public static void validateNonNegativeAmount(BigDecimal amount, String fieldName) {
        if (amount == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(fieldName + " cannot be negative");
        }
    }

    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validateEmail(String email) {
        validateNotBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validatePassword(String password) {
        validateNotBlank(password, "Password");
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
    }

    public static void validateDate(LocalDate date, String fieldName) {
        if (date == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
    }

    public static void validateFutureOrToday(LocalDate date, String fieldName) {
        validateDate(date, fieldName);
        if (date.isBefore(LocalDate.now())) {
            throw new ValidationException(fieldName + " must be today or in the future");
        }
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }
    }

    public static void validateMonthAndYear(int month, int year) {
        if (month < 1 || month > 12) {
            throw new ValidationException("Month must be between 1 and 12");
        }
        if (year < 2000 || year > 2100) {
            throw new ValidationException("Year must be between 2000 and 2100");
        }
    }
}
