package com.cpen321group.accountability.mainScreen.dashboard.functionpack;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.GoalsAdapter;
import com.cpen321group.accountability.GoalsModel;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.welcome.LoginActivity;
import com.cpen321group.accountability.welcome.WelcomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GoalSetActivity extends AppCompatActivity {

    private RecyclerView goalsRV;

    // Arraylist for storing data
    private ArrayList<GoalsModel> goalsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_set);
        goalsRV = findViewById(R.id.goalRV);

        // here we have created new array list and added data to it.
        goalsModelArrayList = new ArrayList<>();
        goalsModelArrayList.add(new GoalsModel("Save for new Bike", "***", 315.27));
        goalsModelArrayList.add(new GoalsModel("Trip to Mexico", "***", 1000.00));

        // we are initializing our adapter class and passing our arraylist to it.
        GoalsAdapter goalsAdapter = new GoalsAdapter(this, goalsModelArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

//        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        goalsRV.setLayoutManager(linearLayoutManager);
        goalsRV.setAdapter(goalsAdapter);

        FloatingActionButton createXtendButton = (FloatingActionButton)findViewById(R.id.floating_action_button_goal);
        createXtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goalCreateIntent = new Intent(GoalSetActivity.this, GoalCreateActivity.class);
                startActivity(goalCreateIntent);
            }
        });
    }
}