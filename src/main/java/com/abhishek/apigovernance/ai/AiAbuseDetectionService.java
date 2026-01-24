package com.abhishek.apigovernance.ai;

import com.abhishek.apigovernance.ai.dto.ApiUsageFeatures;
import com.abhishek.apigovernance.domain.ApiUsageBaseline;
import com.abhishek.apigovernance.repository.ApiUsageBaselineRepository;
import org.springframework.stereotype.Service;

@Service
public class AiAbuseDetectionService {

    private static final int MIN_SAMPLES_FOR_ENFORCEMENT = 30;
    private static final double DECAY_FACTOR = 0.85; // 🔥 Step 5

    private final BaselineLearningService baselineService;
    private final ApiUsageBaselineRepository baselineRepo;

    public AiAbuseDetectionService(
            BaselineLearningService baselineService,
            ApiUsageBaselineRepository baselineRepo
    ) {
        this.baselineService = baselineService;
        this.baselineRepo = baselineRepo;
    }

    public AiDecision analyze(ApiUsageFeatures features) {

        String apiKey = features.apiKey();

        ApiUsageBaseline baseline =
                baselineRepo.findById(apiKey).orElse(null);

        /* =====================================================
         * 1️⃣ BASELINE WARM-UP (NO BLOCKING)
         * ===================================================== */
        if (baseline == null || baseline.getSamples() < MIN_SAMPLES_FOR_ENFORCEMENT) {

            baselineService.updateBaseline(apiKey, features);

            int samples = baseline == null ? 0 : baseline.getSamples();

            return new AiDecision(
                    false,
                    0.0,
                    "Learning baseline (" + samples + "/" + MIN_SAMPLES_FOR_ENFORCEMENT + ")",
                    false,
                    1.0
            );
        }

        /* =====================================================
         * 🔴 TEST MODE (FOR DEV)
         * ===================================================== */
        if (features.endpoint() != null && features.endpoint().contains("test-ai")) {
            return new AiDecision(
                    true,
                    0.95,
                    "Test mode trigger: forced AI anomaly",
                    true,
                    0.65
            );
        }

        /* =====================================================
         * 2️⃣ DEVIATION CALCULATION
         * ===================================================== */
        double rpmDeviation =
                deviation(
                        features.requestsPerMinute(),
                        baseline.getAvgRequestsPerMinute()
                );

        double endpointDeviation =
                deviation(
                        features.uniqueEndpoints(),
                        baseline.getAvgUniqueEndpoints()
                );

        /* =====================================================
         * 3️⃣ RAW SCORE
         * ===================================================== */
        double rawScore =
                (rpmDeviation * 0.7) +
                        (endpointDeviation * 0.3);

        rawScore = Math.min(1.0, rawScore);

        /* =====================================================
         * 4️⃣ ADAPTIVE THRESHOLD
         * ===================================================== */
        double threshold = adaptiveThreshold(baseline.getSamples());
        boolean anomaly = rawScore >= threshold;

        /* =====================================================
         * 5️⃣ CONFIDENCE DECAY (🔥 STEP 5)
         * ===================================================== */
        double finalScore;

        if (anomaly) {
            finalScore = rawScore; // no decay during anomaly
        } else {
            finalScore = rawScore * DECAY_FACTOR;
        }

        /* =====================================================
         * 6️⃣ EXPLANATION
         * ===================================================== */
        String explanation = anomaly
                ? String.format(
                "Deviation detected: RPM %.1f vs %.1f, endpoints %d vs %.1f",
                features.requestsPerMinute(),
                baseline.getAvgRequestsPerMinute(),
                features.uniqueEndpoints(),
                baseline.getAvgUniqueEndpoints()
        )
                : "Traffic normalized — confidence decaying";

        /* =====================================================
         * 7️⃣ CONTINUOUS LEARNING
         * ===================================================== */
        baselineService.updateBaseline(apiKey, features);

        return new AiDecision(
                anomaly,
                finalScore,
                explanation,
                true,
                threshold
        );
    }

    /* ===================== HELPERS ===================== */

    private double adaptiveThreshold(int samples) {
        if (samples < 50) return 0.90;
        if (samples < 100) return 0.80;
        return 0.70;
    }

    private double deviation(double current, double baseline) {
        return Math.abs(current - baseline) / (baseline + 1);
    }

    /* ===================== DECISION ===================== */

    public record AiDecision(
            boolean anomaly,
            double score,
            String explanation,
            boolean mature,
            double threshold
    ) {}
}
