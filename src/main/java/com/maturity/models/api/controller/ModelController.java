package com.maturity.models.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.model.Model;
import com.maturity.models.api.requests.models.CreateModelRequest;
import com.maturity.models.api.service.ModelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
@Slf4j
public class ModelController {
     private final ModelService modelService;
    
     @PostMapping("/new")
     public ResponseEntity<?> create(@Validated @RequestBody CreateModelRequest model, final Authentication authentication) {
          try {
               final String username = authentication.getName();
               Model createdModel = modelService.createModel(username, model);
               return ResponseEntity.status(HttpStatus.CREATED).body(createdModel);
          } catch (ResponseStatusException e) {
               return ResponseEntity
               .status(e.getStatusCode())  // Uses the correct 403 status
               .body(Map.of("error", e.getStatusCode(), "message", e.getBody()));
          } catch (RuntimeException e) {
               return handleError(e, "model-0002", "Invalid parameters");
          }
     }

     @GetMapping("/test")
     public String getMethodName(@RequestParam String param) {
         return "hey";
     }
     
 
     private ResponseEntity<?> handleError(Exception e, String errorCode, String message) {
          return ResponseEntity.badRequest().body(Map.of("error", errorCode, "message", message));
     }      

}