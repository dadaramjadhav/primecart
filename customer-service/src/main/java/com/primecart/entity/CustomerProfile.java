package com.primecart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customer_profiles",
       uniqueConstraints = @UniqueConstraint(name = "uk_customer_profiles_keycloak_user_id",
                                             columnNames = "keycloak_user_id"))
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_user_id",
            nullable = false,
            length = 100)
    private String keycloakUserId;

    @Column(length = 100)
    private String username;

    @Column(length = 255)
    private String email;

    @Column(name = "first_name",
            length = 100)
    private String firstName;

    @Column(name = "last_name",
            length = 100)
    private String lastName;

    @Column(length = 30)
    private String phone;

    @Column(name = "created_at",
            nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}