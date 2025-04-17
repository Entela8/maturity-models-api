package com.maturity.models.api.requests.sessions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeactivateSessionRequest {
     @NotNull(message = "Model ID cannot be null")
     private Long modelId;

     @NotNull(message = "Team ID cannot be null")
     private Long teamId;
}