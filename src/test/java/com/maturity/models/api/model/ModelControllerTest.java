package com.maturity.models.api.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.controller.ModelController;
import com.maturity.models.api.dto.ModelDTO;
import com.maturity.models.api.requests.models.CreateAnswerRequest;
import com.maturity.models.api.requests.models.CreateModelRequest;
import com.maturity.models.api.requests.models.CreateQuestionRequest;
import com.maturity.models.api.service.ModelService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ModelControllerTest {

     @Mock
     private ModelService modelService;

     @InjectMocks
     private ModelController modelController;

     @Mock
     private Authentication authentication;

     @Test
     void createModel_Successful() {
          when(authentication.getName()).thenReturn("carlo");

          CreateModelRequest request = new CreateModelRequest();
          request.setTitle("Oppu Maturity Model");

          CreateQuestionRequest question = new CreateQuestionRequest();
          question.setContent("How well does your team follow fOffRT ceremonies?");
          question.setAnswers(List.of(
                    new CreateAnswerRequest("Not at all", 1),
                    new CreateAnswerRequest("Rarely", 2),
                    new CreateAnswerRequest("Sometimes", 3),
                    new CreateAnswerRequest("Most of the time", 4),
                    new CreateAnswerRequest("Always", 5)
          ));
          request.setQuestions(List.of(question));

          Model createdModel = new Model();
          createdModel.setId(11L);
          createdModel.setTitle("Oppu Maturity Model");

          when(modelService.createModel(eq("carlo"), any(CreateModelRequest.class)))
                    .thenReturn(createdModel);

          ResponseEntity<?> response = modelController.create(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
          assertThat(response.getBody()).isInstanceOf(Model.class);

          Model modelResponse = (Model) response.getBody();
          assertThat(modelResponse.getId()).isEqualTo(11L);
          assertThat(modelResponse.getTitle()).isEqualTo("Oppu Maturity Model");
     }

     @Test
     void getAllModels_Successful() {
          when(authentication.getName()).thenReturn("carlo");
     
          ModelDTO model1 = new ModelDTO();
          model1.setId(5L);
          model1.setTitle("Scrum Maturity Model");
     
          ModelDTO model2 = new ModelDTO();
          model2.setId(6L);
          model2.setTitle("Booss Maturity Model");
     
          ModelDTO model3 = new ModelDTO();
          model3.setId(10L);
          model3.setTitle("eewde");
     
          ModelDTO model4 = new ModelDTO();
          model4.setId(11L);
          model4.setTitle("Oppu Maturity Model");
     
          when(modelService.getAllModels("carlo")).thenReturn(List.of(model1, model2, model3, model4));
     
          ResponseEntity<?> response = modelController.getAllModels(authentication);
     
          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isInstanceOf(List.class);
     
          List<?> models = (List<?>) response.getBody();
          assertThat(models).hasSize(4);
     
          models.forEach(model -> {
               assertThat(((ModelDTO) model).getId()).isNotNull();
               assertThat(((ModelDTO) model).getTitle()).isNotNull();
          });
     }

     @Test
     void getModelFromID_Successful() {
          Long modelId = 5L;
          
          when(authentication.getName()).thenReturn("carlo");
          when(authentication.isAuthenticated()).thenReturn(true);

          Model model = new Model();
          model.setId(modelId);
          model.setTitle("Scrum Maturity Model");

          when(modelService.getModel("carlo", modelId)).thenReturn(model);

          ResponseEntity<?> response = modelController.getModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isInstanceOf(Model.class);

          Model modelResponse = (Model) response.getBody();
          assertThat(modelResponse.getId()).isEqualTo(modelId);
          assertThat(modelResponse.getTitle()).isEqualTo("Scrum Maturity Model");
     }

     @Test
     void getModelFromID_Unauthenticated() {
          Long modelId = 5L;
          
          when(authentication.isAuthenticated()).thenReturn(false);

          ResponseEntity<?> response = modelController.getModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
          assertThat(response.getBody()).isEqualTo("User is not authenticated");
     }

     @Test
     void getModelFromID_ModelNotFound() {
          Long modelId = 5L;

          when(authentication.getName()).thenReturn("carlo");
          when(authentication.isAuthenticated()).thenReturn(true);

          when(modelService.getModel("carlo", modelId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Model not found"));

          ResponseEntity<?> response = modelController.getModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
          assertThat(response.getBody()).isEqualTo("Model not found");
     }

     @Test
     void deleteModelFromID_Successful() {
          Long modelId = 5L;

          when(authentication.getName()).thenReturn("carlo");
          when(authentication.isAuthenticated()).thenReturn(true);

          when(modelService.deleteModel("carlo", modelId)).thenReturn(true);

          ResponseEntity<?> response = modelController.deleteModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
          assertThat(response.getBody()).isEqualTo("Model deleted successfully");

          verify(modelService, times(1)).deleteModel("carlo", modelId);
     }

     @Test
     void deleteModelFromID_ModelNotFound() {
          Long modelId = 5L;

          when(authentication.getName()).thenReturn("carlo");
          when(authentication.isAuthenticated()).thenReturn(true);

          when(modelService.deleteModel("carlo", modelId)).thenReturn(false);

          ResponseEntity<?> response = modelController.deleteModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
          assertThat(response.getBody()).isEqualTo("Model not found");

          verify(modelService, times(1)).deleteModel("carlo", modelId);
     }

     @Test
     void deleteModelFromID_Unauthenticated() {
          Long modelId = 5L;

          when(authentication.isAuthenticated()).thenReturn(false);

          ResponseEntity<?> response = modelController.deleteModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
          assertThat(response.getBody()).isEqualTo("User is not authenticated");
     }

     @Test
     void deleteModelFromID_InternalServerError() {
          Long modelId = 5L;

          when(authentication.getName()).thenReturn("carlo");
          when(authentication.isAuthenticated()).thenReturn(true);

          when(modelService.deleteModel("carlo", modelId)).thenThrow(new RuntimeException("Some error"));

          ResponseEntity<?> response = modelController.deleteModelFromID(modelId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
          assertThat(response.getBody()).isEqualTo("An error occurred");

          verify(modelService, times(1)).deleteModel("carlo", modelId);
     }
}
