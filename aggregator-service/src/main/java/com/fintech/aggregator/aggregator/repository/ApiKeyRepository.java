package com.fintech.aggregator.aggregator.repository;

import com.fintech.aggregator.aggregator.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    List<ApiKey> findAllByActiveTrue();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE ApiKey k SET k.lastUsedAt = :lastUsedAt WHERE k.id = :id")
    void updateLastUsedAt(@Param("id") Long id, @Param("lastUsedAt") LocalDateTime lastUsedAt);
}
