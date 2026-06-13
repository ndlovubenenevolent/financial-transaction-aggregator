package com.fintech.aggregator.aggregator.exception;

import com.fintech.aggregator.aggregator.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void shouldHandleNotFound() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new ResourceNotFoundException("Not found"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Not found");
    }

    @Test
    void shouldHandleBadRequest() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        ResponseEntity<ErrorResponse> response = handler.handleBadRequest(
                new IllegalArgumentException("Invalid"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleConstraintViolation() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        ConstraintViolationException ex = new ConstraintViolationException(java.util.Set.<ConstraintViolation<?>>of());
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleAccessDenied() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(
                new AccessDeniedException("Forbidden"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldHandleInvalidDataAccessApiUsage() {
        when(request.getRequestURI()).thenReturn("/api/v1/transactions");
        ResponseEntity<ErrorResponse> response = handler.handleInvalidQuery(
                new InvalidDataAccessApiUsageException("No property 'string' found for type 'Transaction'"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("string");
    }

    @Test
    void shouldHandleGeneralException() {
        when(request.getRequestURI()).thenReturn("/api/v1/test");
        ResponseEntity<ErrorResponse> response = handler.handleGeneral(new RuntimeException("boom"), request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
