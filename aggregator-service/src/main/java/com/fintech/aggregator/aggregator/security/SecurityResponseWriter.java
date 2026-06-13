package com.fintech.aggregator.aggregator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.aggregator.aggregator.dto.UnauthorizedErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Writes JSON security error responses (Single Responsibility).
 */
@Component
@RequiredArgsConstructor
public class SecurityResponseWriter {

    public static final String UNAUTHORIZED_ERROR = "UNAUTHORIZED";
    public static final String FORBIDDEN_ERROR = "FORBIDDEN";
    public static final String RATE_LIMIT_ERROR = "TOO_MANY_REQUESTS";
    public static final String INVALID_API_KEY_MESSAGE = "Invalid API Key";
    public static final String ACCESS_DENIED_MESSAGE = "Access denied";
    public static final String RATE_LIMIT_MESSAGE = "Rate limit exceeded";

    private final ObjectMapper objectMapper;

    public void writeUnauthorized(HttpServletResponse response) throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_ERROR, INVALID_API_KEY_MESSAGE);
    }

    public void writeForbidden(HttpServletResponse response) throws IOException {
        write(response, HttpStatus.FORBIDDEN, FORBIDDEN_ERROR, ACCESS_DENIED_MESSAGE);
    }

    public void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setHeader("Retry-After", "60");
        write(response, HttpStatus.TOO_MANY_REQUESTS, RATE_LIMIT_ERROR, RATE_LIMIT_MESSAGE);
    }

    private void write(HttpServletResponse response, HttpStatus status, String error, String message)
            throws IOException {
        UnauthorizedErrorResponse body = UnauthorizedErrorResponse.builder()
                .error(error)
                .message(message)
                .build();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
