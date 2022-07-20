package com.cpen321group.accountability.mainscreen.dashboard.functionpack;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoalSetActivity extends AppCompatActivity {

    public static Context context;
    private RecyclerView goalsRV;

    // Arraylist for storing data
    private ArrayList<GoalsModel> goalsModelArrayList;

    // disable system return button
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Intent backIntent = new Intent(GoalSetActivity.this, HomeScreenActivity.class);
        startActivity(backIntent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_set);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        goalsRV = findViewById(R.id.goalRV);

        getAllGoals();
        // here we have created new array list and added data to it.


        FloatingActionButton createXtendButton = (FloatingActionButton)findViewById(R.id.floating_action_button_goal);
        createXtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goalCreateIntent = new Intent(GoalSetActivity.this, GoalCreateActivity.class);
                startActivity(goalCreateIntent);
            }
        });
    }

    private void getAllGoals(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/goals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllGoals(FrontendConstants.userID);

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                ArrayList<JsonObject> jsonArray = response.body();
                Log.d("User's all goals:",response.toString());
                if(jsonArray!=null) {
                    goalsModelArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i);
                        String title = jsonObject.get("title").toString();
                        String id = jsonObject.get("_id").toString();
                        int goal_cents = Integer.valueOf(jsonObject.get("target").toString());
                        int current_saving_cents = Integer.valueOf(jsonObject.get("current").toString());
                        double price_dollar = goal_cents / 100.0;
                        double current_saving = current_saving_cents / 100.0;
                        goalsModelArrayList.add(new GoalsModel(title, id, FrontendConstants.userID, price_dollar, current_saving));
                        GoalsAdapter goalsAdapter = new GoalsAdapter(goalsModelArrayList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        goalsRV.setLayoutManager(linearLayoutManager);
                        goalsRV.setAdapter(goalsAdapter);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"You don't have any goal set.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d("history",t.toString());
            }
        });
    }
}