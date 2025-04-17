package com.maturity.models.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.model.Answer;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.dto.ModelDTO;
import com.maturity.models.api.model.Question;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.requests.models.CreateAnswerRequest;
import com.maturity.models.api.requests.models.CreateModelRequest;
import com.maturity.models.api.requests.models.CreateQuestionRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelService {
     private final ModelRepository modelRepository;
     private final UserService userService;
     private final UserRepository userRepository;
     private final TeamRepository teamRepository;

     @Transactional
     public Model createModel(String username, CreateModelRequest createModelRequest) {
          userService.ensureUserIsAllowed(username);

          Model model = new Model();
          model.setTitle(createModelRequest.getTitle());

          List<Question> questions = new ArrayList<>();
          for (CreateQuestionRequest createQuestionRequest : createModelRequest.getQuestions()) {
               Question question = new Question();
               question.setContent(createQuestionRequest.getContent());
               question.setModel(model);

               List<Answer> answers = new ArrayList<>();
               for (CreateAnswerRequest createAnswerRequest : createQuestionRequest.getAnswers()) {
                    Answer answer = new Answer();
                    answer.setContent(createAnswerRequest.getContent());
                    answer.setScore(createAnswerRequest.getScore());
                    answer.setQuestion(question);
                    answers.add(answer);
               }

               question.setAnswers(answers);
               questions.add(question);
          }

          model.setQuestions(questions);

          return modelRepository.save(model);
     }

     public List<ModelDTO> getAllModels(String username) {
          User user = userRepository.findByUsername(username);
          List<Model> models;

          if (user.getRole() == Role.OWNER || user.getRole() == Role.ADMIN) {
               models = modelRepository.findAll();
          } else {
               Long teamId = user.getTeam().getId();
               models = modelRepository.findModelsByTeamId(teamId);
          }

          return models.stream().map(model -> {
               ModelDTO dto = new ModelDTO();
               dto.setId(model.getId());
               dto.setTitle(model.getTitle());
               return dto;
          }).collect(Collectors.toList());
     }

     public boolean deleteModel(String username, Long id) {
          userService.ensureUserIsAllowed(username);
      
          Model model = modelRepository.findById(id).orElse(null);
      
          if (model == null) {
              return false;
          }
      
          modelRepository.delete(model);
          return true;
     }
      
     public Model getModel(String username, Long modelId) {
     return modelRepository.findById(modelId)
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Model not found"));
     }

}
