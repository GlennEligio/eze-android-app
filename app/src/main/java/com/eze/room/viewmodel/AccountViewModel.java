package com.eze.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.eze.model.Account;
import com.eze.room.repository.AccountRepository;

public class AccountViewModel extends AndroidViewModel {

    private AccountRepository accountRepository;
    private Account latestAccount;

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

    public Account getLatestAccount(){
        return latestAccount;
    }
}
