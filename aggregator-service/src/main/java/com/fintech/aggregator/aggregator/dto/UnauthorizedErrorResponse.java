package com.fintech.aggregator.aggregator.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UnauthorizedErrorResponse {
    String error;
    String message;
}
