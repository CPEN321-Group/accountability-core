package com.cpen321group.accountability;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        if (MainActivity.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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