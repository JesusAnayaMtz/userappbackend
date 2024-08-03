package com.springboot.backend.usersapp.repositories;

import com.springboot.backend.usersapp.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
