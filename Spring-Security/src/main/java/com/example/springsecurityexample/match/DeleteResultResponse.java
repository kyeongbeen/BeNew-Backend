package com.example.springsecurityexample.match;

import lombok.Getter;

@Getter
public class DeleteResultResponse {
    private boolean isSuccess;
    private String message;

    public DeleteResultResponse(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
