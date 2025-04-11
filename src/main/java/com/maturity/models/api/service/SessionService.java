package com.maturity.models.api.service;

import lombok.RequiredArgsConstructor;

import java.util.Date;

import org.springframework.stereotype.Service;

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

     public Session activateTeam(String username, ActivateSessionRequest newSession) {
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
    
          return sessionRepository.save(session);
      }
}
