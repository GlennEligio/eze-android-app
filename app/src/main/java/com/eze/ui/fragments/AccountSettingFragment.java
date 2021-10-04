package com.eze.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eze.R;
import com.eze.dtos.UpdateAccount;
import com.eze.model.Account;
import com.eze.retrofit.APIClient;
import com.eze.retrofit.UserClient;
import com.eze.room.viewmodel.AccountViewModel;
import com.eze.ui.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eze.ui.fragments.LoginFragment.REMEMBER_ACCOUNT;
import static com.eze.ui.fragments.SettingsFragment.ENABLE_NOTIFICATION;
import static com.eze.ui.fragments.SettingsFragment.EZE_SETTING_PREFERENCES;
import static com.eze.worker.UpdatePendingRequestWorker.NOTIFICATION_WORK_REQUEST_NAME;

public class AccountSettingFragment extends Fragment {

    private static final String TAG = "AccountSettingFragment";

    private AccountViewModel accountViewModel;
    private TextView txt_account_id, txt_account_name, txt_account_username, txt_account_role;
    private RelativeLayout relLayout_password;
    private ImageView img_profile;
    private Account latestAccount;
    private Button btn_logout;

    private UserClient userClient;

    public AccountSettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_setting, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        populateViews();
    }

    private void populateViews() {
        latestAccount = accountViewModel.getLatestAccount();
        if(latestAccount == null){
            return;
        }
        txt_account_username.setText(latestAccount.getUsername());
        txt_account_name.setText(latestAccount.getName());
        txt_account_id.setText(latestAccount.getId());
        txt_account_role.setText(latestAccount.getRole());
        img_profile.setImageResource(R.mipmap.download);

        relLayout_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder changePasswordDialog = new AlertDialog.Builder(getContext());
                final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_password_change, null);

                changePasswordDialog.setView(dialogView);
                changePasswordDialog.setTitle("Change password");

                final EditText edt_old_password = dialogView.findViewById(R.id.edt_old_password);
                final EditText edt_new_password = dialogView.findViewById(R.id.edt_new_password);
                final EditText edt_re_enter_password = dialogView.findViewById(R.id.edt_re_enter_password);

                changePasswordDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!edt_new_password.getText().toString().equals(edt_re_enter_password.getText().toString())){
                            Toast.makeText(getContext(), "New passwords do not match.", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(!latestAccount.getPassword().equals(edt_old_password.getText().toString())){
                            Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        updateAccount(latestAccount, edt_new_password.getText().toString());
                    }
                });
                changePasswordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                changePasswordDialog.create().show();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Logout")
                        .setMessage("Do you want to logout your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Delete all account in local db, clear setting preferences, cancels all workers, and navigate user to Login Activity
                                accountViewModel.deleteAllAccount();
                                SharedPreferences sharedPreferences = getContext().getSharedPreferences(EZE_SETTING_PREFERENCES, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(REMEMBER_ACCOUNT, false);
                                editor.putBoolean(ENABLE_NOTIFICATION, false);
                                editor.apply();
                                WorkManager.getInstance(v.getContext()).cancelUniqueWork(NOTIFICATION_WORK_REQUEST_NAME);
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(View view) {
        txt_account_id = view.findViewById(R.id.txt_account_id);
        txt_account_name = view.findViewById(R.id.txt_accountName);
        txt_account_username = view.findViewById(R.id.txt_account_username);
        txt_account_role = view.findViewById(R.id.txt_account_role);
        img_profile = view.findViewById(R.id.img_profile);
        btn_logout = view.findViewById(R.id.btn_logout);
        relLayout_password = view.findViewById(R.id.relLayout_account_password);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        userClient = APIClient.getClient().create(UserClient.class);
    }

    public void updateAccount(Account account, String newPassword){
        UpdateAccount updateAccount = new UpdateAccount(account.getName(), account.getUsername(), newPassword, account.getRole());
        Call<Void> call = userClient.updateAccount(account.getId(), updateAccount);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Log.d(TAG, "Update unsuccessful. Error code: " + response.code());
                    Toast.makeText(getContext(), "Update error. Please try again", Toast.LENGTH_SHORT).show();
                }

                account.setPassword(newPassword);

                accountViewModel.update(account);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(getContext(), "Error has occured. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


}