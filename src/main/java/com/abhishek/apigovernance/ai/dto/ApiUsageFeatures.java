package com.abhishek.apigovernance.ai.dto;

public record ApiUsageFeatures(
        String apiKey,
        String endpoint,
        int requestsPerMinute,
        int errorCount,
        int uniqueEndpoints,
        double avgRequestIntervalMs
) {}
