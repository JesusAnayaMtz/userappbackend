package com.springboot.backend.usersapp.auth;

import com.springboot.backend.usersapp.auth.filter.JwtValidationFilter;
import com.springboot.backend.usersapp.auth.filter.jwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(configurationSource()))   //pasamos la configutacion del cors
                .addFilter(new jwtAuthenticationFilter(authenticationManager())) //agregamos el filtro creado para la authenticacion
                .addFilter(new JwtValidationFilter(authenticationManager())) //agregamos el filtro creado para la validacion
                .csrf(config -> config.disable())  //se desabilita el csrf ya que esto se maneja por el lado de angular
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //y el sesion managment se maneja sin estado con stateless
                .build();
    }

    //se usara para poder usar springsecurity en angular se anota con bean ya que sera un componente de spring
    @Bean
    CorsConfigurationSource configurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));  //configura el acceso a cualquiera
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));  //aqui se configura el acceso de origin de angular
        config.setAllowedMethods(Arrays.asList("POST","GET","PUT","DELETE")); //se configura l acceso a los metodos del request
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); //se configura el acceso a las cabezeras
        config.setAllowCredentials(true);  //acceso a las credenciales

        //vel url de la configuracion y le pasamos acceso a las url para el filtro en todas la rutas y el config
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    //lo pasamos como prioridad a los filtros para angular se usara
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter(){
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(this.configurationSource()));
        //le damos la precendia mas alta al corsfilter
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }

}
