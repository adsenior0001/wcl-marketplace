package com.wcl.shared;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> validationErrors,
        LocalDateTime timestamp
) {
    public ErrorResponse(int status, String message) {
        this(status, message, null, LocalDateTime.now());
    }
}