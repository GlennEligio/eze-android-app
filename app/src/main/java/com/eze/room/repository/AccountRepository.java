package com.eze.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.eze.executor.AppExecutors;
import com.eze.model.Account;
import com.eze.room.database.EzeDatabase;
import com.eze.room.dao.AccountDao;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AccountRepository {

    private final AccountDao accountDao;
    private LiveData<List<Account>> accountLiveData;

    public AccountRepository(Application application) {
        EzeDatabase ezeDatabase = EzeDatabase.getInstance(application);
        accountDao = ezeDatabase.accountDao();
        accountLiveData = accountDao.getAllAccount();
    }

    public void insert(Account account){
        AppExecutors.getInstance().getDiskIO().execute(new InsertRunnable(accountDao, account));
    }

    public void update(Account account){
        AppExecutors.getInstance().getDiskIO().execute(new UpdateRunnable(accountDao, account));
    }

    public void delete(Account account){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteRunnable(accountDao, account));
    }

    public void deleteAll(){
        AppExecutors.getInstance().getDiskIO().execute(new DeleteAllRunnable(accountDao));
    }

    public LiveData<List<Account>> getAllAccount(){
        return accountLiveData;
    }

    public Account getLatestAccount(){
        Future<Account> future = AppExecutors.getInstance().getDiskIO().submit(new GetAccountCallable(accountDao));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class InsertRunnable implements Runnable {
        private final AccountDao accountDao;
        private final Account account;

        public InsertRunnable(AccountDao accountDao, Account account) {
            this.accountDao = accountDao;
            this.account = account;
        }

        @Override
        public void run() {
            accountDao.insertAccount(account);
        }
    }

    private static class UpdateRunnable implements Runnable {
        private final AccountDao accountDao;
        private final Account account;

        public UpdateRunnable(AccountDao accountDao, Account account) {
            this.accountDao = accountDao;
            this.account = account;
        }

        @Override
        public void run() {
            accountDao.updateAccount(account);
        }
    }

    private static class DeleteRunnable implements Runnable {
        private final AccountDao accountDao;
        private final Account account;

        public DeleteRunnable(AccountDao accountDao, Account account) {
            this.accountDao = accountDao;
            this.account = account;
        }

        @Override
        public void run() {
            accountDao.deleteAccount(account);
        }
    }

    private static class DeleteAllRunnable implements Runnable {
        private final AccountDao accountDao;

        public DeleteAllRunnable(AccountDao accountDao) {
            this.accountDao = accountDao;
        }

        @Override
        public void run() {
            accountDao.deleteAllAccount();
        }
    }

    private static class GetAccountCallable implements Callable<Account> {

        private final AccountDao accountDao;

        public GetAccountCallable(AccountDao accountDao) {
            this.accountDao = accountDao;
        }

        @Override
        public Account call() throws Exception {
            return accountDao.getLatestAccount();
        }
    }
}
