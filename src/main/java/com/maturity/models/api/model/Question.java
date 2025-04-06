package com.maturity.models.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
 
     @Column(nullable = false, unique = false)
     private String content;

     @ManyToOne
     @JsonBackReference
     @JoinColumn(name = "model_id", nullable = false)
     private Model model;

     @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<Answer> answers;
}

