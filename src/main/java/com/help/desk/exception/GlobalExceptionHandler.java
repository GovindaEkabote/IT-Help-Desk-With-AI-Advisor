package com.help.desk.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("NOT_FOUND")
                .message(ex.getMessage())
                .build();

        logger.error("Resource not found: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError>  handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ){
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NO_CONTENT.value())
                .error("BAD_REQUEST")
                .message(ex.getMessage())
                .build();
        logger.error("Bad Request" + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Validation failed")
                .details(errors)
                .path(request.getRequestURI())
                .build();
        logger.error("Validation failed: " + errors);
        return ResponseEntity.badRequest()
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public  ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ){
        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("Something went wrong")
                .path(request.getRequestURI())
                .build();
        logger.error("Something went wrong: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request
    ) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("FORBIDDEN")
                .message("Access denied. Only ADMIN users can access this resource.")
                .path(request.getRequestURI())
                .build();


        logger.error("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("UNAUTHORIZED")
                .message("Please login to access this resource")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public  ResponseEntity<ErrorResponse> handleTooManyRequests(
            TooManyRequestsException ex
    ){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                ex.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(errorResponse);

    }



}
