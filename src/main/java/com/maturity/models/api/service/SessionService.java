package com.maturity.models.api.service;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.maturity.models.api.dto.SessionDTO;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.repository.ModelRepository;
import com.maturity.models.api.repository.SessionRepository;
import com.maturity.models.api.repository.TeamRepository;
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
          userService.ensureUserIsAllowed(username);

          Team team = teamRepository.findById(Long.valueOf(teamId))
                    .orElseThrow(() -> new RuntimeException("Team not found"));

          List<Session> sessionList = sessionRepository.findAllByTeam(team);

          List<SessionDTO> sessions = sessionList.stream()
          .map(this::sessionToDTO)
          .collect(Collectors.toList());
  
          return sessions;
     }


     public SessionDTO sessionToDTO(Session session){
          SessionDTO sessionDTO = new SessionDTO();

          sessionDTO.setActive(session.isActive());
          sessionDTO.setDate(session.getDate());
          sessionDTO.setId(session.getId());
          sessionDTO.setModelId(session.getModel().getId());
          sessionDTO.setTeamId(session.getTeam().getId());

          return sessionDTO;
     }
}


