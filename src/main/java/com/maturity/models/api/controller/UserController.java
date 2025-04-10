package com.maturity.models.api.controller;

import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.exception.UsernameAlreadyInUseException;
import com.maturity.models.api.model.User;
import com.maturity.models.api.requests.user.CreateUserRequest;
import com.maturity.models.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.maturity.models.api.dto.UserDTO;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CreateUserRequest user) {
        try {
            User registeredUser = userService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (UsernameAlreadyInUseException e) {
            log.error("Username already in use: {}", e.getMessage(), e);
            ErrorResponse errorResponse = new ErrorResponse(
                "user0001", 
                "Username already in use", 
                "Choose a different username"
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            log.error("Unexpected error during registration: {}", e.getMessage(), e);
            ErrorResponse errorResponse = new ErrorResponse(
                "user0002", 
                "Unexpected error", 
                "Please try again later"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<UserDTO> users = userService.getAllUsers(username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
    
}