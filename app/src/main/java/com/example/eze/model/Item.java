package com.example.eze.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.OffsetDateTime;

@Entity(tableName = "item_table")
public class Item {

    @PrimaryKey
    public String id;
    public String name;
    public String description;
    public String condition;
    public OffsetDateTime createdDate;

    public Item(String id, String name, String description, String condition, OffsetDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.condition = condition;
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
