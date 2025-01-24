package com.maturity.models.api.service;

import com.maturity.models.api.exception.UsernameAlreadyInUseException;
import com.maturity.models.api.model.User;
import com.maturity.models.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User setGitlabAccessToken(String username, String token) {
        User user = userRepository.findByUsername(username);
        user.setGitlabAccessToken(token);
        return userRepository.save(user);
    }

}
