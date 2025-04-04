package com.maturity.models.api.dto;

import com.maturity.models.api.model.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
     private Long id;
     private String username;
     private String firstName;
     private String lastName;
     private String email;
     private Role role;
}
