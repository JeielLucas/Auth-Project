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

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();
        String encryptedPassword = passwordEncoder.encode("senha123");
        User user = new User("zvladesx3@gmail.com", encryptedPassword, UserRole.USER);
        user.setActive(true);
        userRepository.save(user);
    }
}
