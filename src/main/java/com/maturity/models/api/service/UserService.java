package com.maturity.models.api.service;

import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.exception.UsernameAlreadyInUseException;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.Team;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.TeamRepository;
import com.maturity.models.api.repository.UserRepository;
import com.maturity.models.api.requests.user.CreateUserRequest;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamRepository teamRepository;

    public User register(CreateUserRequest user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyInUseException("Username is already in use");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setRole(user.getRole());
        newUser.setUsername(user.getUsername());
        
        Optional<Team> teamOptional = teamRepository.findById(user.getTeamId());
        teamOptional.ifPresent(newUser::setTeam);

        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(newUser);
    }

    public User getUserDetails(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserDTO> getAllUsers(String username) {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to view users");
        }

        List<UserDTO> userDTOList = new ArrayList<>();
            
        List<User> users = userRepository.findAll();

        for (User u : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(u.getId());
            userDTO.setUsername(u.getUsername());
            userDTO.setFirstName(u.getFirstName());
            userDTO.setLastName(u.getLastName());
            userDTO.setEmail(u.getEmail());
            userDTO.setRole(u.getRole());
            if(u.getTeam() != null) {
                userDTO.setTeam(u.getTeam().getId());
            }
            userDTOList.add(userDTO);
        }

        return userDTOList;
    }

    public void ensureUserIsAllowed(String username) {
        User user = userRepository.findByUsername(username);
    
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to do this action.");
        }
    }    

}
