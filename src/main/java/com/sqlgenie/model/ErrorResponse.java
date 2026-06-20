package com.sqlgenie.model;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String message;
    private String detail;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String detail, int status, LocalDateTime timestamp) {
        this.message = message;
        this.detail = detail;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
