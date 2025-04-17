package com.maturity.models.api.requests.user;

import com.maturity.models.api.model.Role;

import io.micrometer.common.lang.NonNull;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {
     @NonNull
     @Valid
     private String username;

     @NonNull
     @Valid
     private String email;

     @NonNull
     @Valid
     private String firstName;

     @NonNull
     @Valid
     private String lastName;

     @NonNull
     @Valid
     private Role role;

     @NonNull
     @Valid
     private String password;

     @Valid
     private Long teamId;
}
