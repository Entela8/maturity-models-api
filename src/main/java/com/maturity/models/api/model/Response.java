package com.maturity.models.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "response")
public class Response {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne
     @JoinColumn(name = "answer_id", nullable = false)
     private Answer answer;

     @ManyToOne
     @JoinColumn(name = "question_id", nullable = false)
     private Question question;

     @ManyToOne
     @JoinColumn(name = "user_id", nullable = false)
     private User user;

     @ManyToOne
     @JoinColumn(name = "session_id", nullable = false)
     private Session session;
}