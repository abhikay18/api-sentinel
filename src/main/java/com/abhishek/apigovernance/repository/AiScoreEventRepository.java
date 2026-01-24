package com.abhishek.apigovernance.repository;

import com.abhishek.apigovernance.domain.AiScoreEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiScoreEventRepository
        extends JpaRepository<AiScoreEvent, Long> {

    List<AiScoreEvent> findTop50ByApiKeyValueOrderByCreatedAtDesc(String apiKeyValue);
}
