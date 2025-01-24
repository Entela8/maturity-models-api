package com.maturity.models.api.requests.user;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetGitlabAccessTokenRequest {
    @NonNull
    @Valid
    @NotBlank(message = "Gitlab access token is required and cannot be empty")
    private String accessToken;
}
