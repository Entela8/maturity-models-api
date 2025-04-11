package com.maturity.models.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

}
