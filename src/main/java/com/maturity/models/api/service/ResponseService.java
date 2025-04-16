package com.maturity.models.api.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maturity.models.api.dto.ResoponseDTO;
import com.maturity.models.api.model.Answer;
import com.maturity.models.api.model.Question;
import com.maturity.models.api.model.Response;
import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.User;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.repository.AnswerRepository;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.QuestionRepository;
import com.maturity.models.api.repository.ResponseRepository;
import com.maturity.models.api.repository.SessionRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.requests.responses.AddResponsesRequest;


@Service
@RequiredArgsConstructor
public class ResponseService {

     private final UserService userService;
     private final UserRepository userRepository;
     private final SessionRepository sessionRepository;
     private final QuestionRepository questionRepository;
     private final AnswerRepository answerRepository;
     private final ResponseRepository responseRepository;
     private final ModelRepository modelRepository;

     public List<Response> saveResponses(String username, AddResponsesRequest responsesSent) {
          userService.ensureUserIsAllowed(username);

          User user = userRepository.findByUsername(username);
          if (user == null) {
               throw new IllegalArgumentException("User not found");
          }

          Session session = sessionRepository.findById(Long.valueOf(responsesSent.getSessionId()))
               .orElseThrow(() -> new IllegalArgumentException("Session not found"));

          List<Response> savedResponses = new ArrayList<>();

          for (ResoponseDTO dto : responsesSent.getResponses()) {
               Question question = questionRepository.findById(Long.parseLong(dto.getQuestionId()))
                    .orElseThrow(() -> new IllegalArgumentException("Question not found: " + dto.getQuestionId()));

               Answer answer = answerRepository.findById(Long.parseLong(dto.getAnswerId()))
                    .orElseThrow(() -> new IllegalArgumentException("Answer not found: " + dto.getAnswerId()));

               Response response = new Response();
               response.setUser(user);
               response.setSession(session);
               response.setQuestion(question);
               response.setAnswer(answer);

               savedResponses.add(responseRepository.save(response));
          }

          return savedResponses;
     }

     public List<Response> getResponsesOfModel(String username, String modelId, String sessionId) {
          User user = userRepository.findByUsername(username);
          if (user == null) {
              throw new IllegalArgumentException("User not found");
          }
      
          Session session = sessionRepository.findById(Long.valueOf(sessionId))
              .orElseThrow(() -> new IllegalArgumentException("Session not found"));
      
          Model model = modelRepository.findById(Long.valueOf(modelId))
              .orElseThrow(() -> new IllegalArgumentException("Model not found"));
      
          // Get all questions of this model
          List<Question> questions = questionRepository.findByModel(model);
      
          // Extract the IDs of the questions to filter responses
          List<Long> questionIds = questions.stream()
              .map(Question::getId)
              .toList();
      
          // Fetch all responses that match user, session, and are related to this model's questions
          List<Response> responses = responseRepository.findByUserAndSessionAndQuestionIdIn(user, session, questionIds);
      
          return responses;
     }
      

}