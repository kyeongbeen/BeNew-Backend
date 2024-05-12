package com.example.springsecurityexample.member.dto;

public class CheckResponse {
    private boolean isDuplicate;
    private String message;

    public CheckResponse(boolean isDuplicate, String message) {
        this.isDuplicate = isDuplicate;
        this.message = message;
    }

    // Getters and Setters
    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
