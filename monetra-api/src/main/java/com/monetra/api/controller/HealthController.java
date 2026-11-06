package com.monetra.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health Check", description = "System health, uptime, and database connectivity monitoring")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping({"/health", "/api/v1/health"})
    @Operation(summary = "Application health status and database connectivity check")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "MONETRA Personal Finance Management API");
        response.put("version", "1.0.0");
        response.put("timestamp", Instant.now().toString());

        String dbStatus;
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            dbStatus = (result != null && result == 1) ? "CONNECTED" : "UNHEALTHY";
        } catch (Exception ex) {
            dbStatus = "DOWN: " + ex.getMessage();
        }
        response.put("database", dbStatus);

        return ResponseEntity.ok(response);
    }
}

