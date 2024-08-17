package com.springboot.backend.usersapp.services;

import com.springboot.backend.usersapp.entities.Role;
import com.springboot.backend.usersapp.entities.User;
import com.springboot.backend.usersapp.models.UserRequest;
import com.springboot.backend.usersapp.repositories.RoleRepository;
import com.springboot.backend.usersapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) this.userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);

    }

    @Override
    @Transactional
    public User save(User user) {
        //se crea una losta de roles
        List<Role> roles = new ArrayList<>();
        //se bsca el rol a asignar para ver sie existe
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        //validamos que el rol exista y si existe se pasa a la lista
        optionalRoleUser.ifPresent(role -> roles.add(role));

        if (user.getAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            //validamos que el rol exista y si existe se pasa a la lista
            optionalRoleAdmin.ifPresent(role -> roles.add(role));
        }

        //pasamos los roles al usuario
        user.setRoles(roles);

        //codificamos el password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id) {

        Optional<User> userEncontrado = userRepository.findById(id);
        if (userEncontrado.isPresent()) {
            User userBd = userEncontrado.get();
            userBd.setName(user.getName());
            userBd.setLastName(user.getLastName());
            userBd.setEmail(user.getEmail());
            userBd.setUsername(user.getUsername());

            //se usa para asiganar los roles en caso de actualizar
            List<Role> roles = new ArrayList<>();
            Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

            //validamos que el rol exista y si existe se pasa a la lista
            optionalRoleUser.ifPresent(role -> roles.add(role));

            if (user.getAdmin()){
                Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
                //validamos que el rol exista y si existe se pasa a la lista
                optionalRoleAdmin.ifPresent(role -> roles.add(role));
            }
            userBd.setRoles(roles);
            //guardamos si existe
            userRepository.save(userBd);
            return Optional.of(userBd);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }


}
