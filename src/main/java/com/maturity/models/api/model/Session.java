package com.maturity.models.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session")
public class Session {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
 
     @ManyToOne
     @JoinColumn(name = "model_id", nullable = false)
     private Model model;
 
     @ManyToOne
     @JoinColumn(name = "team_id", nullable = false)
     private Team team;
 
     private boolean active = false;

     @Temporal(TemporalType.TIMESTAMP)
     private Date date;

 }
