package com.maturity.models.api.response;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maturity.models.api.dto.ResoponseDTO;
import com.maturity.models.api.dto.StatisticDTO;
import com.maturity.models.api.model.Answer;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Question;
import com.maturity.models.api.model.Response;
import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.AnswerRepository;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.QuestionRepository;
import com.maturity.models.api.repository.ResponseRepository;
import com.maturity.models.api.repository.SessionRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.requests.responses.AddResponsesRequest;
import com.maturity.models.api.service.ResponseService;

@ExtendWith(MockitoExtension.class)
class ResponseServiceTest {
     
     @InjectMocks
     private ResponseService responseService;

     @Mock
     private UserRepository userRepository;

     @Mock
     private SessionRepository sessionRepository;

     @Mock
     private QuestionRepository questionRepository;

     @Mock
     private AnswerRepository answerRepository;

     @Mock
     private ResponseRepository responseRepository;

     @Mock
     private ModelRepository modelRepository;

     @BeforeEach
     void setUp() {
          MockitoAnnotations.openMocks(this);
     }

     
}
