package com.fintech.aggregator.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyValidator apiKeyValidator;
    private final ApiKeyUsageTracker apiKeyUsageTracker;

    @Transactional
    public Optional<String> validateAndResolveClient(String rawApiKey) {
        return apiKeyValidator.validate(rawApiKey)
                .map(validated -> {
                    apiKeyUsageTracker.recordUsage(validated.id());
                    return validated.clientName();
                });
    }
}
