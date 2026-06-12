package com.fintech.aggregator.aggregator.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ErrorResponse {
    Instant timestamp;
    int status;
    String message;
    String path;
}
