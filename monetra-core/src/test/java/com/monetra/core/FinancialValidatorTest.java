package com.monetra.core;

import com.monetra.core.exception.ValidationException;
import com.monetra.core.validation.FinancialValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinancialValidatorTest {

    @Test
    @DisplayName("Validate positive amounts")
    void testValidatePositiveAmount() {
        assertThatCode(() -> FinancialValidator.validatePositiveAmount(new BigDecimal("10.50"), "Amount"))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> FinancialValidator.validatePositiveAmount(BigDecimal.ZERO, "Amount"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("must be greater than zero");

        assertThatThrownBy(() -> FinancialValidator.validatePositiveAmount(new BigDecimal("-5.00"), "Amount"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("must be greater than zero");

        assertThatThrownBy(() -> FinancialValidator.validatePositiveAmount(null, "Amount"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Validate email format")
    void testValidateEmail() {
        assertThatCode(() -> FinancialValidator.validateEmail("user@example.com")).doesNotThrowAnyException();
        assertThatCode(() -> FinancialValidator.validateEmail("john.doe+finance@bank.co.uk")).doesNotThrowAnyException();

        assertThatThrownBy(() -> FinancialValidator.validateEmail("plainaddress"))
                .isInstanceOf(ValidationException.class);

        assertThatThrownBy(() -> FinancialValidator.validateEmail("user@.com"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Validate password length")
    void testValidatePassword() {
        assertThatCode(() -> FinancialValidator.validatePassword("secret123")).doesNotThrowAnyException();

        assertThatThrownBy(() -> FinancialValidator.validatePassword("12345"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("at least 6 characters");
    }

    @Test
    @DisplayName("Validate date ranges")
    void testValidateDateRange() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        assertThatCode(() -> FinancialValidator.validateDateRange(start, end)).doesNotThrowAnyException();

        assertThatThrownBy(() -> FinancialValidator.validateDateRange(end, start))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Start date cannot be after end date");
    }
}
