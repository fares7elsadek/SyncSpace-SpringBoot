package com.fares7elsadek.syncspace.shared.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String error
) {
    public static <T> ApiResponse<T> success(String message,T data){
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String error,T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
    }
}
