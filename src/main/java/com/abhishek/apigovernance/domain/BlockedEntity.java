package com.abhishek.apigovernance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blocked_entities")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BlockedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String value; // API key or IP

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime blockedUntil;

    private Double aiScore;
    private String detectionSource; // RATE_LIMIT / AI

    @Column(length = 500)
    private String explanation; // ✅ NEW

}
