package com.maturity.models.api.controller;

import com.maturity.models.api.model.User;
import com.maturity.models.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AuthController {
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Map<String, Object> authData = authService.login(user);
            return ResponseEntity.ok(authData);
        } catch (RuntimeException e) {
            return handleAuthError(
                e, 
                "auth-0001", 
                "Invalid credentials or authentication failure"
            );
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No refresh token provided");
        }

        try {
            Map<String, Object> authData = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(authData);
        } catch (RuntimeException e) {
            return handleAuthError(
                e, 
                "auth-0002", 
                "Token refresh failure"
            );
        }
    }

    private ResponseEntity<?> handleAuthError(Exception e, String errorCode, String userMessage) {
        log.error("Authentication error: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse(
                    errorCode, 
                    e.getMessage(), 
                    userMessage
                ));
    }

    private Map<String, String> createErrorResponse(String errorCode, String developerMessage, String userMessage) {
        return Map.of(
                "errorCode", errorCode,
                "developerMessage", developerMessage,
                "userMessage", userMessage
        );
    }
}