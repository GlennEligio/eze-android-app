package com.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.eze.model.Request;

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

    @Query("SELECT * FROM request_table WHERE status LIKE 'Pending' ORDER BY createdDate DESC")
    LiveData<List<Request>> getPendingRequest();

    @Query("SELECT * FROM request_table WHERE status LIKE 'Accepted' OR status LIKE 'Rejected' ORDER BY createdDate DESC")
    LiveData<List<Request>> getFinishedRequest();

    @Query("SELECT * FROM request_table WHERE id LIKE :requestId")
    Request getRequest(String requestId);

    @Query("SELECT * FROM request_table WHERE status LIKE 'Pending' ORDER BY createdDate DESC")
    List<Request> getAllPendingRequestForMassUpdate();
}
