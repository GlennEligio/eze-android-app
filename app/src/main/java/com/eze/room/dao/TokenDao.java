package com.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.eze.model.Token;

import java.util.List;

@Dao
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

    @Query("SELECT * FROM token_table ORDER BY id DESC LIMIT 1")
    LiveData<Token> getLatestTokens();
}

