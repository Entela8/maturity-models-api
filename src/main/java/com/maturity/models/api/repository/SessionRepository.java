package com.maturity.models.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.Team;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

     List<Session> findAllByTeam(Team team);

}
