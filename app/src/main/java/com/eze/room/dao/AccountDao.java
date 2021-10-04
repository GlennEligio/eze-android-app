package com.eze.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.eze.model.Account;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAccount(Account account);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAccount(Account account);

    @Delete
    void deleteAccount(Account account);

    @Query("DELETE FROM account_table")
    void deleteAllAccount();

    @Query("SELECT * FROM account_table ORDER BY name ASC")
    LiveData<List<Account>> getAllAccount();

    @Query("SELECT * FROM account_table ORDER BY localId DESC LIMIT 1")
    Account getLatestAccount();
}
