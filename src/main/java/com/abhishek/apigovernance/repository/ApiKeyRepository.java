package com.abhishek.apigovernance.repository;

import com.abhishek.apigovernance.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyValue(String keyValue);
}
