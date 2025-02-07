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
@Table(name = "answer")
public class Answer {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     @Column(nullable = false, unique = true)
     private String content;

     @Column(nullable = false)
     private Integer score;

     @ManyToOne
     @JoinColumn(name = "question_id", nullable = false)
     private Question question;
}
