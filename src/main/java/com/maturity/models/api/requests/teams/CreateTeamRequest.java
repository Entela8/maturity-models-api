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
public class CreateTeamRequest {

    @NotBlank(message = "Answer content cannot be empty")
    @NotNull(message = "Answer score cannot be null")
    private String name;

}
