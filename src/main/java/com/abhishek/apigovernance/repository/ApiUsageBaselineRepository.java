package com.abhishek.apigovernance.repository;

import com.abhishek.apigovernance.domain.ApiUsageBaseline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiUsageBaselineRepository
        extends JpaRepository<ApiUsageBaseline, String> {
}
