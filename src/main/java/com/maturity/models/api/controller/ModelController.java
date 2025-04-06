package com.maturity.models.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.dto.ModelDTO;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.requests.models.CreateModelRequest;
import com.maturity.models.api.service.ModelService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
               .body(Map.of("model-01", e.getStatusCode(), "message", e.getBody()));
          } catch (RuntimeException e) {
               return handleError(e, "model-02", "Invalid parameters");
          }
     }

     @GetMapping("/all")
     public ResponseEntity<?> getAllModels(Authentication authentication) {
          try {
               final String username = authentication.getName();
               List<ModelDTO> modelDTOs = modelService.getAllModels(username);
               return ResponseEntity.status(HttpStatus.OK).body(modelDTOs);
          } catch (ResponseStatusException e) {
               return ResponseEntity
               .status(e.getStatusCode())
               .body(Map.of("model-03", e.getStatusCode(), "message", e.getBody()));
          } catch (RuntimeException e) {
               return handleError(e, HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Invalid parameters");
          }
     }

     @GetMapping("/{id}")
     public ResponseEntity<?> getModelFromID(@PathVariable Long id, Authentication authentication) {
         if (authentication == null || !authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
         }
     
         try {
             Model model = modelService.getModel(authentication.getName(), id); 
             return ResponseEntity.ok(model);
         } catch (ResponseStatusException e) {
             return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
         }
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deleteModelFromID(@PathVariable Long id, Authentication authentication) {
     if (authentication == null || !authentication.isAuthenticated()) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
     }

     try {
          boolean isDeleted = modelService.deleteModel(authentication.getName(), id);
          
          if (isDeleted) {
               return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Model deleted successfully");
          } else {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Model not found");
          }
     } catch (ResponseStatusException e) {
          return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
     } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
     }
     }

     
     private ResponseEntity<?> handleError(Exception e, String errorCode, String message) {
          return ResponseEntity.badRequest().body(Map.of("error", errorCode, "message", message));
     }      

}