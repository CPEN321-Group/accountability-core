package com.cpen321group.accountability.mainScreen.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity {
    private List<Review> reviewList = new ArrayList<>();
    private RecyclerView reviewRecyclerView;
    private Button new_button;
    private LinearLayoutManager layoutManager;
    private reviewSetting adapter;
    private String accountMark = "5.0";
    private TextView rating_mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        if (VariableStoration.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        getReviews();
        reviewRecyclerView = findViewById(R.id.review_view);
        new_button = findViewById(R.id.button3);
        layoutManager = new LinearLayoutManager(this);
        adapter = new reviewSetting(reviewList);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setAdapter(adapter);
        rating_mark = findViewById(R.id.textView4);
        Button back_button =findViewById(R.id.button_back);

        back_button.setVisibility(View.INVISIBLE);

        new_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(ReviewActivity.this, AddReviewActivity.class);
                startActivity(settingsIntent);
            }
        });
    }

    private void getReviews(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/accounts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.getAccount(VariableStoration.receiverID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonArray = response.body();
                Log.d("review",response.toString());
                if(jsonArray!=null) {
                    JsonArray reviews = jsonArray.getAsJsonArray("reviews");
                    if(reviews!=null) {
                        int count = 0;
                        int mark = 0;
                        for (int i = 0; i < reviews.size(); i++) {
                            JsonObject jsonObject = reviews.get(i).getAsJsonObject();
                            if(jsonObject != null) {
                                String content = "";
                                String title = "";
                                if(jsonObject.get("content")!=null) {
                                    Log.d("Review", jsonObject.get("content").getAsString());
                                    content = jsonObject.get("content").getAsString();
                                }
                                if(jsonObject.get("title")!=null) {
                                    title = jsonObject.get("title").getAsString();
                                }
                                String date = jsonObject.get("date").getAsString();
                                String rate = jsonObject.get("rating").getAsString();
                                Log.d("Review", title);
                                Log.d("Review", date);
                                Log.d("Review", rate);
                                mark = Integer.valueOf(rate) + mark;
                                Review review_text = new Review("", "", "", "");
                                review_text.setContent(content);
                                review_text.setDate(date.substring(0, 10));
                                review_text.setRating(rate);
                                review_text.setTitle(title);
                                reviewList.add(review_text);
                                adapter.notifyItemInserted(reviewList.size() - 1);
                                reviewRecyclerView.scrollToPosition(reviewList.size() - 1);
                                count++;
                            }
                        }
                        reviewRecyclerView.scrollToPosition(0);
                        DecimalFormat df = new DecimalFormat("0.0");
                        accountMark = df.format((float)mark/(float)count);
                        rating_mark.setText(accountMark);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Review",t.toString());
            }
        });
    }
}