package com.maturity.models.api.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticDTO {
     private Long userId;
     private String userName;
     private List<AnswerDTO> answers;

     public StatisticDTO(Long userId, String userName, List<AnswerDTO> answers) {
          this.userId = userId;
          this.userName = userName;
          this.answers = answers;
      }
}