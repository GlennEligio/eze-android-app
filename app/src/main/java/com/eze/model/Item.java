package com.eze.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.eze.room.typeconverter.OffSetDateTimeConverter;

import java.time.OffsetDateTime;

@Entity(tableName = "item_table")
@TypeConverters(OffSetDateTimeConverter.class)
public class Item {

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String description;
    public String condition;
    public OffsetDateTime createdDate;

    public Item(@NonNull String id, String name, String description, String condition, OffsetDateTime createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.condition = condition;
        this.createdDate = createdDate;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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
