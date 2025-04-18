package com.maturity.models.api.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maturity.models.api.dto.SessionDTO;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.SessionRepository;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.requests.sessions.ActivateSessionRequest;
import com.maturity.models.api.service.SessionService;
import com.maturity.models.api.service.UserService;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {
     @Mock
     private UserService userService;

     @Mock
     private ModelRepository modelRepository;

     @Mock
     private TeamRepository teamRepository;

     @Mock
     private SessionRepository sessionRepository;

     @InjectMocks
     private SessionService sessionService;

     @Test
     void activateTeam_shouldCreateAndReturnSessionDTO() {
          // Given
          String username = "adminUser";
          ActivateSessionRequest request = new ActivateSessionRequest();
          request.setModelId(1L);
          request.setTeamId(2L);

          Model model = new Model();
          model.setId(1L);

          Team team = new Team();
          team.setId(2L);
          team.setName("Team A");

          Session session = new Session();
          session.setId(99L);
          session.setModel(model);
          session.setTeam(team);
          session.setDate(new Date());
          session.setActive(true);

          when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
          when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
          when(sessionRepository.save(any(Session.class))).thenAnswer(invocation -> {
               Session saved = invocation.getArgument(0);
               saved.setId(99L);
               return saved;
          });           

          SessionDTO result = sessionService.activateTeam(username, request);

          assertNotNull(result);
          
          assertEquals(99L, result.getId());
          assertEquals(1L, result.getModelId());
          assertEquals(2L, result.getTeamId());
          assertEquals("Team A", result.getTeamName());
          assertTrue(result.isActive());

          verify(userService).ensureUserIsAllowed(username);
     }

     @Test
     void getTeamSession_shouldReturnSessionDTOList() {
          Team team = new Team();
          team.setId(10L);
          team.setName("Alpha");

          Session session = new Session();
          session.setId(5L);
          session.setTeam(team);
          session.setModel(new Model());
          session.setDate(new Date());
          session.setActive(true);

          when(teamRepository.findById(10L)).thenReturn(Optional.of(team));
          when(sessionRepository.findAllByTeam(team)).thenReturn(List.of(session));

          List<SessionDTO> result = sessionService.getTeamSession("user", "10");

          assertEquals(1, result.size());
          assertEquals(5L, result.get(0).getId());
     }

     @Test
     void deactivateSession_shouldSetSessionInactiveAndReturnDTO() {
          String username = "adminUser";
          ActivateSessionRequest request = new ActivateSessionRequest();
          request.setModelId(1L);
          request.setTeamId(2L);

          Model model = new Model();
          model.setId(1L);

          Team team = new Team();
          team.setId(2L);
          team.setName("Beta");

          Session session = new Session();
          session.setId(3L);
          session.setActive(true);
          session.setModel(model);
          session.setTeam(team);

          when(sessionRepository.findByModelIdAndTeamId(1L, 2L)).thenReturn(Optional.of(session));
          when(sessionRepository.save(any())).thenReturn(session);

          SessionDTO result = sessionService.deactivateSession(username, request);

          assertFalse(result.isActive());
          assertEquals(3L, result.getId());

          verify(userService).ensureUserIsAllowed(username);
     }
}
