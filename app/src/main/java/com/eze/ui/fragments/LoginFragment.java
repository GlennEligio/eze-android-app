package com.eze.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eze.R;
import com.eze.dtos.AccountWithTokens;
import com.eze.dtos.LoginAccount;
import com.eze.model.Account;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.UserClient;
import com.eze.room.viewmodel.AccountViewModel;
import com.eze.ui.ProfessorMainActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.ui.fragments.SettingsFragment.EZE_SETTING_PREFERENCES;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private EditText fragment_edt_username, fragment_edt_password;
    private Button fragment_btn_login;
    private CheckBox cb_remember_account;

    private UserClient userClient;

    private AccountViewModel accountViewModel;

    public static final String REMEMBER_ACCOUNT = "com.eze.remember.account";

    public LoginFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "LoginFragment created");

        init(view);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        fragment_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clicked");
                String username = fragment_edt_username.getText().toString();
                String password = fragment_edt_password.getText().toString();

                LoginAccount loginAccount = new LoginAccount(username, password);
                getLogin(loginAccount, requireView());
            }
        });

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(EZE_SETTING_PREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(REMEMBER_ACCOUNT,false)){
        }

        cb_remember_account.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(isChecked){
                    editor.putBoolean(REMEMBER_ACCOUNT, true);
                    editor.apply();
                }else{
                    editor.putBoolean(REMEMBER_ACCOUNT, false);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View view) {
        fragment_edt_username = view.findViewById(R.id.fragment_edt_username);
        fragment_edt_password = view.findViewById(R.id.fragment_edt_password);
        fragment_btn_login = view.findViewById(R.id.fragment_btn_Login);
        cb_remember_account = view.findViewById(R.id.cb_remember_account);

        userClient = APIClient.getClient().create(UserClient.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void getLogin(LoginAccount loginAccount, View view) {
        Log.d(TAG, "Sending Login credentials");
        Call<AccountWithTokens> call = userClient.getLogin(loginAccount);

        call.enqueue(new Callback<AccountWithTokens>() {
            @Override
            public void onResponse(Call<AccountWithTokens> call, Response<AccountWithTokens> response) {
                if(!response.isSuccessful())
                {
                    if(response.code() == 404){
                        Log.d(TAG, "No account found");
                        Toast.makeText(view.getContext(), "No account found in system", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "Code: " + response.code());
                    Toast.makeText(view.getContext(), "Login error. Please try again later", Toast.LENGTH_SHORT).show();
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

                accountViewModel.insert(account);

                Log.d(TAG, account.toString());


                Intent intent = new Intent(getContext(), ProfessorMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AccountWithTokens> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }
}