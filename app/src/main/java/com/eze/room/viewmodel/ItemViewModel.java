package com.eze.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.eze.model.Item;
import com.eze.room.repository.ItemRepository;

import java.util.List;

public class ItemViewModel extends AndroidViewModel {
    private ItemRepository itemRepository;
    private LiveData<List<Item>> allItems;

    public ItemViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        allItems = itemRepository.getAllItem();
    }

    public void insert(Item item){
        itemRepository.insert(item);
    }

    public void update(Item item){
        itemRepository.update(item);
    }

    public void delete(Item item){
        itemRepository.delete(item);
    }

    public void deleteAllItem(){
        itemRepository.deleteAll();
    }

    public Item getItem(String itemId){
        return itemRepository.getItem(itemId);
    }
}
