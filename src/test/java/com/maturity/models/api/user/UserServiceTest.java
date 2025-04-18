package com.maturity.models.api.user;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.controller.SessionController;
import com.maturity.models.api.controller.UserController;
import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.exception.UsernameAlreadyInUseException;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.requests.user.CreateUserRequest;
import com.maturity.models.api.service.SessionService;
import com.maturity.models.api.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

     @Mock
     private UserRepository userRepository;

     @Mock
     private TeamRepository teamRepository;

     @Mock
     private PasswordEncoder passwordEncoder;

     @InjectMocks
     private UserService userService;

     private CreateUserRequest createUserRequest;
     private User user;

     @BeforeEach
     void setup() {
          createUserRequest = new CreateUserRequest();
          createUserRequest.setUsername("johndoe");
          createUserRequest.setEmail("john@example.com");
          createUserRequest.setFirstName("John");
          createUserRequest.setLastName("Doe");
          createUserRequest.setPassword("password123");
          createUserRequest.setRole(Role.MEMBER);

          user = new User();
          user.setId(1L);
          user.setUsername("johndoe");
          user.setEmail("john@example.com");
          user.setFirstName("John");
          user.setLastName("Doe");
          user.setRole(Role.MEMBER);
     }

     @Test
     void register_Successful() {
          when(userRepository.findByUsername("johndoe")).thenReturn(null);
          when(passwordEncoder.encode("password123")).thenReturn("hashedpass");
          when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

          User result = userService.register(createUserRequest);

          assertEquals("johndoe", result.getUsername());
          assertEquals("hashedpass", result.getPassword());
          verify(userRepository).save(any(User.class));
     }

     @Test
     void register_WithTeamId() {
          createUserRequest.setTeamId(1L);
          Team team = new Team();
          team.setId(1L);

          when(userRepository.findByUsername("johndoe")).thenReturn(null);
          when(passwordEncoder.encode("password123")).thenReturn("hashedpass");
          when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
          when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

          User result = userService.register(createUserRequest);

          assertNotNull(result.getTeam());
          assertEquals(1L, result.getTeam().getId());
     }

     @Test
     void register_Throws_UsernameAlreadyInUse() {
          when(userRepository.findByUsername("johndoe")).thenReturn(user);

          assertThrows(UsernameAlreadyInUseException.class, () -> userService.register(createUserRequest));
     }

     @Test
     void getUserDetails_ReturnsUser() {
          when(userRepository.findByUsername("johndoe")).thenReturn(user);

          User result = userService.getUserDetails("johndoe");

          assertEquals("johndoe", result.getUsername());
     }

     @Test
     void getAllUsers_UserNotFound() {
          when(userRepository.findByUsername("unknown")).thenReturn(null);

          assertThrows(ResponseStatusException.class, () -> userService.getAllUsers("unknown"));
     }

     @Test
     void getAllUsers_ForbiddenAccess() {
          user.setRole(Role.MEMBER);
          when(userRepository.findByUsername("johndoe")).thenReturn(user);

          assertThrows(ResponseStatusException.class, () -> userService.getAllUsers("johndoe"));
     }

     @Test
     void getAllUsers_ReturnsListForAdmin() {
          user.setRole(Role.ADMIN);
          when(userRepository.findByUsername("admin")).thenReturn(user);

          User user2 = new User();
          user2.setId(2L);
          user2.setUsername("jane");
          user2.setFirstName("Jane");
          user2.setLastName("Smith");
          user2.setEmail("jane@example.com");
          user2.setRole(Role.MEMBER);
          user2.setTeam(new Team() {{ setId(99L); }});

          when(userRepository.findAll()).thenReturn(List.of(user, user2));

          List<UserDTO> result = userService.getAllUsers("admin");

          assertEquals(2, result.size());
          assertEquals("jane", result.get(1).getUsername());
          assertEquals(99L, result.get(1).getTeam());
     }

     @Test
     void ensureUserIsAllowed_ThrowsIfNotAllowed() {
          user.setRole(Role.MEMBER);
          when(userRepository.findByUsername("johndoe")).thenReturn(user);

          assertThrows(ResponseStatusException.class, () -> userService.ensureUserIsAllowed("johndoe"));
     }

     @Test
     void ensureUserIsAllowed_AllowsAdmin() {
          user.setRole(Role.ADMIN);
          when(userRepository.findByUsername("admin")).thenReturn(user);

          assertDoesNotThrow(() -> userService.ensureUserIsAllowed("admin"));
     }
}
