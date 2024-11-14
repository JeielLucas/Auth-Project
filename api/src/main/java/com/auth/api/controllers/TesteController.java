package com.auth.api.controllers;

import com.auth.api.entities.User;
import com.auth.api.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/test")
public class    TesteController {

    private final UserRepository userRepository;

    TesteController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<User> users(){
        return userRepository.findAll();
    }
}
