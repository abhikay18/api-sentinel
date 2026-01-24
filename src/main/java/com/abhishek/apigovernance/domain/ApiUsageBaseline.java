package com.abhishek.apigovernance.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "api_usage_baseline")
@Getter @Setter
public class ApiUsageBaseline {

    @Id
    private String apiKeyValue;

    private double avgRequestsPerMinute;
    private double avgUniqueEndpoints;
    private int samples;
}

