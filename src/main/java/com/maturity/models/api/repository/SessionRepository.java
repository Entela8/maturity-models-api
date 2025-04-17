package com.maturity.models.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.Team;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

     List<Session> findAllByTeam(Team team);
     List<Session> findByModelIdAndActiveTrue(Long modelId);
     Optional<Session> findByModelIdAndTeamId(Long modelId, Long teamId);
}
