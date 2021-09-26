package com.example.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eze.model.Account;
import com.example.eze.model.Item;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Item item);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Query("SELECT * FROM item_table ORDER BY name ASC")
    LiveData<List<Item>> getAllItems();

    @Query("DELETE FROM item_table")
    void deleteAllItems();
}
