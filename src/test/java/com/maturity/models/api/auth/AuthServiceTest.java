package com.maturity.models.api.auth;

import com.maturity.models.api.controller.AuthController;
import com.maturity.models.api.model.User;
import com.maturity.models.api.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceTest {
     @Mock
     private AuthService authService;

     @InjectMocks
     private AuthController authController;

     @BeforeEach
     void setUp() {
          MockitoAnnotations.openMocks(this);
     }
    
}