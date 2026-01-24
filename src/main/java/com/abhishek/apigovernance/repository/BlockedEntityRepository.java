package com.abhishek.apigovernance.repository;

import com.abhishek.apigovernance.domain.BlockedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedEntityRepository
        extends JpaRepository<BlockedEntity, Long> {

    Optional<BlockedEntity> findByValue(String value);
}
