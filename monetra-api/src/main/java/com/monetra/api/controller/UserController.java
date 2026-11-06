package com.monetra.api.controller;

import com.monetra.api.security.UserContext;
import com.monetra.core.dto.UserDto;
import com.monetra.core.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile")
    public ResponseEntity<UserDto> getProfile() {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(userService.getById(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserDto dto) {
        Long userId = UserContext.getRequiredUserId();
        return ResponseEntity.ok(userService.updateProfile(userId, dto.getFullName(), dto.getEmail(), dto.getCurrency()));
    }
}

