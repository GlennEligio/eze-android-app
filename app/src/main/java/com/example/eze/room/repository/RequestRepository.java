package com.example.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.eze.R;
import com.example.eze.executor.AppExecutors;
import com.example.eze.model.Account;
import com.example.eze.model.Request;
import com.example.eze.room.dao.AccountDao;
import com.example.eze.room.dao.ItemDao;
import com.example.eze.room.dao.RequestDao;
import com.example.eze.room.database.EzeDatabase;

import java.util.List;

public class RequestRepository {
    private final RequestDao requestDao;
    private LiveData<List<Request>> requestLiveData;
    private LiveData<List<Request>> pendingRequestLiveData;
    private LiveData<List<Request>> finishedRequestLiveData;

    public RequestRepository(Application application) {
        EzeDatabase ezeDatabase = EzeDatabase.getInstance(application);
        requestDao = ezeDatabase.requestDao();
        requestLiveData = requestDao.getAllRequest();
        pendingRequestLiveData = requestDao.getPendingRequest();
        finishedRequestLiveData = requestDao.getFinishedRequest();
    }

    public void insert(Request request){
        AppExecutors.getInstance().getDiskIO().execute(new InsertRunnable(requestDao, request));
    }

    public void update(Request request){
        AppExecutors.getInstance().getDiskIO().execute(new UpdateRunnable(requestDao, request));
    }

    public void delete(Request request){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteRunnable(requestDao, request));
    }

    public void deleteAll(){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteAllRunnable(requestDao));
    }

    public LiveData<List<Request>> getAllRequest(){
        return requestLiveData;
    }

    public LiveData<List<Request>> getPendingRequest(){
        return pendingRequestLiveData;
    }

    public LiveData<List<Request>> getFinishedRequest(){
        return finishedRequestLiveData;
    }

    private static class InsertRunnable implements Runnable{

        private final RequestDao requestDao;
        private final Request request;

        public InsertRunnable(RequestDao requestDao, Request request) {
            this.requestDao = requestDao;
            this.request = request;
        }

        @Override
        public void run() {
            requestDao.insertRequest(request);
        }
    }

    private static class DeleteRunnable implements Runnable{

        private final RequestDao requestDao;
        private final Request request;

        public DeleteRunnable(RequestDao requestDao, Request request) {
            this.requestDao = requestDao;
            this.request = request;
        }

        @Override
        public void run() {
            requestDao.deleteRequest(request);
        }
    }

    private static class UpdateRunnable implements Runnable{

        private final RequestDao requestDao;
        private final Request request;

        public UpdateRunnable(RequestDao requestDao, Request request) {
            this.requestDao = requestDao;
            this.request = request;
        }

        @Override
        public void run() {
            requestDao.updateRequest(request);
        }
    }

    private static class DeleteAllRunnable implements Runnable{

        private final RequestDao requestDao;

        public DeleteAllRunnable(RequestDao requestDao) {
            this.requestDao = requestDao;
        }

        @Override
        public void run() {
            requestDao.deleteAllRequest();
        }
    }
}
