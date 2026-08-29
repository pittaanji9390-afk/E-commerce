package com.marketplace.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    @Builder.Default
    private boolean success = true;

    private T data;

    private String message;

    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> Result<T> ok(T data) {
        return Result.<T>builder()
                .success(true)
                .data(data)
                .message("Success")
                .timestamp(Instant.now())
                .build();
    }

    public static <T> Result<T> ok(T data, String message) {
        return Result.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> Result<T> fail(String message) {
        return Result.<T>builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}
