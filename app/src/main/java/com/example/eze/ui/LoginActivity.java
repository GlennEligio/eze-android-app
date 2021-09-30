package com.example.eze.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eze.R;
import com.example.eze.dtos.AccountWithTokens;
import com.example.eze.dtos.LoginAccount;
import com.example.eze.model.Account;
import com.example.eze.retrofit.APIClient;
import com.example.eze.retrofit.UserClient;
import com.example.eze.room.viewmodel.AccountViewModel;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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
    private TextView txt_response;
    private Retrofit retrofit;

    private UserClient userClient;

    private AccountViewModel accountViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        userClient = APIClient.getClient().create(UserClient.class);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }

    public void init() {
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        loginLayout = findViewById(R.id.loginLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        txt_response = findViewById(R.id.txt_response);
    }

    //Login Button OnCLick method
    public void Login(View view) {
        Log.d(TAG, "Button clicked");
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();

        LoginAccount loginAccount = new LoginAccount(username, password);
        getLogin(loginAccount, this);
    }

    public void getLogin(LoginAccount loginAccount, Context context)
    {
        Log.d(TAG, "Sending Login credentials");
        Call<AccountWithTokens> call = userClient.getLogin(loginAccount);

        call.enqueue(new Callback<AccountWithTokens>() {
            @Override
            public void onResponse(Call<AccountWithTokens> call, Response<AccountWithTokens> response) {
                if(!response.isSuccessful())
                {
                    if(response.code() == 404){
                        Log.d(TAG, "No account found");
                        Toast.makeText(LoginActivity.this, "No account found in system", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Login error. Please try again later", Toast.LENGTH_SHORT).show();
                    return;
                }



                Log.d(TAG, "Putting account in sqlite");
                AccountWithTokens accountWithTokens = response.body();

                Account account = new Account(accountWithTokens.getId(),
                        accountWithTokens.getName(),
                        accountWithTokens.getUsername(),
                        accountWithTokens.getPassword(),
                        accountWithTokens.getRole(),
                        accountWithTokens.getAccessToken(),
                        accountWithTokens.getRefreshToken());

                String accountString = accountWithTokens.name + "\n" +
                        accountWithTokens.username + "\n" +
                        accountWithTokens.password + "\n" +
                        accountWithTokens.role + "\n" +
                        accountWithTokens.accessToken + "\n" +
                        accountWithTokens.refreshToken + "\n";

                txt_response.setText(accountString);

                accountViewModel.insert(account);

                Log.d(TAG, account.toString());

                Intent intent = new Intent(context, RequestActivity.class);
                intent.putExtra(RequestActivity.ACCOUNT_ID, account.getId());
                intent.putExtra(RequestActivity.ACCESS_TOKEN, account.getAccessToken());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AccountWithTokens> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}