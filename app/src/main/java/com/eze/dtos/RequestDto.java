package com.eze.dtos;

import com.eze.model.Item;

import java.time.OffsetDateTime;
import java.util.List;

public class RequestDto {

    public String id;
    public List<Item> items;
    public OffsetDateTime createdDate;
    public String studentName;
    public String professorName;
    public String code;
    public String status;

    public RequestDto(String id, List<Item> items, OffsetDateTime createdDate, String studentName, String professorName, String code, String status) {
        this.id = id;
        this.items = items;
        this.createdDate = createdDate;
        this.studentName = studentName;
        this.professorName = professorName;
        this.code = code;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
