package com.disease.predictor.dto;

import java.util.Objects;

public class ErrorResponse {

    private String Status;

    private String message;

    public ErrorResponse(String status, String message) {
        Status = status;
        this.message = message;
    }

    public ErrorResponse() {
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
