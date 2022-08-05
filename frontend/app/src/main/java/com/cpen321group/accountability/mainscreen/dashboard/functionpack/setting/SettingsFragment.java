package com.cpen321group.accountability.mainscreen.dashboard.functionpack.setting;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        Preference biometric_pref = findPreference("biometric");
        BiometricManager biometricManager = BiometricManager.from(getContext());
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                Toast.makeText(getContext(), "App can authenticate using biometrics.", Toast.LENGTH_LONG);
                biometric_pref.setEnabled(true);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                Toast.makeText(getContext(), "No biometric features available on this device.", Toast.LENGTH_LONG);
                biometric_pref.setEnabled(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                Toast.makeText(getContext(), "Biometric features are currently unavailable.", Toast.LENGTH_LONG);
                biometric_pref.setEnabled(false);
                break;
            default:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                Toast.makeText(getContext(), "Biometric features are currently unavailable.", Toast.LENGTH_LONG);
                biometric_pref.setEnabled(false);
                break;
        }

        Preference dark_mode_pref = findPreference("dark_mode");
        dark_mode_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean switchPref = sharedPref.getBoolean("dark_mode", false);
                FrontendConstants.is_darkMode = switchPref;
                if (FrontendConstants.is_darkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return false;
            }
        });

        Preference notification_pref = findPreference("notification_allow");
        notification_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean notificationPref = sharedPref.getBoolean("notification_allow", false);
                FrontendConstants.is_notificationGlobalOn = notificationPref;
                return false;
            }
        });


        biometric_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean switchPref = sharedPref.getBoolean("biometric", false);
                FrontendConstants.is_biometricAllowed = switchPref;
                Log.d("biometric:", ""+FrontendConstants.is_biometricAllowed);
                return false;
            }
        });
    }
}