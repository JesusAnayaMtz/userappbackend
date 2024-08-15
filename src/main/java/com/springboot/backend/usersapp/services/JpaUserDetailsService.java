package com.springboot.backend.usersapp.services;

import com.springboot.backend.usersapp.entities.User;
import com.springboot.backend.usersapp.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    //este es el metodo de login
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //primero vamos a buscar al usuario que viene del usuario registrado
        Optional<User> optionalUser = userRepository.findByUsername(username);
        //validamos que no este vacio, si esta vacio manda una exception
        if (optionalUser.isEmpty()){
            throw new UsernameNotFoundException(String.format("UserName %s no existe en el sistema", username));
        }

        // obtenemos el usuario ya sea con get o con or elsethow
        User user = optionalUser.orElseThrow();

        //creamos una lista para obtener los roles de la clase role, con  y lo convertimos con stream y map para convertirlo a una tipo role pero de Grantend authorties y los pasamos a una lista
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        //aqui pasamos el user name, y el password  y valida contra el password de login
        return new org.springframework.security.core.userdetails.User(username,
                user.getPassword(),
                true,
                true,
                true,
                true, authorities);
    }
}
