package com.auth.api.configs;

import com.auth.api.entities.User;
import com.auth.api.enums.UserRole;
import com.auth.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Database implements CommandLineRunner {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    public Database(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        User user = new User("teste@teste.com", passwordEncoder.encode("12345678"), UserRole.ADMIN);
        user.setActive(true);
        userRepository.save(user);
    }
}