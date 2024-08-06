package com.springboot.backend.usersapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @NotBlank
    private String name;

    @NotEmpty
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
    private String userName;

    @NotEmpty
    @NotBlank
    @Size(min = 6)
    private String password;
}
