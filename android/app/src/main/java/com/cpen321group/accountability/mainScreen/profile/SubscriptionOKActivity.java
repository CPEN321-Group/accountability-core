package com.cpen321group.accountability.mainScreen.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cpen321group.accountability.MainActivity;
import com.cpen321group.accountability.R;
import com.google.android.material.color.DynamicColors;

public class SubscriptionOKActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable dark mode
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_ok);
        if (MainActivity.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Button start_subscription = findViewById(R.id.cancel_subscription_button);
        start_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.is_subscribed = false;
            }
        });
    }
}