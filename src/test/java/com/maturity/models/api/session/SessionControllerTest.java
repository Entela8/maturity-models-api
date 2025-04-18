package com.maturity.models.api.session;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.maturity.models.api.controller.SessionController;
import com.maturity.models.api.dto.SessionDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.requests.sessions.ActivateSessionRequest;
import com.maturity.models.api.service.SessionService;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {
     @InjectMocks
     private SessionController sessionController;

     @Mock
     private SessionService sessionService;

     @Mock
     private Authentication authentication;

     @Test
     void activateSession_Successful() {
          ActivateSessionRequest request = new ActivateSessionRequest();
          // populate the request with necessary data
          request.setModelId(1L);
          request.setTeamId(2L);

          SessionDTO expectedSession = new SessionDTO();
          expectedSession.setId(10L);
          expectedSession.setModelId(1L);
          expectedSession.setTeamId(2L);
          expectedSession.setActive(true);
          expectedSession.setDate(new Date());
          expectedSession.setTeamName("Team Alpha");

          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.activateTeam("carlo", request)).thenReturn(expectedSession);

          ResponseEntity<?> response = sessionController.activateSession(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
          assertThat(response.getBody()).isEqualTo(expectedSession);

          verify(sessionService, times(1)).activateTeam("carlo", request);
     }

     @Test
     void activateSession_InvalidInput() {
          ActivateSessionRequest request = new ActivateSessionRequest();
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.activateTeam("carlo", request))
               .thenThrow(new IllegalArgumentException("Team not found"));

          ResponseEntity<?> response = sessionController.activateSession(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0001");
          assertThat(error.getMessage()).isEqualTo("Error in activating session");
     }

     @Test
     void activateSession_UnexpectedError() {
          ActivateSessionRequest request = new ActivateSessionRequest();
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.activateTeam("carlo", request))
               .thenThrow(new RuntimeException("DB unavailable"));

          ResponseEntity<?> response = sessionController.activateSession(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0002");
          assertThat(error.getMessage()).isEqualTo("Unexpected error in activating session");
     }  

     @Test
     void deactivateSession_Successful() {
          ActivateSessionRequest request = new ActivateSessionRequest();
          request.setModelId(1L);
          request.setTeamId(2L);

          SessionDTO expectedSession = new SessionDTO();
          expectedSession.setId(10L);
          expectedSession.setModelId(1L);
          expectedSession.setTeamId(2L);
          expectedSession.setActive(false);
          expectedSession.setDate(new Date());
          expectedSession.setTeamName("Team Beta");

          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.deactivateSession("carlo", request)).thenReturn(expectedSession);

          ResponseEntity<?> response = sessionController.deactivateSession(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo(expectedSession);
     }

     @Test
     void deactivateSession_InvalidInput() {
          ActivateSessionRequest request = new ActivateSessionRequest();
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.deactivateSession("carlo", request))
               .thenThrow(new IllegalArgumentException("Invalid session ID"));

          ResponseEntity<?> response = sessionController.deactivateSession(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0003");
          assertThat(error.getMessage()).isEqualTo("Error in deactivating session");
     }

     @Test
     void deactivateSession_UnexpectedError() {
          ActivateSessionRequest request = new ActivateSessionRequest();
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.deactivateSession("carlo", request))
               .thenThrow(new RuntimeException("Database connection lost"));

          ResponseEntity<?> response = sessionController.deactivateSession(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0004");
          assertThat(error.getMessage()).isEqualTo("Unexpected error in deactivating session");
     }

     @Test
     void getTeamSession_Successful() {
          List<SessionDTO> mockSessions = List.of(
               new SessionDTO() {{
                    setId(1L);
                    setModelId(101L);
                    setTeamId(201L);
                    setActive(true);
                    setDate(new Date());
                    setTeamName("Team Gamma");
               }},
               new SessionDTO() {{
                    setId(2L);
                    setModelId(102L);
                    setTeamId(202L);
                    setActive(false);
                    setDate(new Date());
                    setTeamName("Team Gamma");
               }}
          );

          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getTeamSession("carlo", "5")).thenReturn(mockSessions);

          ResponseEntity<?> response = sessionController.getTeamSession(authentication, "5");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo(mockSessions);
     }

     @Test
     void getTeamSession_InvalidInput() {
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getTeamSession("carlo", "5"))
               .thenThrow(new IllegalArgumentException("Invalid team ID"));

          ResponseEntity<?> response = sessionController.getTeamSession(authentication, "5");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0001");
          assertThat(error.getMessage()).isEqualTo("Error fetching the sessions");
     }

     @Test
     void getTeamSession_UnexpectedError() {
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getTeamSession("carlo", "5"))
               .thenThrow(new RuntimeException("Service unavailable"));

          ResponseEntity<?> response = sessionController.getTeamSession(authentication, "5");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0002");
          assertThat(error.getMessage()).isEqualTo("Unexpected error");
     }

     @Test
     void getActiveSessions_Successful() {
          List<SessionDTO> mockSessions = List.of(
               new SessionDTO() {{
                    setId(1L);
                    setModelId(101L);
                    setTeamId(201L);
                    setActive(true);
                    setDate(new Date());
                    setTeamName("Alpha");
               }}
          );

          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getActiveSessions("carlo", "5")).thenReturn(mockSessions);

          ResponseEntity<?> response = sessionController.getActiveSessions(authentication, "5");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo(mockSessions);
     }

     @Test
     void getActiveSessions_InvalidInput() {
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getActiveSessions("carlo", "5"))
               .thenThrow(new IllegalArgumentException("Invalid ID"));

          ResponseEntity<?> response = sessionController.getActiveSessions(authentication, "5");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0003");
          assertThat(error.getMessage()).isEqualTo("Error fetching active sessions");
     }

     @Test
     void getActiveSessions_UnexpectedError() {
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getActiveSessions("carlo", "5"))
               .thenThrow(new RuntimeException("Something went wrong"));

          ResponseEntity<?> response = sessionController.getActiveSessions(authentication, "5");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("team0004");
          assertThat(error.getMessage()).isEqualTo("Unexpected error");
     }

     @Test
     void getAllSessions_Successful() {
          List<SessionDTO> mockSessions = List.of(
               new SessionDTO() {{
                    setId(3L);
                    setModelId(103L);
                    setTeamId(203L);
                    setActive(false);
                    setDate(new Date());
                    setTeamName("Zeta");
               }}
          );

          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getAllSessions("carlo")).thenReturn(mockSessions);

          ResponseEntity<?> response = sessionController.getAllSessions(authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo(mockSessions);
     }

     @Test
     void getAllSessions_InvalidInput() {
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getAllSessions("carlo"))
               .thenThrow(new IllegalArgumentException("Failed to parse"));

          ResponseEntity<?> response = sessionController.getAllSessions(authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("session0005");
          assertThat(error.getMessage()).isEqualTo("Error fetching all sessions");
     }

     @Test
     void getAllSessions_UnexpectedError() {
          when(authentication.getName()).thenReturn("carlo");
          when(sessionService.getAllSessions("carlo"))
               .thenThrow(new RuntimeException("Boom!"));

          ResponseEntity<?> response = sessionController.getAllSessions(authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("team0002");
          assertThat(error.getMessage()).isEqualTo("Unexpected error");
     }


}
