package com.maturity.models.api.service;

import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.exception.InvalidCredentialsException;
import com.maturity.models.api.exception.InvalidRefreshTokenException;
import com.maturity.models.api.jwt.JwtTokenGenerator;
import com.maturity.models.api.jwt.JwtTokenValidator;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.UserRepository;

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
    private final UserRepository userRepository;
    
    public Map<String, Object> login(User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                Map<String, Object> responseData = new HashMap<>();
            
                // Authentication data
                responseData.put("token", jwtTokenGenerator.generateToken(user.getUsername()));
                responseData.put("refreshToken", jwtTokenGenerator.generateRefreshToken(user.getUsername()));
                responseData.put("type", "Bearer");

                User userData = userRepository.findByUsername(user.getUsername());
                // UserDTO data
                UserDTO userDTO = new UserDTO();
                userDTO.setId(userData.getId());
                userDTO.setUsername(userData.getUsername());
                userDTO.setFirstName(userData.getFirstName());
                userDTO.setLastName(userData.getLastName());
                userDTO.setEmail(userData.getEmail());
                userDTO.setRole(userData.getRole());
                if(userData.getTeam() != null) {
                    userDTO.setTeam(userData.getTeam().getId());
                }
                responseData.put("user", userDTO);
                return responseData;
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
