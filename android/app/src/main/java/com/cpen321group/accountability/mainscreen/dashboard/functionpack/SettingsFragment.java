package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.VariablesSpace;

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
                Boolean switchPref = sharedPref.getBoolean("dark_mode", false);
                VariablesSpace.is_darkMode = switchPref;
                if (VariablesSpace.is_darkMode) {
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
                VariablesSpace.is_notificationGlobalOn = notificationPref;
                return false;
            }
        });
    }
}