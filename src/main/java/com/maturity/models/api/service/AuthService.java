package com.maturity.models.api.service;

import com.maturity.models.api.exception.InvalidCredentialsException;
import com.maturity.models.api.exception.InvalidRefreshTokenException;
import com.maturity.models.api.jwt.JwtTokenGenerator;
import com.maturity.models.api.jwt.JwtTokenValidator;
import com.maturity.models.api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenGenerator jwtTokenGenerator;
    private final JwtTokenValidator jwtTokenValidator;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    
    public Map<String, Object> login(User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtTokenGenerator.generateToken(user.getUsername()));
                authData.put("refreshToken", jwtTokenGenerator.generateRefreshToken(user.getUsername()));
                authData.put("type", "Bearer");
                return authData;
            }
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password", e);
        }
    }

    public Map<String, Object> refreshToken(String refreshToken) {
        String username = jwtTokenValidator.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (!jwtTokenValidator.validateToken(refreshToken, userDetails)) { 
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        String newAccessToken = jwtTokenGenerator.generateToken(username);

        Map<String, Object> authData = new HashMap<>();
        authData.put("token", newAccessToken);
        authData.put("type", "Bearer");

        return authData;
    }
}
