package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApiKeyUsageTracker {

    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public void recordUsage(Long apiKeyId) {
        apiKeyRepository.updateLastUsedAt(apiKeyId, LocalDateTime.now());
    }
}
