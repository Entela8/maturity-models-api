package com.maturity.models.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.dto.MembersDTO;
import com.maturity.models.api.exception.NotFoundException;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamService {
     private final TeamRepository teamRepository;
     private final UserService userService;
     private final UserRepository userRepository;

     @Transactional
     public void addMember(String username, Long id, String email) {
          userService.ensureUserIsAllowed(username);

          return;
     }

     public List<MembersDTO> getTeamMembers(String username, Long teamId) {
          User requester = userRepository.findByUsername(username);
          
          if (requester == null) {
               throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
          }

          if ((requester.getRole() == Role.MEMBER)) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view team members");
          }

          Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new NotFoundException("Team not found"));

          // Optionnel : s'assurer que le user appartient à l'équipe (sauf admin/owner)
          if (requester.getRole() == Role.TEAM_LEADER || requester.getRole() == Role.MEMBER) {
               if (requester.getTeam() == null || !requester.getTeam().getId().equals(teamId)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this team");
               }
          }

          List<User> users = userRepository.findByTeam(team);

          return users.stream().map(user -> {
               MembersDTO dto = new MembersDTO();
               dto.setId(user.getId());
               dto.setUsername(user.getUsername());
               dto.setFirstName(user.getFirstName());
               dto.setLastName(user.getLastName());
               dto.setEmail(user.getEmail());
               dto.setRole(user.getRole());
               return dto;
          }).collect(Collectors.toList());
     }


     public Team getUserTeam(String username) {
          User user = userRepository.findByUsername(username);
          
          return user.getTeam();
     }

     public void removeMemberFromTeam(String username, Long teamId, Long userId) {

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
     
     public void inviteMember(String inviterUsername, String email, String teamName) {
          userService.ensureUserIsAllowed(inviterUsername);

          Team team = teamRepository.findByName(teamName);

          if (team == null) throw new NotFoundException("Team not found");

          //TODO ENVOYER EMAIL
          System.out.println("Invitation envoyée à : " + email + " pour rejoindre l'équipe " + teamName);
     }
}
