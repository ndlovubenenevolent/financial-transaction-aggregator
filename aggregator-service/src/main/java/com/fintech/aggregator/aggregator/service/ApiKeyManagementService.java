package com.fintech.aggregator.aggregator.service;

import com.fintech.aggregator.aggregator.entity.ApiKey;
import com.fintech.aggregator.aggregator.exception.ResourceNotFoundException;
import com.fintech.aggregator.aggregator.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyManagementService {

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActiveApiKeyCacheEvictor cacheEvictor;

    @Transactional
    public ApiKey createApiKey(String clientName, String plainKey) {
        ApiKey apiKey = ApiKey.builder()
                .clientName(clientName)
                .apiKeyHash(passwordEncoder.encode(plainKey))
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        ApiKey saved = apiKeyRepository.save(apiKey);
        cacheEvictor.evictAll();
        log.info("Created API key id={} clientName={}", saved.getId(), saved.getClientName());
        return saved;
    }

    @Transactional
    public void deactivateApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found: " + id));
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
        cacheEvictor.evictAll();
        log.info("Deactivated API key id={} clientName={}", id, apiKey.getClientName());
    }

    public void refreshCache() {
        cacheEvictor.evictAll();
        log.info("Evicted active API key cache");
    }
}
