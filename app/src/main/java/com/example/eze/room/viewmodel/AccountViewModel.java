package com.example.eze.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eze.model.Account;
import com.example.eze.room.repository.AccountRepository;

public class AccountViewModel extends AndroidViewModel {

    private AccountRepository accountRepository;
    private LiveData<Account> latestAccount;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
        latestAccount = accountRepository.getLatestAccount();
    }

    public void insert(Account account){
        accountRepository.insert(account);
    }

    public void update(Account account){
        accountRepository.insert(account);
    }

    public void delete(Account account){
        accountRepository.delete(account);
    }

    public void deleteAllAccount(){
        accountRepository.deleteAll();
    }

    public void getAllAccount(){
        accountRepository.getAllAccount();
    }

    public LiveData<Account> getLatestAccount(){
        return latestAccount;
    }
}
