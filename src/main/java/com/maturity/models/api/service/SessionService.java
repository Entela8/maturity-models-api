package com.maturity.models.api.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.maturity.models.api.dto.SessionDTO;
import com.maturity.models.api.model.Answer;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.SessionRepository;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.requests.models.CreateAnswerRequest;
import com.maturity.models.api.requests.sessions.ActivateSessionRequest;

@Service
@RequiredArgsConstructor
public class SessionService {

     private final UserService userService;
     private final ModelRepository modelRepository;
     private final TeamRepository teamRepository;
     private final SessionRepository sessionRepository;

     public SessionDTO activateTeam(String username, ActivateSessionRequest newSession) {
          userService.ensureUserIsAllowed(username);

          Model model = modelRepository.findById(newSession.getModelId())
                    .orElseThrow(() -> new RuntimeException("Model not found"));

          Team team = teamRepository.findById(newSession.getTeamId())
                    .orElseThrow(() -> new RuntimeException("Team not found"));

          Session session = new Session();
          session.setActive(true);
          session.setDate(new Date());
          session.setModel(model);
          session.setTeam(team);
          sessionRepository.save(session);

          SessionDTO sessionDTO = sessionToDTO(session);

          return sessionDTO;
     }

     public List<SessionDTO> getTeamSession(String username, String teamId) {
          Team team = teamRepository.findById(Long.valueOf(teamId))
                    .orElseThrow(() -> new RuntimeException("Team not found"));

          List<Session> sessionList = sessionRepository.findAllByTeam(team);

          List<SessionDTO> sessions = sessionList.stream()
          .map(this::sessionToDTO)
          .collect(Collectors.toList());
  
          return sessions;
     }

     public List<SessionDTO> getActiveSessions(String username, String modelId) {
          Long modelIdLong = Long.valueOf(modelId);
      
          modelRepository.findById(modelIdLong)
              .orElseThrow(() -> new RuntimeException("Model not found"));
          List<Session> activeSessions = sessionRepository.findByModelIdAndActiveTrue(modelIdLong);
      
          return activeSessions.stream()
                  .map(this::sessionToDTO)
                  .collect(Collectors.toList());
     }

     public SessionDTO deactivateSession(String username, ActivateSessionRequest receivedSession) {
          userService.ensureUserIsAllowed(username);

          Optional<Session> optionalSession = sessionRepository.findByModelIdAndTeamId(receivedSession.getModelId(), receivedSession.getTeamId());
          Session session = optionalSession.get();

          session.setActive(false);
          sessionRepository.save(session);

          SessionDTO sessionDTO = sessionToDTO(session);

          return sessionDTO;
     }

     public List<SessionDTO> getAllSessions(String username) {
          userService.ensureUserIsAllowed(username);
      
          return sessionRepository.findAll().stream()
                  .map(this::sessionToDTO)
                  .collect(Collectors.toList());
     }
      
     public SessionDTO sessionToDTO(Session session){
          SessionDTO sessionDTO = new SessionDTO();

          sessionDTO.setActive(session.isActive());
          sessionDTO.setDate(session.getDate());
          sessionDTO.setId(session.getId());
          sessionDTO.setModelId(session.getModel().getId());
          sessionDTO.setTeamId(session.getTeam().getId());
          sessionDTO.setTeamName(session.getTeam().getName());
          return sessionDTO;
     }
}


