package com.fintech.aggregator.aggregator.controller;

import com.fintech.aggregator.aggregator.service.ApiKeyManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/api-keys")
@RequiredArgsConstructor
@Tag(name = "API Key Admin", description = "Operational endpoints for API key cache management")
public class ApiKeyAdminController {

    private final ApiKeyManagementService apiKeyManagementService;

    @PostMapping("/cache/evict")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Evict the active API key cache")
    public void evictCache() {
        apiKeyManagementService.refreshCache();
    }
}
