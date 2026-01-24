package com.abhishek.apigovernance.ai;

import com.abhishek.apigovernance.ai.dto.ApiUsageFeatures;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class ApiUsageFeatureCollector {

    private final StringRedisTemplate redisTemplate;

    public ApiUsageFeatureCollector(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public ApiUsageFeatures collect(String apiKey, String endpoint, boolean error) {

        String baseKey = "ai:usage:" + apiKey;
        long now = Instant.now().getEpochSecond();

        redisTemplate.opsForValue().increment(baseKey + ":rpm");
        redisTemplate.opsForSet().add(baseKey + ":endpoints", endpoint);

        if (error) {
            redisTemplate.opsForValue().increment(baseKey + ":errors");
        }

        int rpm = getInt(baseKey + ":rpm");
        int errors = getInt(baseKey + ":errors");
        int uniqueEndpoints = redisTemplate.opsForSet()
                .members(baseKey + ":endpoints").size();

        double avgInterval = 200.0; // simplified for now

        return new ApiUsageFeatures(
                apiKey,
                endpoint,
                rpm,
                errors,
                uniqueEndpoints,
                avgInterval
        );
    }

    private int getInt(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Integer.parseInt(value);
    }


}
