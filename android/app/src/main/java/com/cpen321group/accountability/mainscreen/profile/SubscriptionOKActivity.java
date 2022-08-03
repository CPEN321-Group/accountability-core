package com.cpen321group.accountability.mainscreen.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.RetrofitAPI;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubscriptionOKActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable dark mode
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_ok);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Button start_subscription = findViewById(R.id.cancel_subscription_button);
        start_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSubscription();
                FrontendConstants.is_subscribed = false;
                Intent homeScreen = new Intent(SubscriptionOKActivity.this, HomeScreenActivity.class);
                startActivity(homeScreen);
            }
        });
    }
    private void stopSubscription() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/subscription/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        //String subscriptionDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Calendar c= Calendar.getInstance();
        c.add(Calendar.DATE, -30);
        Date d=c.getTime();
        String expiryDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
        Call<JsonObject> call = retrofitAPI.updateSubscription(FrontendConstants.userID, expiryDate);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(),"You have cancelled our advanced service",Toast.LENGTH_LONG).show();
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Message","error");
            }
        });
    }
}