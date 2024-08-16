package com.springboot.backend.usersapp.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.usersapp.auth.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import static com.springboot.backend.usersapp.auth.tokenJwtConfig.*;

import java.io.IOException;
import java.util.*;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //cuando accedemos a una pagina que utiliza seguridad cada vez que visitemos una pagina que este protegida, esto validara la firma guardada
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //AQUI OBTENEMOS EL TOKEN CON EL BEARER
        String header = request.getHeader(HEADER_AUTHORIZATION);
        //validamos que no sea null y que el bearer venga en la cabezera (header) el inicio del token cn startwind
        if (header == null || !header.startsWith(PREFIX_TOKEN)){
            chain.doFilter(request, response);
            return;
        }

        //obtenemos el token y remplazamos la palabra bearer por nada del token
        String token = header.replace(PREFIX_TOKEN, "");

        try {
            //validamos nuesytro token CON CLAIMS con la llave que fue firmada nuestro token, leemos nuestro token firmado con porseSignedclaims, obtenemos el payload
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
            //obtenemos el username
            //aqui viene en string
            String username = (String) claims.get("username");
            //aqui viene en json por que es un arreglo de roles
            Object authoritiesClaims = claims.get("authorities");

            //convtertimos a una coleccion  una lista de grantedautorithies
            Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper()
                    //mezclamos el contructor de la segunda clase con el de la priemra que es la original del simplegrantedauthroties
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                    .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            //nos autenticamos pasando el username y roles
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,null, roles);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //continuamos con la autenticaciomn
            chain.doFilter(request, response);
        } catch (JwtException e){
            Map<String,String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token no es valido");

            //tomamos loa datos bodi, del map y lo convertimos a un json
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType(CONTENT_TYPE);
        }
    }
}
