package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.VariableStoration;

import java.util.concurrent.Executor;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Preference dark_mode_pref = findPreference("dark_mode");

        dark_mode_pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean switchPref = sharedPref.getBoolean
                        (SettingsActivity.KEY_PREF_EXAMPLE_SWITCH, false);
                Toast.makeText(getContext(), "Dark Theme: enabled", Toast.LENGTH_SHORT).show();
                VariableStoration.is_darkMode = switchPref;
                if (VariableStoration.is_darkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return false;
            }
        });
    }
}