package com.cpen321group.accountability;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public static boolean is_darkMode;
    public static boolean is_notificationGlobalOn;
    public static boolean is_biometricAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //load theme preference

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        Button settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(MainActivity.this, HomeScreenActivity.class);
                startActivity(settingsIntent);
            }
        });

    }
}