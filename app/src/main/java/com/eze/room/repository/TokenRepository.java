package com.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.eze.executor.AppExecutors;
import com.eze.model.Token;
import com.eze.room.database.EzeDatabase;
import com.eze.room.dao.TokenDao;

import java.util.List;

public class TokenRepository {

    private final TokenDao tokenDao;
    private LiveData<List<Token>> tokenLiveData;
    private LiveData<Token> latestToken;

    public TokenRepository(Application application) {
        EzeDatabase ezeDatabase = EzeDatabase.getInstance(application);
        tokenDao = ezeDatabase.tokenDao();
        tokenLiveData = tokenDao.getAllToken();
        latestToken = tokenDao.getLatestTokens();
    }

    public void insert(Token token){
        AppExecutors.getInstance().getDiskIO().execute(new InsertRunnable(tokenDao, token));
    }

    public void update(Token token){
        AppExecutors.getInstance().getDiskIO().execute(new UpdateRunnable(tokenDao, token));
    }

    public void delete(Token token){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteRunnable(tokenDao, token));
    }

    public void deleteAll(){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteAllRunnable(tokenDao));
    }

    public LiveData<List<Token>> getAllToken(){
        return tokenLiveData;
    }

    public LiveData<Token> getLatestToken(){
        return latestToken;
    }

    private static class InsertRunnable implements Runnable{

        private final TokenDao tokenDao;
        private final Token token;

        public InsertRunnable(TokenDao tokenDao, Token token) {
            this.tokenDao = tokenDao;
            this.token = token;
        }

        @Override
        public void run() {
            tokenDao.insertToken(token);
        }
    }

    private static class UpdateRunnable implements Runnable{

        private final TokenDao tokenDao;
        private final Token token;

        public UpdateRunnable(TokenDao tokenDao, Token token) {
            this.tokenDao = tokenDao;
            this.token = token;
        }

        @Override
        public void run() {
            tokenDao.updateToken(token);
        }
    }

    private static class DeleteRunnable implements Runnable{

        private final TokenDao tokenDao;
        private final Token token;

        public DeleteRunnable(TokenDao tokenDao, Token token) {
            this.tokenDao = tokenDao;
            this.token = token;
        }

        @Override
        public void run() {
            tokenDao.deleteToken(token);
        }
    }

    private static class DeleteAllRunnable implements Runnable{

        private final TokenDao tokenDao;

        public DeleteAllRunnable(TokenDao tokenDao) {
            this.tokenDao = tokenDao;
        }

        @Override
        public void run() {
            tokenDao.deleteAllToken();
        }
    }

}
