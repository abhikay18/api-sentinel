package com.abhishek.apigovernance.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_score_events")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AiScoreEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String apiKeyValue;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
