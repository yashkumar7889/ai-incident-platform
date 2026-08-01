package com.aiplatform.sentinel.common;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
    }

    public ApiResponse(
            boolean success,
            String message,
            T data,
            LocalDateTime timestamp) {

        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                message,
                data,
                LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(
                false,
                message,
                null,
                LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(
            String message,
            T data) {

        return new ApiResponse<>(
                false,
                message,
                data,
                LocalDateTime.now());
    }
}