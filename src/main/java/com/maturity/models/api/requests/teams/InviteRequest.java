package com.maturity.models.api.requests.teams;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteRequest {

     @NotBlank(message = "Email cannot be empty")
     @NotNull(message = "Email cannot be null")
     private String email;

     @NotBlank(message = "Team cannot be empty")
     @NotNull(message = "Team cannot be null")
     private String team;
}
