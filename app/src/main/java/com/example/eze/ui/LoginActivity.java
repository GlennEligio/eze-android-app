package com.example.eze.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.eze.R;
import com.example.eze.dtos.AccountWithTokens;
import com.example.eze.dtos.LoginAccount;
import com.example.eze.retrofit.UserClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText edt_username;
    private EditText edt_password;
    private ConstraintLayout loginLayout, loadingLayout;

    private UserClient userClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://eze-service-glenneligio.cloud.okteto.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        userClient = retrofit.create(UserClient.class);
    }

    public void init() {
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        loginLayout = findViewById(R.id.loginLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
    }

    //Login Button OnCLick method
    public void Login(View view) {
        Log.d(TAG, "Button clicked");
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();

        LoginAccount loginAccount = new LoginAccount(username, password);
        getLogin(loginAccount);

    }

    public void getLogin(LoginAccount loginAccount)
    {
        Call<AccountWithTokens> call = userClient.getLogin(loginAccount);

        call.enqueue(new Callback<AccountWithTokens>() {
            @Override
            public void onResponse(Call<AccountWithTokens> call, Response<AccountWithTokens> response) {
                if(!response.isSuccessful())
                {
                    Log.d(TAG, "Code: " + response.code());
                    return;
                }

                AccountWithTokens accountWithTokens = response.body();
                String account = "Name: " + accountWithTokens.getName() + "\n" +
                        "Username: " + accountWithTokens.getUsername() + "\n" +
                        "Password: " + accountWithTokens.getPassword() + "\n" +
                        "AccessToken: " + accountWithTokens.getAccessToken() + "\n" +
                        "RefreshToken: " + accountWithTokens.getRefreshToken();

                Log.d(TAG, account);
            }

            @Override
            public void onFailure(Call<AccountWithTokens> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}