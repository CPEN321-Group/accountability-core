package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cpen321group.accountability.R;
import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.StandardCharsets;

public class GoalCreateActivity extends AppCompatActivity {
    private String goalName;
    private Double goalTarget;
    public static int year;
    public static int month;
    public static int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_create);
        Button createGoal = findViewById(R.id.goalCreateButton);
        createGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText goalNameEditText = (TextInputEditText) findViewById(R.id.goalNameInput);
                goalName = goalNameEditText.getText().toString();

                TextInputEditText goalTargetEditText = (TextInputEditText) findViewById(R.id.goalTargetPriceInput);
                goalTarget = Double.parseDouble(goalTargetEditText.getText().toString());

                Log.d("Goal Name:", goalName);
                Log.d("Goal Target", ""+goalTarget);
                Log.d("Year:", ""+year);
                Log.d("Month:", ""+month);
                Log.d("Day:",""+day);
            }
        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}