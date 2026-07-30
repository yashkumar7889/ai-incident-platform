package com.aiplatform.sentinel.common;

import java.time.LocalDateTime;

public final class ApiResponseBuilder {

    private ApiResponseBuilder() {
    }

    public static <T> ApiResponse<T> success(
            String message,
            T data) {

        return new ApiResponse<>(
                true,
                message,
                data,
                LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failure(
            String message) {

        return new ApiResponse<>(
                false,
                message,
                null,
                LocalDateTime.now());
    }
}