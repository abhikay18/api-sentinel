package com.abhishek.apigovernance.rate_limit;

import com.abhishek.apigovernance.domain.*;
import com.abhishek.apigovernance.repository.RateLimitPolicyRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    private final RedisTemplate<String, Integer> redisTemplate;
    private final RateLimitPolicyRepository policyRepository;

    public RateLimiterService(RedisTemplate<String, Integer> redisTemplate,
                              RateLimitPolicyRepository policyRepository) {
        this.redisTemplate = redisTemplate;
        this.policyRepository = policyRepository;
    }

    public boolean allowRequest(ApiKey apiKey) {

        RateLimitPolicy policy = policyRepository
                .findByPlan(apiKey.getPlan())
                .orElseThrow(() ->
                        new RuntimeException("Rate policy not configured"));

        String redisKey = "rate:" + apiKey.getKeyValue();
        int limit = policy.getMaxRequests();
        int window = policy.getWindowSeconds();

        Integer current = redisTemplate.opsForValue().get(redisKey);

        if (current == null) {
            redisTemplate.opsForValue()
                    .set(redisKey, limit - 1, Duration.ofSeconds(window));
            return true;
        }

        if (current > 0) {
            redisTemplate.opsForValue().decrement(redisKey);
            return true;
        }

        return false;
    }

    public void resetLimiter(String apiKey) {
        redisTemplate.delete("rate:count:" + apiKey);
        redisTemplate.delete("rate:window:" + apiKey);
    }

}
