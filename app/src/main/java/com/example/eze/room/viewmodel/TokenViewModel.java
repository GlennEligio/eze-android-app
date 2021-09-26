package com.example.eze.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eze.model.Token;
import com.example.eze.room.repository.TokenRepository;

import java.util.List;

public class TokenViewModel extends AndroidViewModel {
    private TokenRepository tokenRepository;
    private LiveData<Token> latestToken;
    private LiveData<List<Token>> allTokens;

    public TokenViewModel(@NonNull Application application) {
        super(application);
        tokenRepository = new TokenRepository(application);
        latestToken = tokenRepository.getLatestToken();
        allTokens = tokenRepository.getAllToken();
    }

    public void insert(Token token){
        tokenRepository.insert(token);
    }

    public void update(Token token){
        tokenRepository.update(token);
    }

    public void delete(Token token){
        tokenRepository.delete(token);
    }

    public LiveData<Token> getLatestToken(){
        return latestToken;
    }

    public LiveData<List<Token>> getAllTokens(){
        return allTokens;
    }
}
