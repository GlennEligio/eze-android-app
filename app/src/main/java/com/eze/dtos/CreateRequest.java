package com.eze.dtos;

import java.util.List;

public class CreateRequest {

    public List<String> itemIds;
    public String studentName;
    public String professorId;
    public String description;

    public CreateRequest(List<String> itemIds, String studentName, String professorId, String description) {
        this.itemIds = itemIds;
        this.studentName = studentName;
        this.professorId = professorId;
        this.description = description;
    }

    public List<String> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
