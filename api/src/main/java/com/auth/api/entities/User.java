package com.auth.api.entities;


import com.auth.api.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    private String id;

    private String email;
    private String password;
    private UserRole role;

    private boolean isActive;
    private String activationToken;
    private LocalDateTime tokenExpiration;

    private LocalDateTime createdAt;

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
