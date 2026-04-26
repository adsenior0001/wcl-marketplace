package com.wcl.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users") // 'user' is a reserved keyword in PostgreSQL, so we name the table 'users'
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private String companyName; // SKF WCL is a B2B platform, so company affiliation is key

    @Column(nullable = false)
    private String role; // e.g., DISTRIBUTOR, INTERNAL_ADMIN

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // This method automatically sets the timestamp right before saving to the database
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}