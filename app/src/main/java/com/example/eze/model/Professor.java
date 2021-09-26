package com.example.eze.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "professor_table")
public class Professor {

    @PrimaryKey
    public String id;
    public String name;

    public Professor(String id, String name) {
        this.id = id;
        this.name = name;
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
}
