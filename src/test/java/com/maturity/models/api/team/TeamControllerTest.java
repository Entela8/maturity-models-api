package com.maturity.models.api.team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;


import com.maturity.models.api.controller.TeamController;
import com.maturity.models.api.dto.MembersDTO;
import com.maturity.models.api.exception.ErrorResponse;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.requests.teams.AddMemberRequest;
import com.maturity.models.api.requests.teams.CreateTeamRequest;
import com.maturity.models.api.requests.teams.InviteRequest;
import com.maturity.models.api.service.TeamService;


import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {
     @InjectMocks
     private TeamController teamController;

     @Mock
     private TeamService teamService;

     @Mock
     private Authentication authentication;

     @Test
     void createTeam_Successful() {
          CreateTeamRequest request = new CreateTeamRequest();
          request.setName("Alpha");

          Team mockTeam = new Team();
          mockTeam.setId(1L);
          mockTeam.setName("Alpha");

          when(authentication.getName()).thenReturn("carlo");
          when(teamService.create("carlo", "Alpha")).thenReturn(mockTeam);

          ResponseEntity<?> response = teamController.create(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
          assertThat(response.getBody()).isEqualTo(mockTeam);
     }

     @Test
     void createTeam_TeamAlreadyExists() {
          CreateTeamRequest request = new CreateTeamRequest();
          request.setName("Beta");

          when(authentication.getName()).thenReturn("carlo");
          when(teamService.create("carlo", "Beta"))
               .thenThrow(new IllegalArgumentException("Name exists"));

          ResponseEntity<?> response = teamController.create(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("team0001");
          assertThat(error.getMessage()).isEqualTo("A team with this name already exists");
     }

     @Test
     void createTeam_UnexpectedError() {
          CreateTeamRequest request = new CreateTeamRequest();
          request.setName("Gamma");

          when(authentication.getName()).thenReturn("carlo");
          when(teamService.create("carlo", "Gamma"))
               .thenThrow(new RuntimeException("Boom"));

          ResponseEntity<?> response = teamController.create(request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("team0002");
          assertThat(error.getMessage()).isEqualTo("Unexpected error");
     }

     @Test
     void getAllTeams_Successful() {
          List<Team> mockTeams = List.of(
               new Team() {{ setId(1L); setName("Team A"); }},
               new Team() {{ setId(2L); setName("Team B"); }}
          );

          when(authentication.getName()).thenReturn("carlo");
          when(teamService.getTeams("carlo")).thenReturn(mockTeams);

          ResponseEntity<?> response = teamController.getAllTeams(authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
          assertThat(response.getBody()).isEqualTo(mockTeams);
     }

     @Test
     void getAllTeams_UnexpectedError() {
          when(authentication.getName()).thenReturn("carlo");
          when(teamService.getTeams("carlo"))
               .thenThrow(new RuntimeException("Boom again"));

          ResponseEntity<?> response = teamController.getAllTeams(authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

          ErrorResponse error = (ErrorResponse) response.getBody();
          assertThat(error.getError()).isEqualTo("team0002");
          assertThat(error.getMessage()).isEqualTo("Unexpected error");
     }

     @Test
     void deleteTeam_Unauthenticated() {
          when(authentication.isAuthenticated()).thenReturn(false);

          ResponseEntity<?> response = teamController.deleteTeam(1L, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
          assertThat(response.getBody()).isEqualTo("User is not authenticated");
     }

     @Test
     void inviteUserToTeam_Successful() {
          InviteRequest inviteRequest = new InviteRequest();
          inviteRequest.setEmail("user@example.com");
          inviteRequest.setTeam("Team A");

          ResponseEntity<?> response = teamController.inviteUserToTeam(inviteRequest, "carlo");

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo("Invitation envoyée");
     }

     @Test
     void addMemberToTeam_Successful() {
          Long teamId = 1L;
          AddMemberRequest request = new AddMemberRequest();
          request.setEmail("newuser@example.com");
          request.setRole(Role.MEMBER);

          when(authentication.getName()).thenReturn("carlo");

          ResponseEntity<?> response = teamController.addMemberToTeam(teamId, request, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo("Mail d'ajout à l'équipe envoyé avec succès");
     }

     @Test
     void addMemberToTeam_UnexpectedError() {
         Long teamId = 1L;
         AddMemberRequest request = new AddMemberRequest();
         request.setEmail("newuser@example.com");
         request.setRole(Role.MEMBER);
     
         when(authentication.getName()).thenReturn("carlo");
     
         doThrow(new RuntimeException("Unexpected error"))
             .when(teamService)
             .addMember("carlo", teamId, "newuser@example.com", Role.MEMBER);
     
         ResponseEntity<?> response = teamController.addMemberToTeam(teamId, request, authentication);
     
         assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
         assertThat(response.getBody()).isEqualTo("Erreur inattendue");
     }
     

     @Test
     void getTeamMembers_Successful() {
          Long teamId = 1L;
          List<MembersDTO> mockMembers = List.of(
               new MembersDTO() {{ setId(1L); setUsername("user1"); }},
               new MembersDTO() {{ setId(2L); setUsername("user2"); }}
          );

          when(authentication.getName()).thenReturn("carlo");
          when(teamService.getTeamMembers("carlo", teamId)).thenReturn(mockMembers);

          ResponseEntity<?> response = teamController.getTeamMembers(teamId, authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
          assertThat(response.getBody()).isEqualTo(mockMembers);
     }

     @Test
     void getMyTeam_Unauthenticated() {
          when(authentication.isAuthenticated()).thenReturn(false);

          ResponseEntity<?> response = teamController.getMyTeam(authentication);

          assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
          assertThat(response.getBody()).isEqualTo("User is not authenticated");
     }
}
