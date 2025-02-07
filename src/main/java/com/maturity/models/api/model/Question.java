package com.maturity.models.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "question")
public class Question {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     @Column(nullable = false, unique = true)
     private String content;

     @ManyToOne
     @JoinColumn(name = "model_id", nullable = false)
     private Model model;

     @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<Answer> answers;
}
