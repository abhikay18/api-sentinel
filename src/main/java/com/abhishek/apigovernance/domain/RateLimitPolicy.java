package com.abhishek.apigovernance.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rate_limit_policies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RateLimitPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ApiPlan plan;

    @Column(nullable = false)
    private int maxRequests;

    @Column(nullable = false)
    private int windowSeconds;
}
