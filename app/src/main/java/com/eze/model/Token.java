package com.eze.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "token_table")
public class Token {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String refreshToken;

    public String accessToken;


    public Token(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
