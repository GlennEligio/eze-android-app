package com.example.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eze.model.Account;
import com.example.eze.model.Request;

import java.util.List;

@Dao
public interface RequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRequest(Request request);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRequest(Request request);

    @Delete
    void deleteRequest(Request request);

    @Query("DELETE FROM request_table")
    void deleteAllRequest();

    @Query("SELECT * FROM request_table")
    LiveData<List<Request>> getAllRequest();
}
