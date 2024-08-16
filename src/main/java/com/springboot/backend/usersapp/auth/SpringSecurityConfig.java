package com.springboot.backend.usersapp.auth;

import com.springboot.backend.usersapp.auth.filter.JwtValidationFilter;
import com.springboot.backend.usersapp.auth.filter.jwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    //metoto que devuelve el authentication
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //se encarga de codificar el password
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(authz ->
            authz.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "api/users/{id}").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated())
                .addFilter(new jwtAuthenticationFilter(authenticationManager())) //agregamos el filtro creado para la authenticacion
                .addFilter(new JwtValidationFilter(authenticationManager())) //agregamos el filtro creado para la validacion
                .csrf(config -> config.disable())  //se desabilita el csrf ya que esto se maneja por el lado de angular
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //y el sesion managment se maneja sin estado con stateless
                .build();
    }

}
