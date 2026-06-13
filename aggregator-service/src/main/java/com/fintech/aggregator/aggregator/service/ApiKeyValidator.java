package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.ApiKey;
import com.fintech.aggregator.aggregator.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyValidator {

    private final ActiveApiKeyLoader activeApiKeyLoader;
    private final PasswordEncoder passwordEncoder;

    public Optional<ValidatedApiKey> validate(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.isBlank()) {
            return Optional.empty();
        }

        for (ApiKey apiKey : activeApiKeyLoader.loadActiveApiKeys()) {
            if (passwordEncoder.matches(rawApiKey, apiKey.getApiKeyHash())) {
                return Optional.of(new ValidatedApiKey(apiKey.getId(), apiKey.getClientName()));
            }
        }
        return Optional.empty();
    }

    public record ValidatedApiKey(Long id, String clientName) {
    }
}
