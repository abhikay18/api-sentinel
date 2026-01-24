package com.abhishek.apigovernance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", unique = true, nullable = false)
    private String keyValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiPlan plan;

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
}
