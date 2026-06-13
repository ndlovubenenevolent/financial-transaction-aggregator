package com.fintech.aggregator.aggregator.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class ActiveApiKeyCacheEvictor {

    @CacheEvict(cacheNames = "activeApiKeys", allEntries = true)
    public void evictAll() {
        // Eviction handled by Spring Cache AOP
    }
}
