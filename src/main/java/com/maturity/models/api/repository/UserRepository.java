package com.maturity.models.api.repository;

import com.maturity.models.api.model.Team;
import com.maturity.models.api.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    long count();
    User findByUsername(String username);
    List<User> findByTeam(Team team);
}
