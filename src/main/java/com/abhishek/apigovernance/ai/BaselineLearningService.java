package com.abhishek.apigovernance.ai;

import com.abhishek.apigovernance.ai.dto.ApiUsageFeatures;
import com.abhishek.apigovernance.domain.ApiUsageBaseline;
import com.abhishek.apigovernance.repository.ApiUsageBaselineRepository;
import org.springframework.stereotype.Service;

@Service
public class BaselineLearningService {

    private final ApiUsageBaselineRepository repository;

    public BaselineLearningService(ApiUsageBaselineRepository repository) {
        this.repository = repository;
    }

    /* ===============================
       BASELINE UPDATE (LEARNING)
       =============================== */

    public void updateBaseline(String apiKey, ApiUsageFeatures features) {

        ApiUsageBaseline baseline =
                repository.findById(apiKey)
                        .orElseGet(() -> {
                            ApiUsageBaseline b = new ApiUsageBaseline();
                            b.setApiKeyValue(apiKey);
                            b.setSamples(0);
                            b.setAvgRequestsPerMinute(0);
                            b.setAvgUniqueEndpoints(0);
                            return b;
                        });

        int n = baseline.getSamples();

        baseline.setAvgRequestsPerMinute(
                rollingAvg(
                        baseline.getAvgRequestsPerMinute(),
                        features.requestsPerMinute(),
                        n
                )
        );

        baseline.setAvgUniqueEndpoints(
                rollingAvg(
                        baseline.getAvgUniqueEndpoints(),
                        features.uniqueEndpoints(),
                        n
                )
        );

        baseline.setSamples(n + 1);

        repository.save(baseline);
    }

    /* ===============================
       BASELINE RESET (ADMIN)
       =============================== */

    public void resetBaseline(String apiKey) {
        repository.deleteById(apiKey);
    }

    /* ===============================
       UTIL
       =============================== */

    private double rollingAvg(double oldAvg, double newValue, int n) {
        return (oldAvg * n + newValue) / (n + 1);
    }
}
