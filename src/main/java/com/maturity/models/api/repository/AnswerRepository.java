package com.maturity.models.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
