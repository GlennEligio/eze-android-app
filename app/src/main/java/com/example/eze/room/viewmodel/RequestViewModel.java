package com.example.eze.room.viewmodel;


import android.app.Application;

import com.example.eze.model.Item;
import com.example.eze.model.Request;
import com.example.eze.room.repository.ItemRepository;
import com.example.eze.room.repository.RequestRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class RequestViewModel extends AndroidViewModel {

    private ItemRepository itemRepository;

    private RequestRepository requestRepository;
    private LiveData<List<Request>> allRequests;
    private LiveData<List<Request>> allPendingRequest;
    private LiveData<List<Request>> allFinishedRequest;


    public RequestViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        requestRepository = new RequestRepository(application);

        allRequests = requestRepository.getAllRequest();
        allPendingRequest = requestRepository.getPendingRequest();
        allFinishedRequest = requestRepository.getFinishedRequest();
    }

    public void insertItem(Item item){
        itemRepository.insert(item);
    }

    public void updateItem(Item item){
        itemRepository.update(item);
    }

    public void deleteItem(Item item){
        itemRepository.delete(item);
    }

    public void deleteAllItem(Item item){
        itemRepository.deleteAll();
    }

    public Item getItem(String itemId){
        return itemRepository.getItem(itemId);
    }

    public void insertRequest(Request request){
        requestRepository.insert(request);
    }

    public void updateRequest(Request request){
        requestRepository.update(request);
    }

    public void deleteRequest(Request request){
        requestRepository.delete(request);
    }

    public void deleteAllRequest(){
        requestRepository.deleteAll();
    }

    public LiveData<List<Request>> getAllRequests(){
        return allRequests;
    }

    public LiveData<List<Request>> getAllPendingRequest(){
        return allPendingRequest;
    }

    public LiveData<List<Request>> getAllFinishedRequest(){
        return allFinishedRequest;
    }

}
