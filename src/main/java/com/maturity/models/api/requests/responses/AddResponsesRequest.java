package com.maturity.models.api.requests.responses;

import java.util.List;

import com.maturity.models.api.dto.ResoponseDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddResponsesRequest {

     @NotNull
     private String sessionId;

     private List<ResoponseDTO> responses;

}