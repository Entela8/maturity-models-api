package com.maturity.models.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.model.Answer;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Question;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.ModelRepository;
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
     private final UserRepository userRepository;

     @Transactional
     public Model createModel(String username, CreateModelRequest createModelRequest) {
          User user = userRepository.findByUsername(username);
          
          if (user.getRole() != Role.ADMIN && user.getRole() != Role.OWNER) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to create maturity models");
          }
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

}
