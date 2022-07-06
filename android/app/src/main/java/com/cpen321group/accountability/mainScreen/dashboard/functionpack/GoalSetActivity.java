package com.cpen321group.accountability.mainScreen.dashboard.functionpack;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.mainScreen.chat.Msg;
import com.google.android.material.color.DynamicColors;
import com.cpen321group.accountability.GoalsAdapter;
import com.cpen321group.accountability.GoalsModel;
import com.cpen321group.accountability.welcome.LoginActivity;
import com.cpen321group.accountability.welcome.WelcomeActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_set);
        if (VariableStoration.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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
                .baseUrl("http://20.239.52.70:8000/goals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllGoals(VariableStoration.userID);

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
                        double price_dollar = goal_cents/100.0;
                        goalsModelArrayList.add(new GoalsModel(title, id, VariableStoration.userID, price_dollar));
                        GoalsAdapter goalsAdapter = new GoalsAdapter(getApplicationContext(), goalsModelArrayList);
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