package com.example.eze.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.OffsetDateTime;
import java.util.List;

@Entity(tableName = "request_table")
public class Request {

    @PrimaryKey
    public String id;
    public String itemIds;
    public int itemCount;
    public OffsetDateTime createdDate;
    public String studentName;
    public String professorName;
    public String code;
    public String status;

    public Request(String id, String itemIds, int itemCount, OffsetDateTime createdDate, String studentName, String professorName, String code, String status) {
        this.id = id;
        this.itemIds = itemIds;
        this.itemCount = itemCount;
        this.createdDate = createdDate;
        this.studentName = studentName;
        this.professorName = professorName;
        this.code = code;
        this.status = status;
    }

    public String getItemIds() {
        return itemIds;
    }

    public void setItemIds(String itemIds) {
        this.itemIds = itemIds;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
