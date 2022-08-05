package com.cpen321group.accountability.welcome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.welcome.register.RegisterActivity;
import com.google.android.material.color.DynamicColors;

import java.util.concurrent.Executor;

public class WelcomeActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private String TAG = "WelcomeActivity";
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Toast.makeText(this, "You need to sign in or register first", Toast.LENGTH_LONG).show();
    }
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

        SharedPreferences sharedPreferences = getSharedPreferences("APP", MODE_PRIVATE);
        int is_signed = sharedPreferences.getInt("is_logged_in", 0);
        boolean isAccountant = sharedPreferences.getBoolean("isAccountant", false);
        boolean is_subscribed = sharedPreferences.getBoolean("is_subscribed", false);
        String userId = sharedPreferences.getString("userId", "");

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(WelcomeActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                //Finger print authentication succeed.
                FrontendConstants.is_subscribed = is_subscribed;
                FrontendConstants.userID = userId;
                FrontendConstants.avatar = " ";
                FrontendConstants.isAccountant = isAccountant;
                Intent intent = new Intent(WelcomeActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Accountability")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use 3rd party login")
                .build();


        // Dark Mode Toggle
        Boolean switchPref = sharedPref.getBoolean
                ("dark_mode", false);
        FrontendConstants.is_darkMode = switchPref;
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // Notification toggle
        Boolean notificationPref = sharedPref.getBoolean("notification_allow", false);
        FrontendConstants.is_notificationGlobalOn = notificationPref;

        Boolean is_biometricAllowed = sharedPref.getBoolean("biometric", false);

        Button biometric_button = findViewById(R.id.finger_button);
        if(is_signed == 1 && is_biometricAllowed) {
            biometric_button.setVisibility(View.VISIBLE);
            biometric_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });
        } else {
            biometric_button.setVisibility(View.INVISIBLE);
        }
        Button loginbutton = findViewById(R.id.welcome_login);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Log in...");
                Intent signIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(signIntent);
            }
        });

        Button registerbutton = findViewById(R.id.welcome_register);
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