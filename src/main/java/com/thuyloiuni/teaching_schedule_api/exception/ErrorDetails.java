package com.thuyloiuni.teaching_schedule_api.exception;

import java.util.Date;
import java.util.Map;

public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
    private Map<String, String> validationErrors; // Cho lỗi validation chi tiết


    // Constructor cho lỗi chung
    public ErrorDetails(Date timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    // Constructor cho lỗi validation
    public ErrorDetails(Date timestamp, String message, String details, Map<String, String> validationErrors) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details; // Có thể là "Validation Failed"
        this.validationErrors = validationErrors;
    }

    // Getters
    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}

