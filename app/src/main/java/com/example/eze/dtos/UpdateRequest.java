package com.example.eze.dtos;

public class UpdateRequest {
    public boolean status;

    public UpdateRequest(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
