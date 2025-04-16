package com.maturity.models.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maturity.models.api.model.Response;
import com.maturity.models.api.model.Session;
import com.maturity.models.api.model.User;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

     List<Response> findByUserAndSessionAndQuestionIdIn(User user, Session session, List<Long> questionIds);

}
