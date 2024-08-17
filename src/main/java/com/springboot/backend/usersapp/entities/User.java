package com.springboot.backend.usersapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    @Transient  //significa que este campo no es mapeado en la tabla solos se maneja a nivel de clase
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  //esto es para que se pueda poblar en el json cuando se crea en la clase entiti o user request
    private Boolean admin;

    @NotEmpty
    @NotBlank
    @Size(min = 6)
    private String password;

    @JsonIgnoreProperties({"handler", "hibernateLazyinitializer"})
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            //tabla que establece la relacion
            name = "users_roles",
            //establecemos la relacion principal
            joinColumns = @JoinColumn(name = "user_id"),
            //la inversa de la relacion
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            //los campos unicos ya que los dos son llaves primarias
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role_id"})}
    )
    private List<Role> roles;

    //inicializamos nuestra lista de roles
    public User(){
        this.roles = new ArrayList<>();
    }
}
