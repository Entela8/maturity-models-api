package com.maturity.models.api.service;

import com.maturity.models.api.dto.ModelDTO;
import com.maturity.models.api.model.*;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.requests.models.CreateAnswerRequest;
import com.maturity.models.api.requests.models.CreateModelRequest;
import com.maturity.models.api.requests.models.CreateQuestionRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ModelService modelService;

    private User ownerUser;

    @BeforeEach
    void setUp() {
        ownerUser = new User();
        ownerUser.setUsername("owner");
        ownerUser.setRole(Role.OWNER);
    }

    @Test
    void createModel_shouldCreateModelCorrectly() {
        // Given
        String username = "owner";
        CreateModelRequest request = new CreateModelRequest();
        request.setTitle("Security Model");

        CreateAnswerRequest answer = new CreateAnswerRequest();
        answer.setContent("Yes");
        answer.setScore(10);

        CreateQuestionRequest question = new CreateQuestionRequest();
        question.setContent("Is access restricted?");
        question.setAnswers(List.of(answer));

        request.setQuestions(List.of(question));

        Model savedModel = new Model();
        savedModel.setId(1L);
        savedModel.setTitle("Security Model");

        Question savedQuestion = new Question();
        savedQuestion.setContent("Is access restricted?");
        savedQuestion.setModel(savedModel);

        Answer savedAnswer = new Answer();
        savedAnswer.setContent("Yes");
        savedAnswer.setScore(10);
        savedAnswer.setQuestion(savedQuestion);

        savedQuestion.setAnswers(List.of(savedAnswer));
        savedModel.setQuestions(List.of(savedQuestion)); // Populate questions list

        when(modelRepository.save(any(Model.class))).thenReturn(savedModel);

        // When
        Model result = modelService.createModel(username, request);

        // Then
        verify(userService).ensureUserIsAllowed(username);
        assertEquals("Security Model", result.getTitle());
        assertEquals(1, result.getQuestions().size());
        assertEquals("Is access restricted?", result.getQuestions().get(0).getContent());
        assertEquals(1, result.getQuestions().get(0).getAnswers().size());
        assertEquals("Yes", result.getQuestions().get(0).getAnswers().get(0).getContent());
    }

    @Test
    void getAllModels_shouldReturnAllModelsForAdmin() {
        // Given
        User admin = new User();
        admin.setRole(Role.OWNER);
        admin.setUsername("admin");

        Model m = new Model();
        m.setId(1L);
        m.setTitle("Model A");

        when(userRepository.findByUsername("admin")).thenReturn(admin);
        when(modelRepository.findAll()).thenReturn(List.of(m));

        // When
        List<ModelDTO> models = modelService.getAllModels("admin");

        // Then
        assertEquals(1, models.size());
        assertEquals("Model A", models.get(0).getTitle());
    }

    @Test
    void getAllModels_shouldReturnTeamModelsForUser() {
        // Given
        Team team = new Team();
        team.setId(42L);

        User user = new User();
        user.setUsername("user");
        user.setRole(Role.MEMBER);
        user.setTeam(team);

        Model m = new Model();
        m.setId(2L);
        m.setTitle("Model B");

        when(userRepository.findByUsername("user")).thenReturn(user);
        when(modelRepository.findModelsByTeamId(42L)).thenReturn(List.of(m));

        // When
        List<ModelDTO> models = modelService.getAllModels("user");

        // Then
        assertEquals(1, models.size());
        assertEquals("Model B", models.get(0).getTitle());
    }

    @Test
    void deleteModel_shouldReturnTrueWhenModelExists() {
        // Given
        String username = "admin";
        Model model = new Model();
        model.setId(10L);

        when(modelRepository.findById(10L)).thenReturn(Optional.of(model));

        // When
        boolean deleted = modelService.deleteModel(username, 10L);

        // Then
        verify(userService).ensureUserIsAllowed(username);
        verify(modelRepository).delete(model);
        assertTrue(deleted);
    }

    @Test
    void deleteModel_shouldReturnFalseWhenModelDoesNotExist() {
        when(modelRepository.findById(999L)).thenReturn(Optional.empty());

        boolean deleted = modelService.deleteModel("admin", 999L);

        assertFalse(deleted);
    }

    @Test
    void getModel_shouldReturnModelWhenExists() {
        Model model = new Model();
        model.setId(1L);
        model.setTitle("Test Model");

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));

        Model result = modelService.getModel("admin", 1L);

        assertEquals("Test Model", result.getTitle());
    }

    @Test
    void getModel_shouldThrowExceptionWhenNotFound() {
        when(modelRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> modelService.getModel("admin", 404L));
    }
}
