package com.abhishek.apigovernance.ai;

public class AiPredictionResponse {

    private boolean anomaly;
    private double score;

    public boolean isAnomaly() {
        return anomaly;
    }

    public double getScore() {
        return score;
    }
}
