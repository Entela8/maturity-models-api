package com.maturity.models.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Model;
import com.maturity.models.api.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
     List<Question> findByModel(Model model);
}
