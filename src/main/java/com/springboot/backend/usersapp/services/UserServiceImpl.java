package com.springboot.backend.usersapp.services;

import com.springboot.backend.usersapp.entities.Role;
import com.springboot.backend.usersapp.entities.User;
import com.springboot.backend.usersapp.models.IUser;
import com.springboot.backend.usersapp.models.UserRequest;
import com.springboot.backend.usersapp.repositories.RoleRepository;
import com.springboot.backend.usersapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        //creamos una lista en la cual mandamos a llamar a al metodo getroles para asignar los roles
        List<Role> roles = getRoles(user);

        //pasamos los roles al usuario
        user.setRoles(roles);

        //codificamos el password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    //metodo para asignar los roles y validar que existan tanto user como admin
    private List<Role> getRoles(IUser user) {
        //se crea una losta de roles
        List<Role> roles = new ArrayList<>();
        //se bsca el rol a asignar para ver sie existe
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");

        //validamos que el rol exista y si existe se pasa a la lista
        optionalRoleUser.ifPresent(role -> roles.add(role));

        //validamos si es admin para siganrle el rol
        if (user.isAdmin()){
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            //validamos que el rol exista y si existe se pasa a la lista
            optionalRoleAdmin.ifPresent(role -> roles.add(role));
        }
        return roles;
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

            //pasamos los roles al user que se va a guadar en la bd el actualizado
            userBd.setRoles(getRoles(user));

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
