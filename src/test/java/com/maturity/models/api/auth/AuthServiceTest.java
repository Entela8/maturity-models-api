package com.maturity.models.api.auth;

import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.exception.InvalidCredentialsException;
import com.maturity.models.api.exception.InvalidRefreshTokenException;
import com.maturity.models.api.jwt.JwtTokenGenerator;
import com.maturity.models.api.jwt.JwtTokenValidator;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.service.AuthService;
import com.maturity.models.api.service.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private JwtTokenValidator jwtTokenValidator;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_shouldReturnTokenAndUserData_whenCredentialsAreValid() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(jwtTokenGenerator.generateToken("johndoe")).thenReturn("access-token");
        when(jwtTokenGenerator.generateRefreshToken("johndoe")).thenReturn("refresh-token");

        User fullUser = new User();
        fullUser.setId(1L);
        fullUser.setUsername("johndoe");
        fullUser.setFirstName("John");
        fullUser.setLastName("Doe");
        fullUser.setEmail("john@example.com");
        fullUser.setRole(Role.MEMBER);
        Team team = new Team();
        team.setId(10L);
        fullUser.setTeam(team);

        when(userRepository.findByUsername("johndoe")).thenReturn(fullUser);

        Map<String, Object> result = authService.login(user);

        assertEquals("access-token", result.get("token"));
        assertEquals("refresh-token", result.get("refreshToken"));
        assertEquals("Bearer", result.get("type"));

        UserDTO userDTO = (UserDTO) result.get("user");
        assertEquals(1L, userDTO.getId());
        assertEquals("johndoe", userDTO.getUsername());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals("john@example.com", userDTO.getEmail());
        assertEquals(Role.MEMBER, userDTO.getRole());
        assertEquals(10L, userDTO.getTeam());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenAuthenticationFails() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Bad credentials"));


        assertThrows(InvalidCredentialsException.class, () -> authService.login(user));
    }

    @Test
    void refreshToken_shouldReturnNewToken_whenRefreshTokenIsValid() {
        String refreshToken = "valid-refresh-token";

        when(jwtTokenValidator.extractUsername(refreshToken)).thenReturn("johndoe");
        when(customUserDetailsService.loadUserByUsername("johndoe")).thenReturn(userDetails);
        when(jwtTokenValidator.validateToken(refreshToken, userDetails)).thenReturn(true);
        when(jwtTokenGenerator.generateToken("johndoe")).thenReturn("new-access-token");

        Map<String, Object> result = authService.refreshToken(refreshToken);

        assertEquals("new-access-token", result.get("token"));
        assertEquals("Bearer", result.get("type"));
    }

    @Test
    void refreshToken_shouldThrowInvalidRefreshTokenException_whenTokenIsInvalid() {
        String refreshToken = "invalid-refresh-token";

        when(jwtTokenValidator.extractUsername(refreshToken)).thenReturn("johndoe");
        when(customUserDetailsService.loadUserByUsername("johndoe")).thenReturn(userDetails);
        when(jwtTokenValidator.validateToken(refreshToken, userDetails)).thenReturn(false);

        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(refreshToken));
    }
}
