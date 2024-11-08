package com.auth.api.configs;

import com.auth.api.entities.User;
import com.auth.api.enums.UserRole;
import com.auth.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Database implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        userRepository.deleteAll();

        User user = new User("zvladesx3@gmail.com", "senha123", UserRole.USER);
        user.setActive(true);
        userRepository.save(user);
    }
}
