package com.springboot.backend.usersapp.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @NotEmpty
    @NotBlank
    private String name;

    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @NotEmpty
    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotEmpty
    @NotBlank
    @Size(min = 3, max = 12)
    @Column(name = "user_name", unique = true)
    private String username;

    private Boolean admin;
}
