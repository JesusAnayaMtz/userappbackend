package com.springboot.backend.usersapp.services;

import com.springboot.backend.usersapp.entities.User;
import com.springboot.backend.usersapp.models.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

    Optional<User> update(UserRequest user, Long id);



}
