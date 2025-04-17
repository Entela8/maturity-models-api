package com.maturity.models.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
     private String questionTitle;
     private int score;

     public AnswerDTO(String questionTitle, int score) {
          this.questionTitle = questionTitle;
          this.score = score;
     }
}