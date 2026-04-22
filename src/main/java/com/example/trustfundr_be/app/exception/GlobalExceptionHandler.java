package com.example.trustfundr_be.app.exception;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.example.trustfundr_be.exception.AccountDisabledException;
import com.example.trustfundr_be.exception.AuthException;
import com.example.trustfundr_be.exception.FundraisingActivityException;
import com.example.trustfundr_be.exception.UserAccountException;
import com.example.trustfundr_be.exception.UserProfileException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Unauthorized";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", message));
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<Map<String, String>> handleAccountDisabled(AccountDisabledException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Account disabled";
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", message));
    }

    @ExceptionHandler(UserProfileException.class)
    public ResponseEntity<Map<String, String>> handleUserProfileException(UserProfileException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Request failed";
        return ResponseEntity.status(ex.getStatus()).body(Map.of("message", message));
    }

    @ExceptionHandler(UserAccountException.class)
    public ResponseEntity<Map<String, String>> handleUserAccountException(UserAccountException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Request failed";
        return ResponseEntity.status(ex.getStatus()).body(Map.of("message", message));
    }

    @ExceptionHandler(FundraisingActivityException.class)
    public ResponseEntity<Map<String, String>> handleFundraisingActivityException(FundraisingActivityException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Request failed";
        return ResponseEntity.status(ex.getStatus()).body(Map.of("message", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        String message = ex.getParameterValidationResults().stream()
                .flatMap(p -> p.getResolvableErrors().stream())
                .map(err -> {
                    if (err instanceof FieldError fe) {
                        return fe.getDefaultMessage();
                    }
                    if (err instanceof ObjectError oe) {
                        return oe.getDefaultMessage();
                    }
                    return err.toString();
                })
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("; "));
        if (message.isBlank()) {
            message = ex.getReason() != null ? ex.getReason() : "Validation failed";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }
}
