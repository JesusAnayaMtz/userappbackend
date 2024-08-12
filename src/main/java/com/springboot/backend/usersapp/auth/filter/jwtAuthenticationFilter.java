package com.springboot.backend.usersapp.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.usersapp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//IMPORTAMOS LA CLASE PARA LA SECRET KEY
import static com.springboot.backend.usersapp.auth.tokenJwtConfig.*;


import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class jwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public jwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //metodo para la autenticacion
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //recibimos el username y el password
        String username = null;
        String password = null;

        //convertimos los datos del body que vienen del tipo string a un tipo objeto usuario
        try {
            //creamos un usuario se poblan con los datos del request
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            //asignamos los valores que vienen del user
            username= user.getUserName();
            password = user.getPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //pasamos los valos de username y password en el authenticationtoken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        //retornamos el authenticatiomanager y le pasamos con autenthicate el token
        return this.authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //implementamos la creacion de nuestro jwt
        //obtenemos el user de springsecurity  va a ser igual y lo obtenemos el objeto mediante el authresult y casteamos al tipo de dato user de spring
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)authResult.getPrincipal();
        String username = user.getUsername();
        //obtenemos los roles devuelve un tipo collection
       Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

       //agregamos los roles como claims y se reescriben a tipo json con objectmapper para agregarlos en el jwt
        Claims claims = Jwts.claims().add("authorities", new ObjectMapper().writeValueAsString(roles)).add("username", username).build();

        //firmamos el token con la llave secreta que viene de tokenJwtCOnfig que es la secretkey, le asignamos la fecha de creacion y la de expiracion en milisegundos
        String jwt = Jwts.builder().subject(username).claims(claims).signWith(SECRET_KEY).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 3600000))
                .compact();

        //pasamos el token en la cabezera(header) de la respuesta
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + jwt);

        //generamos el json
        Map<String, String> body = new HashMap<>();
        //pasamos el token
        body.put("token", jwt);
        //el username
        body.put("username", username);
        //un msj de inicio de sesion pasando el usuario que inicio
        body.put("message",String.format("Hola %s iniciado sesion con exito", username));

        //guardamos el map en el cuerpo de la respuesta como un json con objectmapper y writevalueasstring
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        //le damos el tipo de contenido
        response.setContentType(CONTENT_TYPE);
        //el status de la respuesta
        response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

    }
}
