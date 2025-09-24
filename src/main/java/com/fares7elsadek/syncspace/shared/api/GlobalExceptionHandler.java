package com.fares7elsadek.syncspace.shared.api;

import com.fares7elsadek.syncspace.shared.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {
        String errorId = generateErrorId();
        log.warn("User not found - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(NotFoundException ex) {
        String errorId = generateErrorId();
        log.warn("Resource not found - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Requested resource not found", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
        String errorId = generateErrorId();
        log.warn("Unauthorized access - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        String errorId = generateErrorId();
        log.warn("Access denied - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access denied", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(InsufficientPermissionsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse<Object>> handleInsufficientPermissions(InsufficientPermissionsException ex) {
        String errorId = generateErrorId();
        log.warn("Insufficient permissions - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Insufficient permissions to perform this action", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
        String errorId = generateErrorId();
        log.warn("Conflict occurred - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Resource conflict occurred", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(FriendshipRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleFriendshipRequest(FriendshipRequestException ex) {
        String errorId = generateErrorId();
        log.warn("Friendship request failed - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Unable to process friendship request", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(SaveFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleSaveFile(SaveFileException ex) {
        String errorId = generateErrorId();
        log.warn("File save failed - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Unable to save file", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(RemoveFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleRemoveFile(RemoveFileException ex) {
        String errorId = generateErrorId();
        log.warn("File removal failed - Error ID: {} - Message: {}", errorId, ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Unable to remove file", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(ServerExceptions.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Object>> handleServerErrors(ServerExceptions ex) {
        String errorId = generateErrorId();
        log.error("Server error - Error ID: {} - Message: {}", errorId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error occurred", Map.of("errorId", errorId)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorId = generateErrorId();

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        log.warn("Validation failed - Error ID: {} - Field errors: {}", errorId, fieldErrors);

        Map<String, Object> errorData = Map.of(
                "errorId", errorId,
                "fieldErrors", fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errorData));
    }

    /**
     * Generic exception handler for all other exceptions
     * This ensures no sensitive information is leaked to clients
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        String errorId = generateErrorId();

        // Log full exception details for debugging (only visible in server logs)
        log.error("Unexpected error - Error ID: {} - Exception: {}", errorId, ex.getMessage(), ex);

        // Return generic error message to client without revealing system details
        String message = isProductionEnvironment() ?
                "An unexpected error occurred" :
                "An unexpected error occurred: " + ex.getClass().getSimpleName();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message, Map.of("errorId", errorId)));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Object>> handleRuntime(RuntimeException ex) {
        String errorId = generateErrorId();
        log.error("Runtime error - Error ID: {} - Message: {}", errorId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("A runtime error occurred", Map.of("errorId", errorId)));
    }


    /**
     * Generates a unique error ID for tracking purposes
     */
    private String generateErrorId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Checks if the application is running in production environment
     */
    private boolean isProductionEnvironment() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("prod".equals(profile) || "production".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}