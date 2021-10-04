package com.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.eze.model.Professor;

import java.util.List;

@Dao
public interface ProfessorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProfessor(Professor professor);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateProfessor(Professor professor);

    @Delete
    void deleteProfessor(Professor professor);

    @Query("DELETE FROM professor_table")
    void deleteAllProfessor();

    @Query("SELECT * FROM professor_table ORDER BY name")
    LiveData<List<Professor>> getAllProfessor();
}
