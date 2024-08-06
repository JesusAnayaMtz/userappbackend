package com.springboot.backend.usersapp.repositories;

import com.springboot.backend.usersapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    //metodo para devolver una paginacion de usuarios
    Page<User> findAll(Pageable pageable);

}
