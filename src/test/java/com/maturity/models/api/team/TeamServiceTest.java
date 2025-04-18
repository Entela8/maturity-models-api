package com.maturity.models.api.team;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.maturity.models.api.dto.MembersDTO;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.service.MailService;
import com.maturity.models.api.service.TeamService;
import com.maturity.models.api.service.UserService;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

     @Mock
     private TeamRepository teamRepository;

     @Mock
     private UserService userService;

     @Mock
     private UserRepository userRepository;

     @Mock
     private MailService mailService;

     @InjectMocks
     private TeamService teamService;

     @BeforeEach
     void setup() {
          ReflectionTestUtils.setField(teamService, "webUrl", "http://localhost:3000");
     }

     @Test
     void testAddMember_ShouldSendEmail() {
          String username = "admin";
          Long teamId = 1L;
          String email = "test@example.com";
          Role role = Role.MEMBER;

          doNothing().when(userService).ensureUserIsAllowed(username);
          doNothing().when(mailService).sendEmail(anyString(), anyString(), anyString());

          teamService.addMember(username, teamId, email, role);

          verify(mailService, times(1)).sendEmail(eq(email), contains("Invitation"), contains("Rejoindre l'équipe"));
     }

     @Test
     void testGetTeamMembers_ShouldReturnMemberList() {
          String username = "admin";
          Long teamId = 1L;

          User admin = new User();
          admin.setUsername(username);
          admin.setRole(Role.ADMIN);

          Team team = new Team();
          team.setId(teamId);

          User member = new User();
          member.setId(2L);
          member.setUsername("member");
          member.setRole(Role.MEMBER);
          member.setTeam(team);

          when(userRepository.findByUsername(username)).thenReturn(admin);
          when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
          when(userRepository.findByTeam(team)).thenReturn(List.of(member));

          List<MembersDTO> result = teamService.getTeamMembers(username, teamId);

          assertThat(result).hasSize(1);
          assertThat(result.get(0).getUsername()).isEqualTo("member");
     }

     @Test
     void testCreate_ShouldCreateNewTeam() {
          String username = "owner";
          String teamName = "New Team";

          when(teamRepository.findByName(teamName)).thenReturn(null);
          when(teamRepository.save(any(Team.class))).thenAnswer(i -> i.getArgument(0));

          doNothing().when(userService).ensureUserIsAllowed(username);

          Team created = teamService.create(username, teamName);

          assertThat(created.getName()).isEqualTo(teamName);
          verify(teamRepository).save(any(Team.class));
     }

     @Test
     void testDelete_ShouldDeleteTeamAndReturnTrue() {
          String username = "admin";
          Long teamId = 10L;

          Team team = new Team();
          team.setId(teamId);
          team.setName("Old Team");

          doNothing().when(userService).ensureUserIsAllowed(username);
          when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

          boolean result = teamService.delete(username, teamId);

          assertThat(result).isTrue();
          verify(teamRepository).delete(team);
     }

     @Test
     void testInviteMember_ShouldLogAndCheckPermissions() {
          String inviter = "owner";
          String email = "invitee@example.com";
          String teamName = "Alpha";

          Team team = new Team();
          team.setName(teamName);

          when(teamRepository.findByName(teamName)).thenReturn(team);
          doNothing().when(userService).ensureUserIsAllowed(inviter);

          teamService.inviteMember(inviter, email, teamName);

          // No email sent, just logs for now
          verify(userService).ensureUserIsAllowed(inviter);
     }

     @Test
     void testGetUserTeam_ShouldReturnUserTeam() {
          String username = "member";
          Team team = new Team();
          team.setId(5L);

          User user = new User();
          user.setUsername(username);
          user.setTeam(team);

          when(userRepository.findByUsername(username)).thenReturn(user);

          Team result = teamService.getUserTeam(username);

          assertThat(result).isEqualTo(team);
     }
     
}
