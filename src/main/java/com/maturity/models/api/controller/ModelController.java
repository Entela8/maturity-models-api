package com.maturity.models.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.model.Model;
import com.maturity.models.api.service.ModelService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
     public ResponseEntity<?> create(@RequestBody Model model/* , final Authentication authentication*/) {
          System.out.println("ciaooooo\n\n\n");
          try {
               //final String username = authentication.getName();
               String username = "john";
               Model createdModel = modelService.createModel(username, model);
               return ResponseEntity.status(HttpStatus.CREATED).body(createdModel);
          } catch (ResponseStatusException e) {
               return handleError(e, "model-0001", "Model title is empty");
          } catch (RuntimeException e) {
               return handleError(e, "model-0002", "Invalid parameters");
          }
     }

     @GetMapping("/test")
     public String getMethodName(@RequestParam String param) {
         return "hey";
     }
     
 
     private ResponseEntity<?> handleError(RuntimeException e, String errorCode, String message) {
         log.error("Error [{}]: {}", errorCode, e.getMessage());
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
     }

}