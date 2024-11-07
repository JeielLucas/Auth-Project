package com.auth.api.entities;

import com.auth.api.enums.StatusEmail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String ownerRef;
    private String emailTo;
    private String subject;
    private String text;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private StatusEmail statusEmail;
}
