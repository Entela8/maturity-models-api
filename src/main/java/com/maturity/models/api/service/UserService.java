package com.maturity.models.api.service;

import com.maturity.models.api.dto.UserDTO;
import com.maturity.models.api.exception.UsernameAlreadyInUseException;
import com.maturity.models.api.model.Role;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UsernameAlreadyInUseException("Username is already in use");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
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

            userDTOList.add(userDTO);
        }

        return userDTOList;
    }

}
