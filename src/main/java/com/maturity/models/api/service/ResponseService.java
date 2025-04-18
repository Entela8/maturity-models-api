package com.maturity.models.api.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.maturity.models.api.dto.AnswerDTO;
import com.maturity.models.api.dto.ResoponseDTO;
import com.maturity.models.api.dto.StatisticDTO;
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

     private final UserRepository userRepository;
     private final SessionRepository sessionRepository;
     private final QuestionRepository questionRepository;
     private final AnswerRepository answerRepository;
     private final ResponseRepository responseRepository;
     private final ModelRepository modelRepository;

     public List<Response> saveResponses(String username, AddResponsesRequest responsesSent) {
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

     public List<StatisticDTO> getResponsesOfModel(String username, String modelId, String sessionId) {
          Model model = modelRepository.findById(Long.valueOf(modelId))
        .orElseThrow(() -> new IllegalArgumentException("Model not found"));

          // Récupère toutes les questions liées à ce modèle
          List<Question> questions = questionRepository.findByModel(model);
          Session session = sessionRepository.findById(Long.valueOf(sessionId))
          .orElseThrow(() -> new IllegalArgumentException("Session not found"));
          List<Response> responses = responseRepository.findBySession(session);

          Set<Long> modelQuestionIds = questions.stream().map(Question::getId).collect(Collectors.toSet());
          List<Response> filteredResponses = responses.stream()
               .filter(r -> modelQuestionIds.contains(r.getQuestion().getId()))
               .toList();

          Map<User, List<Response>> responsesByUser = filteredResponses.stream()
               .collect(Collectors.groupingBy(Response::getUser));

          List<StatisticDTO> result = new ArrayList<>();
          for (Map.Entry<User, List<Response>> entry : responsesByUser.entrySet()) {
               User userEntry = entry.getKey();
               List<AnswerDTO> answers = entry.getValue().stream()
                    .map(r -> new AnswerDTO(r.getQuestion().getContent(), r.getAnswer().getScore()))
                    .toList();

               StatisticDTO userResponse = new StatisticDTO(userEntry.getId(), userEntry.getUsername(), answers);
               result.add(userResponse);
          }

          return result;
     }
}