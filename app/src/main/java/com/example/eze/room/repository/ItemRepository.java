package com.example.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eze.executor.AppExecutors;
import com.example.eze.model.Item;
import com.example.eze.room.dao.ItemDao;
import com.example.eze.room.database.EzeDatabase;

import java.util.List;

public class ItemRepository {

    private final ItemDao itemDao;
    private LiveData<List<Item>> itemLiveData;

    public ItemRepository(Application application) {
        EzeDatabase ezeDatabase = EzeDatabase.getInstance(application);
        itemDao = ezeDatabase.itemDao();
        itemLiveData = itemDao.getAllItems();
    }

    public void insert(Item item){
        AppExecutors.getInstance().getDiskIO().execute(new InsertRunnable(itemDao, item));
    }

    public void update(Item item){
        AppExecutors.getInstance().getDiskIO().execute(new UpdateRunnable(itemDao, item));
    }

    public void delete(Item item){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteRunnable(itemDao, item));
    }

    public void deleteAll(){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteAllRunnable(itemDao));
    }

    public LiveData<List<Item>> getAllItem(){
        return itemLiveData;
    }

    private static class InsertRunnable implements Runnable {
        private final ItemDao itemDao;
        private final Item item;

        public InsertRunnable(ItemDao itemDao, Item item) {
            this.itemDao = itemDao;
            this.item = item;
        }

        @Override
        public void run() {
            itemDao.insertItem(item);
        }
    }

    private static class UpdateRunnable implements Runnable {
        private final ItemDao itemDao;
        private final Item item;

        public UpdateRunnable(ItemDao itemDao, Item item) {
            this.itemDao = itemDao;
            this.item = item;
        }

        @Override
        public void run() {
            itemDao.updateItem(item);
        }
    }

    private static class DeleteRunnable implements Runnable {
        private final ItemDao itemDao;
        private final Item item;

        public DeleteRunnable(ItemDao itemDao, Item item) {
            this.itemDao = itemDao;
            this.item = item;
        }

        @Override
        public void run() {
            itemDao.deleteItem(item);
        }
    }

    private static class DeleteAllRunnable implements Runnable {
        private final ItemDao itemDao;

        public DeleteAllRunnable(ItemDao itemDao) {
            this.itemDao = itemDao;
        }

        @Override
        public void run() {
            itemDao.deleteAllItems();
        }
    }
}
