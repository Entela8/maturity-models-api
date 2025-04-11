package com.maturity.models.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.model.Session;
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
               final Session activatedSession = sessionService.activateTeam(username, session);
               return ResponseEntity.status(HttpStatus.CREATED).body(activatedSession);
 
          } catch (IllegalArgumentException e) {
               log.error("Team name already in use: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0001", 
                    "A team with this name already exists", 
                    "Choose a different team name"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error during team creation: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0002",   
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }
}
