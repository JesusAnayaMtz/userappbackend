package com.springboot.backend.usersapp.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(authz ->
            authz.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll()
                    .anyRequest().authenticated()
        ).csrf(config -> config.disable())  //se desabilita el csrf ya que esto se maneja por el lado de angular
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //y el sesion managment se maneja sin estado con stateless
                .build();
    }

}
