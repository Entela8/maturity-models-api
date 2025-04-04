package com.maturity.models.api.requests.models;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateModelRequest {
    @Valid
    @NotBlank(message = "Model title cannot be empty")
    private String title;

    @Valid
    private List<CreateQuestionRequest> questions;
}
