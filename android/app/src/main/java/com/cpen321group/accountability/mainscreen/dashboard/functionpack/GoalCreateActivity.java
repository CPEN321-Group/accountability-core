package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoalCreateActivity extends AppCompatActivity {
    private String goalName;
    private int goalTarget;
    public static int year;
    public static int month;
    public static int day;
    private String date;

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
                String goalTargetText = goalTargetEditText.getText().toString();
                date = "" + year + "/" + month + "/" + day;
                Log.d("Goal Name:", goalName);
                Log.d("Goal Target", "" + goalTarget);
                Log.d("Date:", "" + date);
                if (!goalName.equals("") && !goalTargetText.equals("") && !date.equals("")) {
                    goalTarget = (int) Math.round((Double.parseDouble(goalTargetText) * 100));
                    try {
                        createGoal();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent GoalSetIntent = new Intent(GoalCreateActivity.this, GoalSetActivity.class);
                            startActivity(GoalSetIntent);
                        }
                    }, 2000);
                } else {
                    Toast.makeText(GoalCreateActivity.this, "Some necessary information missing!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void createGoal() throws IOException{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VariableStoration.baseURL + "/goals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.postGoal(VariableStoration.userID, this.goalName, this.goalTarget, 0, this.date);

        Log.d("API url:", VariableStoration.baseURL + "/goals/"+VariableStoration.userID+"/");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(),"You have successfully added your new goal",Toast.LENGTH_LONG).show();
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                new MaterialAlertDialogBuilder(getApplicationContext())
                        .setIcon(R.drawable.ic_goal_set_24)
                        .setTitle("Sorry, we encountered in errors")
                        .setMessage("Failed to add your goal.")
                        .setNeutralButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent GoalCreateIntent = new Intent(GoalCreateActivity.this, GoalCreateActivity.class);
                                startActivity(GoalCreateIntent);
                            }
                        })
                        .show();
                Log.d("Message","error");
            }
        });
    }
}