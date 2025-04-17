package com.maturity.models.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maturity.models.api.dto.SessionDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.requests.sessions.ActivateSessionRequest;
import com.maturity.models.api.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Slf4j
public class SessionController {
     
     private final SessionService sessionService;

     @PostMapping("/activate")
     public ResponseEntity<?> activateSession(@Validated @RequestBody ActivateSessionRequest session, final Authentication authentication) {
          try {
               final String username = authentication.getName();
               final SessionDTO activatedSession = sessionService.activateTeam(username, session);
               return ResponseEntity.status(HttpStatus.CREATED).body(activatedSession);
 
          } catch (IllegalArgumentException e) {
               log.error("Error in activating session: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "session0001", 
                    "Error in activating session", 
                    "Error in activating session"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error in activating session: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "session0002",   
                    "Unexpected error in activating session", 
                    "Unexpected error in activating session"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }

     @PostMapping("/deactivate")
     public ResponseEntity<?> deactivateSession(@Validated @RequestBody ActivateSessionRequest session, final Authentication authentication) {
         try {
             final String username = authentication.getName();
             final SessionDTO deactivatedSession = sessionService.deactivateSession(username, session);
             return ResponseEntity.status(HttpStatus.OK).body(deactivatedSession);
     
         } catch (IllegalArgumentException e) {
             log.error("Error in deactivating session: {}", e.getMessage(), e);
             ErrorResponse errorResponse = new ErrorResponse(
                 "session0003", 
                 "Error in deactivating session", 
                 "Invalid session or parameters"
             );
             return ResponseEntity.badRequest().body(errorResponse);
         } catch (RuntimeException e) {
             log.error("Unexpected error in deactivating session: {}", e.getMessage(), e);
             ErrorResponse errorResponse = new ErrorResponse(
                 "session0004",   
                 "Unexpected error in deactivating session", 
                 "Please try again later"
             );
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
         }
     }

     @GetMapping("/team/{id}")
     public ResponseEntity<?> getTeamSession(@Validated final Authentication authentication, @PathVariable String id) {
          try {
               final String username = authentication.getName();
               final List<SessionDTO> sessions = sessionService.getTeamSession(username, id);
               return ResponseEntity.status(HttpStatus.OK).body(sessions);
          } catch (IllegalArgumentException e) {
               log.error("Error fetching the sessions {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "session0001", 
                    "Error fetching the sessions", 
                    "Error fetching the sessions"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error during session fetching: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "session0002",   
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }

     @GetMapping("/{id}")
     public ResponseEntity<?> getActiveSessions(@Validated final Authentication authentication, @PathVariable String id) {
          try {
               final String username = authentication.getName();
               final List<SessionDTO> sessions = sessionService.getActiveSessions(username, id);
               return ResponseEntity.status(HttpStatus.OK).body(sessions);
          } catch (IllegalArgumentException e) {
               log.error("Error fetching active sessions: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "session0003", 
                    "Error fetching active sessions", 
                    "Error fetching active sessions"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error fetching active sessions: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0004",   
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }

     @GetMapping("/all")
     public ResponseEntity<?> getAllSessions(@Validated final Authentication authentication) {
          try {
               final String username = authentication.getName();
               final List<SessionDTO> sessions = sessionService.getAllSessions(username);
               return ResponseEntity.status(HttpStatus.OK).body(sessions);
          } catch (IllegalArgumentException e) {
               log.error("Error fetching all sessions: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "session0005", 
                    "Error fetching all sessions", 
                    "Error fetching all sessions"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error fetching all sessions: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0002",   
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }
     
}
