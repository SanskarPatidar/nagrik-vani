package com.sih.AuthService.exception;

import com.sanskar.common.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(NotFoundException e, HttpServletRequest request) {
        log.error("NotFoundException handled with message: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI()) // only path, not full URL
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException e, HttpServletRequest request) {
        log.error("ResourceConflictException handled with message: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.error("BadCredentialsException handled with message: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(e.getMessage()) // generic message
                .path(request.getRequestURI())
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(DownstreamBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleDownstreamBadRequestException(DownstreamBadRequestException e, HttpServletRequest request) {
        log.error("DownstreamBadRequestException handled with message: {}", e.getMessage()); // log actual message
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(e.getErrorResponse().getTimestamp())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("The request could not be processed due to invalid data") // send generic message to client
                .path(request.getRequestURI()) // only path, not full URL
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(DownstreamServerException.class)
    public ResponseEntity<ErrorResponse> handleDownstreamBadRequestException(DownstreamServerException e, HttpServletRequest request) {
        log.error("DownstreamServerException handled with message: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(e.getErrorResponse().getTimestamp())
                .status(HttpStatus.BAD_GATEWAY.value())
                .error(HttpStatus.BAD_GATEWAY.getReasonPhrase())
                .message("A downstream service encountered an error, please try again later")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(DownstreamServerConnectionException.class)
    public ResponseEntity<ErrorResponse> handleDownstreamBadRequestException(DownstreamServerConnectionException e, HttpServletRequest request) {
        log.error("DownstreamServerConnectionException handled with message: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI()) // only path, not full URL
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("UncaughtException handled with message: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected internal error occurred, please contact support")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

}
