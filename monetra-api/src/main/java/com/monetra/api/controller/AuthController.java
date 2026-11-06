package com.monetra.api.controller;

import com.monetra.api.security.JwtTokenProvider;
import com.monetra.api.security.UserContext;
import com.monetra.core.dto.AuthResponse;
import com.monetra.core.dto.LoginRequest;
import com.monetra.core.dto.RegisterRequest;
import com.monetra.core.dto.UserDto;
import com.monetra.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registration, login, and identity verification endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user profile with hashed password and provisions a primary checking account.")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        UserDto userDto = userService.register(request);
        String token = jwtTokenProvider.generateToken(userDto.getId(), userDto.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, userDto, "Registration successful"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates username and password, returning a JWT Bearer token.")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        UserDto userDto = userService.authenticate(request);
        String token = jwtTokenProvider.generateToken(userDto.getId(), userDto.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, userDto, "Login successful"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<UserDto> getCurrentUser() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(userService.getById(userId));
    }
}
