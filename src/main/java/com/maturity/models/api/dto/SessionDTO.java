package com.maturity.models.api.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionDTO {
     private Long id;
     private Long modelId;
     private Long teamId;
     private boolean active;
     private Date date;
}
