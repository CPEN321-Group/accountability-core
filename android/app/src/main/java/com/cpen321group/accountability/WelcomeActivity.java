package com.cpen321group.accountability;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    private String TAG = "WelcomeActivity";
    private Button loginbutton;
    private Button registerbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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