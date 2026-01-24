package com.abhishek.apigovernance.service;

import com.abhishek.apigovernance.domain.*;
import com.abhishek.apigovernance.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repository;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public List<ApiKey> findAll() {
        return repository.findAll();
    }

    public ApiKey create(ApiPlan plan) {
        ApiKey key = new ApiKey();
        key.setKeyValue(UUID.randomUUID().toString());
        key.setPlan(plan);
        key.setActive(true);
        key.setCreatedAt(LocalDateTime.now());
        return repository.save(key);
    }

    public void revoke(Long id) {
        ApiKey key = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key not found"));
        key.setActive(false);
        repository.save(key);
    }
}
