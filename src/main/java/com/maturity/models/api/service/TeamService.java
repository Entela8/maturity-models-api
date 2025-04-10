package com.maturity.models.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maturity.models.api.exception.NotFoundException;
import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.requests.models.CreateModelRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamService {
     private final TeamRepository teamRepository;
     private final UserService userService;

     @Transactional
     public void addMember(String username, CreateModelRequest createModelRequest) {
          userService.ensureUserIsAllowed(username);

          return;
     }

     @Transactional
     public Team create(String username, String name) {
          userService.ensureUserIsAllowed(username);

          if (teamRepository.findByName(name) != null) {
               // If a team with the same name exists, throw an exception or return false
               throw new IllegalArgumentException("Team with name '" + name + "' already exists");
          }

          Team team = new Team();
          team.setName(name);

          return teamRepository.save(team);
     }

     public List<Team> getTeams(String username) {
          userService.ensureUserIsAllowed(username);

          return teamRepository.findAll();
     }

     @Transactional
     public boolean delete(String username, Long id) {
          userService.ensureUserIsAllowed(username);
          
          Team team = teamRepository.findById(id)
               .orElseThrow(() -> new NotFoundException("Team with ID " + id + " not found"));
          
          teamRepository.delete(team);
          return true;
     }
     
}
