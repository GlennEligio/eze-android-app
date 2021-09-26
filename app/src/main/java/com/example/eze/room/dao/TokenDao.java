package com.example.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eze.model.Account;
import com.example.eze.model.Token;

import java.util.List;

public interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertToken(Token token);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateToken(Token token);

    @Delete
    void deleteToken(Token token);

    @Query("DELETE FROM token_table")
    void deleteAllToken();

    @Query("SELECT * FROM token_table")
    LiveData<List<Token>> getAllToken();
}

