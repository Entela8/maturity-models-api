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

class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Successful() {
        User inputUser = new User();
        inputUser.setUsername("carlo");
        inputUser.setPassword("mdp");

        Map<String, Object> mockResponse = Map.of(
            "type", "Bearer",
            "user", Map.of(
                "id", 10,
                "username", "carlo",
                "firstName", "Carlo",
                "lastName", "Conti",
                "email", "cc@gmail.com",
                "role", "MEMBER",
                "team", 3
            ),
            "token", "mocked-jwt-token",
            "refreshToken", "mocked-refresh-token"
        );

        when(authService.login(inputUser)).thenReturn(mockResponse);

        ResponseEntity<?> response = authController.login(inputUser);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertThat(responseBody.get("type")).isEqualTo("Bearer");
        assertThat(((Map<?, ?>) responseBody.get("user")).get("username")).isEqualTo("carlo");
        assertThat(responseBody.get("token")).isEqualTo("mocked-jwt-token");
        assertThat(responseBody.get("refreshToken")).isEqualTo("mocked-refresh-token");
    }
    

    @Test
    void login_Failure() {
        User inputUser = new User();
        inputUser.setUsername("invalid");
        inputUser.setPassword("wrongpass");
    
        when(authService.login(inputUser)).thenThrow(new RuntimeException("Invalid credentials"));
    
        ResponseEntity<?> response = authController.login(inputUser);
    
        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }
    
}