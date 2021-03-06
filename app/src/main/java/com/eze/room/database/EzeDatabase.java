package com.eze.room.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.eze.model.Account;
import com.eze.model.Item;
import com.eze.model.Professor;
import com.eze.model.Request;
import com.eze.model.Token;
import com.eze.room.dao.AccountDao;
import com.eze.room.dao.ItemDao;
import com.eze.room.dao.ProfessorDao;
import com.eze.room.dao.RequestDao;
import com.eze.room.dao.TokenDao;

@Database(entities = {Account.class, Item.class, Professor.class, Request.class, Token.class}, version = 4, exportSchema = false)
public abstract class EzeDatabase extends RoomDatabase {
    private static final String TAG = "EzeDatabase";
    private static EzeDatabase instance;

    public abstract AccountDao accountDao();
    public abstract ItemDao itemDao();
    public abstract ProfessorDao professorDao();
    public abstract RequestDao requestDao();
    public abstract TokenDao tokenDao();

    public static synchronized EzeDatabase getInstance(Context context){
        if(null == instance){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    EzeDatabase.class, "eze_database")
                    .addCallback(callback)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };
}
