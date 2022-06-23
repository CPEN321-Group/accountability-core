package com.cpen321group.accountability;

import static com.cpen321group.accountability.MainActivity.is_darkMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricPrompt;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.color.DynamicColors;

import java.util.concurrent.Executor;

/**
 * Settings screen for user to allow biometric signin, global notification, and dark mode
 * All settings are saved to global variables in Main Activity.
 *
 * TODO: 2022-06-21  store settings into the SharedPreferences
 */
public class AppSettingsActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        if (MainActivity.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(AppSettingsActivity.this,
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
                MainActivity.is_biometricAllowed = true;
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
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Dark mode switch
        Switch dark_mode = (Switch)findViewById(R.id.dark_mode);
        if (is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dark_mode.setChecked(true);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            dark_mode.setChecked(false);
        }
        dark_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (dark_mode.isPressed()){
                    if (b) {
                        is_darkMode = true;
                    } else {
                        is_darkMode = false;
                    }
                }
            }
        });

        // biometric login switch
        Switch biometric_switch = (Switch)findViewById(R.id.biometric_switch);
        if (MainActivity.is_biometricAllowed) {
            biometric_switch.setChecked(true);
        } else {
            biometric_switch.setChecked(false);
        }
        biometric_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (biometric_switch.isPressed()){
                    if (b) {
                        biometricPrompt.authenticate(promptInfo);
                    } else {
                        MainActivity.is_biometricAllowed = false;
                    }
                }
            }
        });

        // Global notification switch
        Switch notification_switch = (Switch)findViewById(R.id.notificiation_switch);
        if (MainActivity.is_notificationGlobalOn) {
            notification_switch.setChecked(true);
        } else {
            notification_switch.setChecked(false);
        }
        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (notification_switch.isPressed()){
                    if (b) {
                        MainActivity.is_notificationGlobalOn = true;
                    } else {
                        MainActivity.is_notificationGlobalOn = false;
                    }
                }
            }
        });

        Button set_theme_button = findViewById(R.id.set_theme_button);
        set_theme_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainIntent = new Intent(AppSettingsActivity.this, MainActivity.class);
                startActivity(MainIntent);
            }
        });
    }
}
