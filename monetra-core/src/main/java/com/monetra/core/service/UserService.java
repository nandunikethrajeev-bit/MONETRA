package com.monetra.core.service;

import com.monetra.core.dto.LoginRequest;
import com.monetra.core.dto.RegisterRequest;
import com.monetra.core.dto.UserDto;
import com.monetra.core.exception.AuthenticationException;
import com.monetra.core.exception.DuplicateResourceException;
import com.monetra.core.exception.ResourceNotFoundException;
import com.monetra.core.model.Account;
import com.monetra.core.model.AccountType;
import com.monetra.core.model.User;
import com.monetra.core.repository.AccountRepository;
import com.monetra.core.repository.UserRepository;
import com.monetra.core.validation.FinancialValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordService passwordService;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordService = passwordService;
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            if (request.getEmail() != null && request.getEmail().contains("@")) {
                request.setUsername(request.getEmail().split("@")[0]);
            }
        }
        FinancialValidator.validateNotBlank(request.getUsername(), "Username");
        FinancialValidator.validateNotBlank(request.getFullName(), "Full Name");
        FinancialValidator.validateEmail(request.getEmail());
        FinancialValidator.validatePassword(request.getPassword());

        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email '" + email + "' is already registered");
        }

        String passwordHash = passwordService.hashPassword(request.getPassword());

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setFullName(request.getFullName().trim());
        user.setCurrency(request.getCurrency() != null && !request.getCurrency().trim().isEmpty() ? request.getCurrency().trim().toUpperCase() : "USD");

        User savedUser = userRepository.save(user);

        // Automatically provision a default primary checking account
        Account defaultAccount = new Account();
        defaultAccount.setUserId(savedUser.getId());
        defaultAccount.setName("Primary Checking");
        defaultAccount.setType(AccountType.CHECKING);
        defaultAccount.setBalance(BigDecimal.ZERO);
        defaultAccount.setCurrency(savedUser.getCurrency());
        defaultAccount.setDescription("Default checking account");
        accountRepository.save(defaultAccount);

        return UserDto.fromEntity(savedUser);
    }

    public UserDto authenticate(LoginRequest request) {
        FinancialValidator.validateNotBlank(request.getUsername(), "Username");
        FinancialValidator.validateNotBlank(request.getPassword(), "Password");

        String identifier = request.getUsername().trim();
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordService.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return UserDto.fromEntity(user);
    }

    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return UserDto.fromEntity(user);
    }

    public UserDto getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserDto updateProfile(Long userId, String fullName, String email, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            FinancialValidator.validateEmail(email);
            String newEmail = email.trim().toLowerCase();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new DuplicateResourceException("Email '" + newEmail + "' is already in use");
            }
            user.setEmail(newEmail);
        }
        if (currency != null && !currency.trim().isEmpty()) {
            user.setCurrency(currency.trim().toUpperCase());
        }

        userRepository.update(user);
        return UserDto.fromEntity(user);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        FinancialValidator.validateNotBlank(currentPassword, "Current Password");
        FinancialValidator.validateNotBlank(newPassword, "New Password");
        FinancialValidator.validatePassword(newPassword);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!passwordService.matches(currentPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Current password does not match");
        }

        user.setPasswordHash(passwordService.hashPassword(newPassword));
        userRepository.update(user);
    }
}

