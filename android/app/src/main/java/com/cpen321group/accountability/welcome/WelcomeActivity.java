package com.cpen321group.accountability.welcome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.VariableStore;
import com.google.android.material.color.DynamicColors;

public class WelcomeActivity extends AppCompatActivity {
    private String TAG = "WelcomeActivity";
    private Button loginbutton;
    private Button registerbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);

        // Dark Mode Toggle
        Boolean switchPref = sharedPref.getBoolean
                ("dark_mode", false);
        VariableStore.is_darkMode = switchPref;
        if (VariableStore.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Notification toggle
        Boolean notificationPref = sharedPref.getBoolean("notification_allow", false);
        VariableStore.is_notificationGlobalOn = notificationPref;


        loginbutton = findViewById(R.id.welcome_login);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Log in...");
                Intent signIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(signIntent);
            }
        });

        registerbutton = findViewById(R.id.welcome_register);
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Log in...");
                Intent signIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(signIntent);
            }
        });
    }
}