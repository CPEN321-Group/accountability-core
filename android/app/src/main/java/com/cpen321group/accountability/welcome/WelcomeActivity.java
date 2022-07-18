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
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.VariableStoration;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.SettingsActivity;
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
        if (VariableStoration.is_darkMode) {
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

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean
                (SettingsActivity.KEY_PREF_EXAMPLE_SWITCH, false);
        Toast.makeText(this, switchPref.toString(), Toast.LENGTH_SHORT).show();
        VariableStoration.is_darkMode = switchPref;
    }
}