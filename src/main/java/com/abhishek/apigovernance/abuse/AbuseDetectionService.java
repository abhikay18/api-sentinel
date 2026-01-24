package com.abhishek.apigovernance.abuse;

import com.abhishek.apigovernance.domain.BlockedEntity;
import com.abhishek.apigovernance.repository.BlockedEntityRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AbuseDetectionService {

    private final BlockedEntityRepository repository;
    private final RedisTemplate<String, String> redisTemplate;

    public AbuseDetectionService(
            BlockedEntityRepository repository,
            RedisTemplate<String, String> redisTemplate
    ) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    /* =====================================================
       BLOCK CHECK
       ===================================================== */

    public boolean isBlocked(String value) {

        // 1️⃣ Redis short-circuit
        if (Boolean.TRUE.equals(redisTemplate.hasKey("blocked:" + value))) {
            return true;
        }

        // 2️⃣ DB fallback
        return repository.findByValue(value)
                .filter(be -> be.getBlockedUntil().isAfter(LocalDateTime.now()))
                .map(be -> {
                    redisTemplate.opsForValue()
                            .set(
                                    "blocked:" + value,
                                    "1",
                                    Duration.ofMinutes(10)
                            );
                    return true;
                })
                .orElse(false);
    }

    /* =====================================================
       BLOCK ACTIONS
       ===================================================== */

    public void blockTemporarily(String value, String reason, int minutes) {
        block(value, reason, "SYSTEM", null, minutes);
        startRateLimitCooldown(value, minutes);
    }

    public void blockByAi(String value, double aiScore, int minutes) {
        block(value, "AI-detected abnormal behavior", "AI", aiScore, minutes);
        startAiCooldown(value, minutes);
    }

    private void block(
            String value,
            String reason,
            String source,
            Double aiScore,
            int minutes
    ) {

        LocalDateTime until =
                LocalDateTime.now().plusMinutes(minutes);

        BlockedEntity entity =
                repository.findByValue(value)
                        .orElseGet(BlockedEntity::new);

        entity.setValue(value);
        entity.setReason(reason);
        entity.setDetectionSource(source);
        entity.setAiScore(aiScore);
        entity.setBlockedUntil(until);

        repository.save(entity);

        redisTemplate.opsForValue()
                .set(
                        "blocked:" + value,
                        "1",
                        Duration.ofMinutes(minutes)
                );
    }

    /* =====================================================
       FULL UNBLOCK (ADMIN / API SAFE)
       ===================================================== */

    public void fullyUnblock(String value) {

        // 1️⃣ Remove DB record
        repository.findByValue(value)
                .ifPresent(repository::delete);

        // 2️⃣ Remove Redis block flag
        redisTemplate.delete("blocked:" + value);

        // 3️⃣ Clear cooldowns
        clearAiCooldown(value);
        clearRateLimitCooldown(value);
    }

    /* =====================================================
       COOLDOWNS
       ===================================================== */

    public boolean isAiCooldownActive(String value) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("ai:cooldown:" + value)
        );
    }

    public void startAiCooldown(String value, int minutes) {
        redisTemplate.opsForValue()
                .set(
                        "ai:cooldown:" + value,
                        "1",
                        Duration.ofMinutes(minutes)
                );
    }

    public void clearAiCooldown(String value) {
        redisTemplate.delete("ai:cooldown:" + value);
    }

    public boolean isRateLimitCooldownActive(String value) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("rate:cooldown:" + value)
        );
    }

    public void startRateLimitCooldown(String value, int minutes) {
        redisTemplate.opsForValue()
                .set(
                        "rate:cooldown:" + value,
                        "1",
                        Duration.ofMinutes(minutes)
                );
    }

    public void clearRateLimitCooldown(String value) {
        redisTemplate.delete("rate:cooldown:" + value);
    }

    /* ---------------- BASELINE RETRAIN ---------------- */

    public boolean isBaselineRetrainActive(String value) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("baseline:retrain:" + value)
        );
    }

    public void startBaselineRetrain(String value, int minutes) {
        redisTemplate.opsForValue().set(
                "baseline:retrain:" + value,
                "1",
                Duration.ofMinutes(minutes)
        );
    }

    public void clearBaselineRetrain(String value) {
        redisTemplate.delete("baseline:retrain:" + value);
    }

}
