package com.eze.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.eze.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "Login activity created");


        if(ProfessorMainActivity.receiver != null){
            unregisterReceiver(ProfessorMainActivity.receiver);
        }
    }
}