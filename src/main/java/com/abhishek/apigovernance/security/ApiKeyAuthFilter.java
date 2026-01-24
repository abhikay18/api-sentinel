package com.abhishek.apigovernance.security;

import com.abhishek.apigovernance.abuse.AbuseDetectionService;
import com.abhishek.apigovernance.ai.AiAbuseDetectionService;
import com.abhishek.apigovernance.ai.ApiUsageFeatureCollector;
import com.abhishek.apigovernance.ai.dto.ApiUsageFeatures;
import com.abhishek.apigovernance.domain.AiScoreEvent;
import com.abhishek.apigovernance.domain.ApiKey;
import com.abhishek.apigovernance.rate_limit.RateLimiterService;
import com.abhishek.apigovernance.repository.AiScoreEventRepository;
import com.abhishek.apigovernance.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ApiKeyAuthFilter.class);

    private final ApiKeyRepository apiKeyRepository;
    private final RateLimiterService rateLimiterService;
    private final AbuseDetectionService abuseDetectionService;
    private final ApiUsageFeatureCollector featureCollector;
    private final AiAbuseDetectionService aiService;
    private final AiScoreEventRepository aiScoreEventRepository;

    public ApiKeyAuthFilter(
            ApiKeyRepository apiKeyRepository,
            RateLimiterService rateLimiterService,
            AbuseDetectionService abuseDetectionService,
            ApiUsageFeatureCollector featureCollector,
            AiAbuseDetectionService aiService,
            AiScoreEventRepository aiScoreEventRepository
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.rateLimiterService = rateLimiterService;
        this.abuseDetectionService = abuseDetectionService;
        this.featureCollector = featureCollector;
        this.aiService = aiService;
        this.aiScoreEventRepository = aiScoreEventRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Only protect API endpoints
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKeyValue = request.getHeader("X-API-KEY");
        log.info("API FILTER HIT for key: {}", apiKeyValue);

        /* ================= BASIC AUTH ================= */

        if (apiKeyValue == null || apiKeyValue.isBlank()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing API Key");
            return;
        }

        ApiKey apiKey = apiKeyRepository
                .findByKeyValue(apiKeyValue)
                .orElse(null);

        if (apiKey == null || !apiKey.isActive()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid or revoked API Key");
            return;
        }

        /* ================= HARD BLOCK (FIRST) ================= */

        if (abuseDetectionService.isBlocked(apiKeyValue)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.getWriter().write("""
            {
              "status": 403,
              "error": "Forbidden",
              "message": "API key temporarily blocked"
            }
            """);
            return;
        }

        /* ================= RATE LIMIT (SECOND) ================= */

        if (!rateLimiterService.allowRequest(apiKey)
                && !abuseDetectionService.isRateLimitCooldownActive(apiKeyValue)) {

            abuseDetectionService.blockTemporarily(
                    apiKeyValue,
                    "Rate limit exceeded",
                    5
            );

            abuseDetectionService.startRateLimitCooldown(apiKeyValue, 2);

            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
            return;
        }

        /* ================= AI FEATURE COLLECTION ================= */

        ApiUsageFeatures features = featureCollector.collect(
                apiKeyValue,
                request.getRequestURI(),
                false
        );

        /* ================= AI ANALYSIS ================= */

        // ===== AI ANALYSIS =====
        AiAbuseDetectionService.AiDecision decision =
                aiService.analyze(features);

        log.info(
                "AI SCORE = {} | ANOMALY = {}",
                decision.score(),
                decision.anomaly()
        );

// ✅ ALWAYS SAVE SCORE (EVEN IF BLOCKING)
        AiScoreEvent event = new AiScoreEvent();
        event.setApiKeyValue(apiKeyValue);
        event.setScore(decision.score());
        event.setCreatedAt(LocalDateTime.now());
        aiScoreEventRepository.save(event);


        /* ================= AI BLOCK (LAST) ================= */

        if (decision.mature()
                && decision.anomaly()
                && !abuseDetectionService.isAiCooldownActive(apiKeyValue)
                && !abuseDetectionService.isBaselineRetrainActive(apiKeyValue)) {


            log.warn(
                    "AI BLOCK TRIGGERED | key={} | score={} | reason={}",
                    apiKeyValue,
                    decision.score(),
                    decision.explanation()
            );

            abuseDetectionService.blockByAi(
                    apiKeyValue,
                    decision.score(),
                    15
            );

            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
    {
      "error": "Blocked due to suspicious activity",
      "aiScore": %s,
      "reason": "%s"
    }
    """.formatted(
                    decision.score(),
                    decision.explanation()
            ));

            return;
        }



        /* ================= FINAL PASS ================= */

        apiKey.setLastUsedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);

        filterChain.doFilter(request, response);
    }
}
