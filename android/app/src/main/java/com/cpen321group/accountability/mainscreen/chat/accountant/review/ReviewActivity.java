package com.cpen321group.accountability.mainscreen.chat.accountant.review;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewActivity extends AppCompatActivity {
    private List<Review> reviewList = new ArrayList<>();
    private RecyclerView reviewRecyclerView;
    private ReviewSetting adapter;
    private String accountMark = "5.0";
    private TextView rating_mark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.topAppBar_review);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        getReviews();
        reviewRecyclerView = findViewById(R.id.review_view);
        FloatingActionButton createXtendButton = (FloatingActionButton)findViewById(R.id.floating_action_button_review);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new ReviewSetting(reviewList);
        reviewRecyclerView.setLayoutManager(layoutManager);
        reviewRecyclerView.setAdapter(adapter);
        rating_mark = findViewById(R.id.textView4);

        createXtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goalCreateIntent = new Intent(ReviewActivity.this, AddReviewActivity.class);
                startActivity(goalCreateIntent);
            }
        });
    }

    private void getReviews(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/accounts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.findAccount(FrontendConstants.receiverID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject jsonArray = response.body();
                    Log.d("review", response.toString());
                    if (jsonArray != null) {
                        JsonArray reviews = jsonArray.getAsJsonArray("reviews");
                        if (reviews != null) {
                            int count = 0;
                            int mark = 0;
                            for (int i = 0; i < reviews.size(); i++) {
                                JsonObject jsonObject = reviews.get(i).getAsJsonObject();
                                if (jsonObject != null) {
                                    String content = "";
                                    String title = "";
                                    if (jsonObject.get("content") != null) {
                                        Log.d("Review", jsonObject.get("content").getAsString());
                                        content = jsonObject.get("content").getAsString();
                                    }
                                    if (jsonObject.get("title") != null) {
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
                            accountMark = df.format((float) mark / (float) count);
                            rating_mark.setText(accountMark);
                        }
                    }
                }catch(Exception e){
                    Log.d("Review",e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Review",t.toString());
            }
        });
    }
}