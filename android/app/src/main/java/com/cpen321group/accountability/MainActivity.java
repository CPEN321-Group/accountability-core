package com.cpen321group.accountability;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen321group.accountability.welcome.WelcomeActivity;
import com.google.android.material.color.DynamicColors;

public class MainActivity extends AppCompatActivity {
    public static boolean is_darkMode;
    public static boolean is_notificationGlobalOn;
    public static boolean is_biometricAllowed;
    public static String userID;
    public static String userName;
    public static boolean isAccountant = false;
    public static boolean is_subscribed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
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

        Button test = findViewById(R.id.test_button);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

}