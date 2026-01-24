package com.abhishek.apigovernance.rate_limit;

import com.abhishek.apigovernance.domain.ApiPlan;

import java.util.Map;

public class RateLimitConfig {

    // requests per minute
    public static final Map<ApiPlan, Integer> LIMITS = Map.of(
            ApiPlan.FREE, 10,
            ApiPlan.PRO, 100
    );

    public static int getLimit(ApiPlan plan) {
        return LIMITS.get(plan);
    }
}
