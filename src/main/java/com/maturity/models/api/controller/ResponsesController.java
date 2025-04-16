package com.maturity.models.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.model.Response;
import com.maturity.models.api.requests.responses.AddResponsesRequest;
import com.maturity.models.api.service.ResponseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/response")
@RequiredArgsConstructor
@Slf4j
public class ResponsesController {
     
     private final ResponseService responseService;

     @PostMapping("/add")
     public ResponseEntity<?> saveResponses(@Validated @RequestBody AddResponsesRequest responses, final Authentication authentication) {
          try {
               final String username = authentication.getName();
               responseService.saveResponses(username, responses);
               return ResponseEntity.status(HttpStatus.CREATED).body(responses);
 
          } catch (IllegalArgumentException e) {
               log.error("Error in saving the response: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "response0001", 
                    "Error in saving the responses", 
                    "Error in saving the responses"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error during responses creation: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "response0002",   
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }

     @GetMapping("/")
     public ResponseEntity<?> getResponsesOfModel(@RequestParam final Authentication authentication, String modelId, String sessionId) {
          try {
               final String username = authentication.getName();
               List<Response> responses = responseService.getResponsesOfModel(username, modelId, sessionId);
               return ResponseEntity.status(HttpStatus.CREATED).body(responses);
 
          } catch (IllegalArgumentException e) {
               log.error("Error in fetching responses: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "response0003", 
                    "Error in fetching responses", 
                    "Error in fetching responses"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error fetching responses: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "response0004",   
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }
     
}
