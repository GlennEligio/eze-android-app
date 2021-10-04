package com.eze.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.eze.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";


//    private EditText edt_username;
//    private EditText edt_password;
//    private ConstraintLayout loginLayout, loadingLayout;
//
//    private UserClient userClient;
//
//    private AccountViewModel accountViewModel;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "Login activity created");

//        init();

        if(ProfessorMainActivity.receiver != null){
            unregisterReceiver(ProfessorMainActivity.receiver);
        }

//        userClient = APIClient.getClient().create(UserClient.class);
//
//        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
    }


//    public void init() {
//        edt_username = findViewById(R.id.edt_username);
//        edt_password = findViewById(R.id.edt_password);
//        loginLayout = findViewById(R.id.loginLayout);
//    }

    //Login Button OnCLick method
//    public void Login(View view) {
//        Log.d(TAG, "Button clicked");
//        String username = edt_username.getText().toString();
//        String password = edt_password.getText().toString();
//
//        LoginAccount loginAccount = new LoginAccount(username, password);
//        getLogin(loginAccount, this);
//    }

//    public void getLogin(LoginAccount loginAccount, Context context)
//    {
//        Log.d(TAG, "Sending Login credentials");
//        Call<AccountWithTokens> call = userClient.getLogin(loginAccount);
//
//        call.enqueue(new Callback<AccountWithTokens>() {
//            @Override
//            public void onResponse(Call<AccountWithTokens> call, Response<AccountWithTokens> response) {
//                if(!response.isSuccessful())
//                {
//                    if(response.code() == 404){
//                        Log.d(TAG, "No account found");
//                        Toast.makeText(LoginActivity.this, "No account found in system", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    Log.d(TAG, "Code: " + response.code());
//                    Toast.makeText(LoginActivity.this, "Login error. Please try again later", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Log.d(TAG, "Putting account in sqlite");
//                AccountWithTokens accountWithTokens = response.body();
//
//                Account account = new Account(accountWithTokens.getId(),
//                        accountWithTokens.getName(),
//                        accountWithTokens.getUsername(),
//                        accountWithTokens.getPassword(),
//                        accountWithTokens.getRole(),
//                        accountWithTokens.getAccessToken(),
//                        accountWithTokens.getRefreshToken());
//
//                accountViewModel.insert(account);
//
//                Log.d(TAG, account.toString());
//
//                Intent intent = new Intent(context, ProfessorMainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onFailure(Call<AccountWithTokens> call, Throwable t) {
//                Log.d(TAG, t.getMessage());
//            }
//        });
//    }
}