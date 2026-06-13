package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.ApiKey;
import com.fintech.aggregator.aggregator.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActiveApiKeyLoader {

    private final ApiKeyRepository apiKeyRepository;

    @Cacheable("activeApiKeys")
    public List<ApiKey> loadActiveApiKeys() {
        return apiKeyRepository.findAllByActiveTrue();
    }
}
