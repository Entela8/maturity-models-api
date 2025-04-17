package com.maturity.models.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

     @Query("SELECT DISTINCT s.model FROM Session s WHERE s.team.id = :teamId")
     List<Model> findModelsByTeamId(Long teamId);
     
}
