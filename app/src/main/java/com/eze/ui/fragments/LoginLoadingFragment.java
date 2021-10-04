package com.eze.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eze.R;
import com.eze.executor.AppExecutors;
import com.eze.ui.ProfessorMainActivity;

import static com.eze.ui.fragments.LoginFragment.REMEMBER_ACCOUNT;
import static com.eze.ui.fragments.SettingsFragment.EZE_SETTING_PREFERENCES;


public class LoginLoadingFragment extends Fragment {
    private static final String TAG = "LoginLoadingFragment";
    private TextView txt_loading_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login_loading, container, false);
        txt_loading_text = view.findViewById(R.id.txt_loading_text);
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(EZE_SETTING_PREFERENCES, Context.MODE_PRIVATE);

        Log.d(TAG, "onViewCreated: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                if(!sharedPreferences.getBoolean(REMEMBER_ACCOUNT, false)){
                    SystemClock.sleep(2000);
                    Log.d(TAG, "Going to login fragment");
                    AppExecutors.getInstance().getMainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            navController.navigate(R.id.action_loginLoadingFragment_to_loginFragment);
                        }
                    });
                }else {
                    AppExecutors.getInstance().getMainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            txt_loading_text.setText("Logging in...");
                        }
                    });
                    SystemClock.sleep(2000);
                    Intent intent = new Intent(view.getContext(), ProfessorMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //TODO: Create logic to base the activity destination of intent on the role of last account signed in
                }
            }
        }).start();
    }
}