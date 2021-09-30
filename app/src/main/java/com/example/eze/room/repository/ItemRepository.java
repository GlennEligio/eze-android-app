package com.example.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eze.executor.AppExecutors;
import com.example.eze.model.Item;
import com.example.eze.room.database.EzeDatabase;
import com.example.eze.room.dao.ItemDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    public Item getItem(String itemId){
        GetItemCallable getItem = new GetItemCallable(itemId, itemDao);
        Future<Item> future = AppExecutors.getInstance().getDiskIO().submit(getItem);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
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

    private static class GetItemCallable implements Callable<Item> {

        private final String itemId;
        private final ItemDao itemDao;

        public GetItemCallable(String itemId, ItemDao itemDao) {
            this.itemId = itemId;
            this.itemDao = itemDao;
        }

        @Override
        public Item call() throws Exception {
            return itemDao.getItem(itemId);
        }
    }
}
