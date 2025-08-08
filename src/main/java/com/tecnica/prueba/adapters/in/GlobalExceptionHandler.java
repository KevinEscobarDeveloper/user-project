package com.tecnica.prueba.adapters.in;

import com.tecnica.prueba.application.exeptions.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Order(0)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException ex) {
        log.error("Custom error: {}", ex.getMessage());
        String message = switch (ex.getHttpCode()) {
            case 400 -> "Bad request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Resource not found";
            default -> "Unexpected error";
        };
        return ResponseEntity
                .status(ex.getHttpCode())
                .body(Map.of(
                        "message", message,
                        "code", ex.getHttpCode()
                ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(WebExchangeBindException ex) {
        // Armar mensaje con los campos que fallaron
        String fields = ex.getFieldErrors().stream()
                .map(error -> String.format("Campo '%s': %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        String message = fields.isEmpty() ? "Bad request" : fields;
        log.warn("Validation error: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "message", message,
                        "code", HttpStatus.BAD_REQUEST.value()
                ));
    }

    @ExceptionHandler({
            ExpiredJwtException.class,
            MalformedJwtException.class,
            SignatureException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, Object>> handleJwtException(Exception ex) {
        log.warn("JWT error: {}", ex.getClass().getSimpleName());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "message", "Invalid or expired token",
                        "code", HttpStatus.UNAUTHORIZED.value()
                ));
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        String message = switch (status) {
            case 400 -> "Bad request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Resource not found";
            default -> "Unexpected error";
        };
        log.warn("{}: {}", message, ex.getReason());
        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "message", message,
                        "code", status
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "Internal server error",
                        "code", HttpStatus.INTERNAL_SERVER_ERROR.value()
                ));
    }
}
