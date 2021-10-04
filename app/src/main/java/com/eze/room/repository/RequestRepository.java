package com.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.eze.executor.AppExecutors;
import com.eze.model.Request;
import com.eze.room.database.EzeDatabase;
import com.eze.room.dao.RequestDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    public Request getRequest(String requestId){
        Future<Request> future = AppExecutors.getInstance().getDiskIO().submit(new GetRequestCallable(requestId, requestDao));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Request> getAllPendingRequestForMassUpdate(){
        Future<List<Request>> future = AppExecutors.getInstance().getDiskIO().submit(new GetAllRequestCallable(requestDao));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
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

    private static class GetRequestCallable implements Callable<Request> {

        private final String requestId;
        private final RequestDao requestDao;

        public GetRequestCallable(String requestId, RequestDao requestDao) {
            this.requestId = requestId;
            this.requestDao = requestDao;
        }

        @Override
        public Request call() throws Exception {
            return requestDao.getRequest(requestId);
        }
    }

    private static class GetAllRequestCallable implements Callable<List<Request>> {

        private final RequestDao requestDao;

        public GetAllRequestCallable(RequestDao requestDao) {
            this.requestDao = requestDao;
        }

        @Override
        public List<Request> call() throws Exception {
            return requestDao.getAllPendingRequestForMassUpdate();
        }
    }
}
