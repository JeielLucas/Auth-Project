package com.auth.api.entities;


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
    private boolean isActive;
    private String activationToken;
    private LocalDateTime tokenExpiration;
    private LocalDateTime createdAt;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
