package com.maturity.models.api.user;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.maturity.models.api.controller.SessionController;
import com.maturity.models.api.controller.UserController;
import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.exception.UsernameAlreadyInUseException;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.User;
import com.maturity.models.api.requests.user.CreateUserRequest;
import com.maturity.models.api.service.SessionService;
import com.maturity.models.api.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
     @InjectMocks
     private SessionController sessionController;

     @Mock
     private SessionService sessionService;

     @Mock
     private Authentication authentication;

     @InjectMocks
     private UserController userController;

     @Mock
     private UserService userService;

     private CreateUserRequest createUserRequest;
     
     private User registeredUser;

     @BeforeEach
     void setup() {
          createUserRequest = new CreateUserRequest();
          createUserRequest.setUsername("johndoe");
          createUserRequest.setEmail("john@example.com");
          createUserRequest.setFirstName("John");
          createUserRequest.setLastName("Doe");
          createUserRequest.setPassword("securepass123");
          createUserRequest.setRole(Role.MEMBER);
          createUserRequest.setTeamId(1L);

          registeredUser = new User();
          registeredUser.setId(1L);
          registeredUser.setUsername("johndoe");
          registeredUser.setFirstName("John");
          registeredUser.setLastName("Doe");
          registeredUser.setEmail("john@example.com");
          registeredUser.setPassword("securepass123");
          registeredUser.setRole(Role.MEMBER);
     }

     @Test
     void register_Successful() {
          when(userService.register(createUserRequest)).thenReturn(registeredUser);

          ResponseEntity<?> response = userController.register(createUserRequest);

          assertEquals(HttpStatus.CREATED, response.getStatusCode());
          assertEquals(registeredUser, response.getBody());
     }

     @Test
     void register_UsernameAlreadyUsed() {
          when(userService.register(createUserRequest)).thenThrow(new UsernameAlreadyInUseException("Username exists"));

          ResponseEntity<?> response = userController.register(createUserRequest);

          assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
          ErrorResponse error = (ErrorResponse) response.getBody();
          assertNotNull(error);
          assertEquals("user0001", error.getError());
          assertEquals("Username already in use", error.getMessage());
     }

     @Test
     void register_UnexpectedException() {
          when(userService.register(createUserRequest)).thenThrow(new RuntimeException("DB is down"));

          ResponseEntity<?> response = userController.register(createUserRequest);

          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
          ErrorResponse error = (ErrorResponse) response.getBody();
          assertNotNull(error);
          assertEquals("user0002", error.getError());
          assertEquals("Unexpected error", error.getMessage());
     }

     @Test
     void getAllUsers_Successful() {
          when(authentication.getName()).thenReturn("adminUser");

          List<UserDTO> userList = List.of(
               new UserDTO() {{
                    setId(1L);
                    setUsername("johndoe");
                    setFirstName("John");
                    setLastName("Doe");
                    setEmail("john@example.com");
                    setRole(Role.MEMBER);
                    setTeam(1L);
               }}
          );

          when(userService.getAllUsers("adminUser")).thenReturn(userList);

          ResponseEntity<?> response = userController.getAllUsers(authentication);

          assertEquals(HttpStatus.OK, response.getStatusCode());
          assertEquals(userList, response.getBody());
     }

     @Test
     void getAllUsers_Error() {
          when(authentication.getName()).thenReturn("adminUser");
          when(userService.getAllUsers("adminUser")).thenThrow(new RuntimeException("Something went wrong"));

          ResponseEntity<?> response = userController.getAllUsers(authentication);

          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
          assertTrue(((String) response.getBody()).contains("Something went wrong"));
     }
}