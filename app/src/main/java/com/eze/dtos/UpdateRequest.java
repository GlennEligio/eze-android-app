package com.eze.dtos;

public class UpdateRequest {
    public String status;

    public UpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
