package com.maturity.models.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
     Team findByName(String name);
}
