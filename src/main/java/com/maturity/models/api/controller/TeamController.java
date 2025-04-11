package com.maturity.models.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.server.ResponseStatusException;

import com.maturity.models.api.dto.MembersDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.exception.NotFoundException;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.service.TeamService;
import com.maturity.models.api.requests.teams.AddMemberRequest;
import com.maturity.models.api.requests.teams.CreateTeamRequest;
import com.maturity.models.api.requests.teams.InviteRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
@Slf4j
public class TeamController {
     
     private final TeamService teamService;

     @PostMapping("/new")
     public ResponseEntity<?> create(@Validated @RequestBody CreateTeamRequest createTeam, final Authentication authentication) {
          try {
               final String username = authentication.getName();

               Team team = teamService.create(username, createTeam.getName());
               return ResponseEntity.status(HttpStatus.CREATED).body(team);
          } catch (IllegalArgumentException e) {
               log.error("Team name already in use: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0001", 
                    "A team with this name already exists", 
                    "Choose a different team name"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error during team creation: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0002", 
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }

     @GetMapping("/all")
     public ResponseEntity<?> getAllTeams(@Validated final Authentication authentication) {
          try {
               final String username = authentication.getName();

               List<Team> teams = teamService.getTeams(username);
               return ResponseEntity.status(HttpStatus.CREATED).body(teams);
          } catch (Forbidden e) {
               log.error("You're not allowed to access this resource: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0003", 
                    "Not allowed to access this resource", 
                    "You must be admin or owner"
               );
               return ResponseEntity.badRequest().body(errorResponse);
          } catch (RuntimeException e) {
               log.error("Unexpected error during team creation: {}", e.getMessage(), e);
               ErrorResponse errorResponse = new ErrorResponse(
                    "team0002", 
                    "Unexpected error", 
                    "Please try again later"
               );
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
          }
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<?> deleteTeam(@Validated @PathVariable Long id, Authentication authentication){
          if (authentication == null || !authentication.isAuthenticated()) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
          }

          try {
               boolean isDeleted = teamService.delete(authentication.getName(), id);
               
               if (isDeleted) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Team deleted successfully");
               } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Team not found");
               }
          } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
          }
     }

     @PostMapping("/invite")
     public ResponseEntity<?> inviteUserToTeam(@RequestBody InviteRequest inviteRequest, @RequestHeader("username") String inviterUsername) {
          teamService.inviteMember(inviterUsername, inviteRequest.getEmail(), inviteRequest.getTeam());
          return ResponseEntity.ok("Invitation envoyée");
     }

     @PostMapping("/{teamId}/add-member")
     public ResponseEntity<?> addMemberToTeam(
          @PathVariable Long teamId,
          @RequestBody AddMemberRequest request,
          final Authentication authentication
     ) {
          try {
               final String username = authentication.getName();
               teamService.addMember(username, teamId, request.getEmail());
               return ResponseEntity.ok("Mail d'ajout à l'équipe envoyé avec succès");
          } catch (NotFoundException e) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
          } catch (ResponseStatusException e) {
               return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
          } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur inattendue");
          }
     }

     @GetMapping("/{teamId}/members")
     public ResponseEntity<?> getTeamMembers(@PathVariable Long teamId, final Authentication authentication) {
          try {
               final String username = authentication.getName();
               List<MembersDTO> members = teamService.getTeamMembers(username, teamId);
               return ResponseEntity.ok(members);
          } catch (ResponseStatusException e) {
               return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
          }
     }

     @GetMapping("/my")
     public ResponseEntity<?> getMyTeam(final Authentication authentication) {
          if (authentication == null || !authentication.isAuthenticated()) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
          }

          try {
               final String username = authentication.getName();
               Team team = teamService.getUserTeam(username);
               return ResponseEntity.ok(team);
          } catch (NotFoundException e) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vous n'appartenez à aucune équipe");
          }
     }

     @DeleteMapping("/{teamId}/remove-member/{userId}")
     public ResponseEntity<?> removeMemberFromTeam(
          @PathVariable Long teamId,
          @PathVariable Long userId,
          final Authentication authentication
     ) {
          try {
               final String username = authentication.getName();
               teamService.removeMemberFromTeam(username, teamId, userId);
               return ResponseEntity.ok("Membre retiré avec succès");
          } catch (NotFoundException e) {
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
          } catch (ResponseStatusException e) {
               return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
          }
     }

}
