package com.eze.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.eze.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    public static final String EZE_SETTING_PREFERENCES = "com.eze.sharedPreferences";
    public static final String ENABLE_NOTIFICATION = "com.eze.sharedpreferences.enable.notification";

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    private void init(View view) {
        if(view == null){
            return;
        }

        sharedPreferences = view.getContext().getSharedPreferences(EZE_SETTING_PREFERENCES, Context.MODE_PRIVATE);

        SwitchCompat sw_enable_notification = view.findViewById(R.id.sw_enable_notification);

        sw_enable_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(isChecked){
                    editor.putBoolean(ENABLE_NOTIFICATION, true);
                    editor.apply();
                    Toast.makeText(getContext(), "Restart the application for settings to take effect", Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean(ENABLE_NOTIFICATION, false);
                    editor.apply();
                    Toast.makeText(getContext(), "Restart the application for settings to take effect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sw_enable_notification.setChecked(sharedPreferences.getBoolean(ENABLE_NOTIFICATION, false));
    }
}