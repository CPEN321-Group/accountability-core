package com.cpen321group.accountability.mainscreen.dashboard.functionpack.goal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoalUpdateActivity extends AppCompatActivity {
    private int goalCurrent;
    private String goalId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_update);

        Button updateGoal = findViewById(R.id.goalUpdateButton);
        updateGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText goalCurrentEditText = (TextInputEditText) findViewById(R.id.goalCurrentPriceInput);
                String goalTextinput = goalCurrentEditText.getText().toString();

                try {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        goalId = extras.getString("goalId");
                        //The key argument here must match that used in the other activity
                    }
                    if(goalId != null && !goalTextinput.equals("")) {
                        goalCurrent = (int)Math.round((Double.parseDouble(goalCurrentEditText.getText().toString())*100));
                        Log.d("Goal Current", ""+goalCurrent);
                        updateGoal();
                        Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent GoalSetIntent = new Intent(GoalUpdateActivity.this, GoalSetActivity.class);
                                startActivity(GoalSetIntent);
                            }
                        },2000);
                    }else{
                        Toast.makeText(GoalUpdateActivity.this,"Some necessary information missing!",Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateGoal() throws IOException{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/goals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.updateGoal(FrontendConstants.userID, goalId, this.goalCurrent);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code()==200) {
                    Toast.makeText(getApplicationContext(), "You have successfully updated your selected goal", Toast.LENGTH_LONG).show();
                }
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to update your new goal",Toast.LENGTH_LONG).show();
                Log.d("Err",t.toString());
            }
        });
    }
}