package com.maturity.models.api.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.maturity.models.api.controller.ResponsesController;
import com.maturity.models.api.dto.AnswerDTO;
import com.maturity.models.api.dto.ResoponseDTO;
import com.maturity.models.api.dto.StatisticDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.requests.responses.AddResponsesRequest;
import com.maturity.models.api.service.ResponseService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResponsesControllerTest {

     @Mock
     private ResponseService responseService;

     @InjectMocks
     private ResponsesController responsesController;

     @Mock
     private Authentication authentication;
     
     @Test
     void saveResponses_Successful() {
         AddResponsesRequest responsesRequest = new AddResponsesRequest();
         responsesRequest.setSessionId("session123");
     
         ResoponseDTO responseDTO = new ResoponseDTO();
         responseDTO.setAnswerId("1");
         responseDTO.setQuestionId("q1");
     
         responsesRequest.setResponses(List.of(responseDTO));
     
         when(authentication.getName()).thenReturn("carlo");
     
         when(responseService.saveResponses("carlo", responsesRequest)).thenReturn(Collections.emptyList());
     
         ResponseEntity<?> response = responsesController.saveResponses(responsesRequest, authentication);
     
         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
         assertThat(response.getBody()).isEqualTo(responsesRequest);
     
         verify(responseService, times(1)).saveResponses("carlo", responsesRequest);
     }
     
     @Test
     void saveResponses_InvalidInput() {
         AddResponsesRequest responses = new AddResponsesRequest();
         responses.setSessionId("123");
     
         when(authentication.getName()).thenReturn("carlo");
         when(responseService.saveResponses(eq("carlo"), any(AddResponsesRequest.class)))
             .thenThrow(new IllegalArgumentException("Missing data"));
     
         ResponseEntity<?> response = responsesController.saveResponses(responses, authentication);
     
         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
     
         ErrorResponse errorResponse = (ErrorResponse) response.getBody();
         assertThat(errorResponse.getError()).isEqualTo("response0001");
         assertThat(errorResponse.getMessage()).isEqualTo("Error in saving the responses");
     }
     
     @Test
     void saveResponses_UnexpectedError() {
         AddResponsesRequest responsesRequest = new AddResponsesRequest();
         responsesRequest.setSessionId("session123");
     
         ResoponseDTO responseDTO = new ResoponseDTO();
         responseDTO.setAnswerId("1");
         responseDTO.setQuestionId("q1");
     
         responsesRequest.setResponses(List.of(responseDTO));
     
         when(authentication.getName()).thenReturn("carlo");
     
         doThrow(new RuntimeException("Unexpected error")).when(responseService).saveResponses(eq("carlo"), eq(responsesRequest));
     
         ResponseEntity<?> response = responsesController.saveResponses(responsesRequest, authentication);
         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
     
         ErrorResponse errorResponse = (ErrorResponse) response.getBody();
         assertThat(errorResponse.getError()).isEqualTo("response0002");
         assertThat(errorResponse.getMessage()).isEqualTo("Unexpected error");
     }

     @Test
     void getResponsesOfModel_Successful() {
          String username = "carlo";
          String modelId = "1";
          String sessionId = "100";

          AnswerDTO answer1 = new AnswerDTO("Question 1", 4);
          AnswerDTO answer2 = new AnswerDTO("Question 2", 5);

          StatisticDTO statDTO = new StatisticDTO(1L, "carlo", List.of(answer1, answer2));
          List<StatisticDTO> mockResponseList = List.of(statDTO);

          when(authentication.getName()).thenReturn(username);
          when(responseService.getResponsesOfModel(username, modelId, sessionId)).thenReturn(mockResponseList);

          ResponseEntity<?> response = responsesController.getResponsesOfModel(authentication, modelId, sessionId);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo(mockResponseList);

          verify(responseService, times(1)).getResponsesOfModel(username, modelId, sessionId);
     }


     @Test
     void getResponsesOfModel_InvalidInput() {
          String username = "carlo";
          String modelId = "invalid";
          String sessionId = "100";

          when(authentication.getName()).thenReturn(username);
          when(responseService.getResponsesOfModel(username, modelId, sessionId))
                    .thenThrow(new IllegalArgumentException("Invalid model ID"));

          ResponseEntity<?> response = responsesController.getResponsesOfModel(authentication, modelId, sessionId);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("response0003");
     }

     @Test
     void getResponsesOfModel_UnexpectedError() {
          String username = "carlo";
          String modelId = "1";
          String sessionId = "100";

          when(authentication.getName()).thenReturn(username);
          when(responseService.getResponsesOfModel(username, modelId, sessionId))
                    .thenThrow(new RuntimeException("Unexpected error"));

          ResponseEntity<?> response = responsesController.getResponsesOfModel(authentication, modelId, sessionId);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("response0004");
     }
     
}
