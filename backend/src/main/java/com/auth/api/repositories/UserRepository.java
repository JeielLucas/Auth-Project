package com.auth.api.repositories;

import com.auth.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findByToken(String activationToken);
}
