package com.cpen321group.accountability.mainscreen.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddReviewActivity extends AppCompatActivity {
    private String rate;
    private String title;
    private String content;
    private TextInputEditText reviewNameEditText;
    private TextInputEditText reviewContentEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //drop down menu
        AutoCompleteTextView autoText = findViewById(R.id.rate_text);

        String[] items = {"1", "2","3","4","5"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(AddReviewActivity.this, R.layout.list_item, items);
        autoText.setAdapter(itemAdapter);
        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                rate = (String)parent.getItemAtPosition(position);
            }
        });

        //Title
        reviewNameEditText = (TextInputEditText) findViewById(R.id.title_text);


        //Content
        reviewContentEditText = (TextInputEditText) findViewById(R.id.reviewcontentInput);
        content = reviewContentEditText.getText().toString();

        Button create_button = findViewById(R.id.create_button);
        create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = reviewNameEditText.getText().toString();
                content = reviewContentEditText.getText().toString();
                if(rate != null && !content.equals("") && !title.equals("")) {
                    postReview();
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent settingsIntent = new Intent(AddReviewActivity.this, ReviewActivity.class);
                            startActivity(settingsIntent);
                        }
                    },4000);
                }else{
                    Toast.makeText(AddReviewActivity.this,"Some necessary information missing!",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void postReview(){
        title = reviewNameEditText.getText().toString();
        content = reviewContentEditText.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reviews/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Date date = Calendar.getInstance().getTime();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.postReview(FrontendConstants.receiverID, FrontendConstants.userID,date,content,title,Integer.parseInt(rate));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_LONG).show();
                Log.d("postReview",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to add, Check the Internet!",Toast.LENGTH_LONG).show();
                Log.d("postReview",t.toString());
            }
        });
    }
}