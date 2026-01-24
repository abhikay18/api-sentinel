package com.abhishek.apigovernance.repository;

import com.abhishek.apigovernance.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateLimitPolicyRepository
        extends JpaRepository<RateLimitPolicy, Long> {

    Optional<RateLimitPolicy> findByPlan(ApiPlan plan);
}
