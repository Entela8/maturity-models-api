package com.maturity.models.api.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "model")
public class Model {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     @Column(nullable = false, unique = false)
     private String title;

     @Temporal(TemporalType.DATE)
     @Column(name = "created_at")
     @CreationTimestamp
     private Date createdAt;
 
     @OneToMany(
          mappedBy = "model",
          cascade = CascadeType.ALL,
          orphanRemoval = true
     )
     @JsonManagedReference
     private List<Question> questions = new ArrayList<>();
     
}
