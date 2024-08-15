package com.springboot.backend.usersapp.controllers;

import com.springboot.backend.usersapp.entities.User;
import com.springboot.backend.usersapp.services.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping()
    public List<User> findAll(){
        return userService.findAll();
    }

    @GetMapping("/page/{page}")
    public Page<User> findAllPageable(@PathVariable Integer page){
        //definimos una variable del tipo Pageable y definimos nuestras reglas de paginacion con pagerequest y el primer parametro es el numero de pagina y el segundo es cuantos elementos mostrara nuestra pagina
        Pageable pageable = PageRequest.of(page, 5);
        //le pasamos por parametro el pageable al finafll
        return userService.findAll(pageable);
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
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result){
        ///validamos los campos y regresamos el mensaje con el error
        ResponseEntity<Map<String, String>> errors = Validation(result);
        if (errors != null) return errors;
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, @PathVariable Long id ){
        ///validamos los campos y regresamos el mensaje con el error
        ResponseEntity<Map<String, String>> errors = Validation(result);
        if (errors != null) return errors;

        Optional<User> userEncontrado = userService.finddById(id);
        if (userEncontrado.isPresent()){
            User userBd = userEncontrado.get();
            userBd.setName(user.getName());
            userBd.setLastName(user.getLastName());
            userBd.setEmail(user.getEmail());
            userBd.setUsername(user.getUsername());
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

    private static ResponseEntity<Map<String, String>> Validation(BindingResult result) {
        if (result.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
        return null;
    }
}
