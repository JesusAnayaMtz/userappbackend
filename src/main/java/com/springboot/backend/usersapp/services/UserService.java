package com.springboot.backend.usersapp.services;

import com.springboot.backend.usersapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> finddById(Long id);

    User save(User user);

    void deleteById(Long id);



}
