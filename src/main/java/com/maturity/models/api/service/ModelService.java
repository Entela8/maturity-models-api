package com.maturity.models.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maturity.models.api.model.Answer;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.dto.ModelDTO;
import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.model.Question;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.ModelRepository;
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
          userService.ensureUserIsAllowed(username);

          List<Model> models = modelRepository.findAll();

          List<ModelDTO> modelDTOs = new ArrayList<>();

          for (Model m : models) {
               ModelDTO modelDTO = new ModelDTO();
               modelDTO.setId(m.getId());
               modelDTO.setTitle(m.getTitle());

               modelDTOs.add(modelDTO);
          }

          return modelDTOs;

     }
}
