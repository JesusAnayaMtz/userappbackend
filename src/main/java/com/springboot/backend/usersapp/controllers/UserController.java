package com.springboot.backend.usersapp.controllers;

import com.springboot.backend.usersapp.entities.User;
import com.springboot.backend.usersapp.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping()
    public List<User> findAll(){
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<User> userFind = userService.finddById(id);
        if (userFind.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(userFind.orElseThrow());
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No se encontro el usuario"));
        }

    }

    @PostMapping()
    public ResponseEntity<User> create(@RequestBody User user){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user){
        Optional<User> userEncontrado = userService.finddById(id);
        if (userEncontrado.isPresent()){
            User userBd = userEncontrado.get();
            userBd.setName(user.getName());
            userBd.setLastName(user.getLastName());
            userBd.setEmail(user.getEmail());
            userBd.setUserName(user.getUserName());
            userBd.setPassword(user.getPassword());

            return  ResponseEntity.status(HttpStatus.OK).body(userService.save(userBd));
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Optional<User> usuarioEncontrado = userService.finddById(id);
        if (usuarioEncontrado.isPresent()){
            userService.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario Eliminado Correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario A Eliminar No Se Encontro");
        }
    }
}
