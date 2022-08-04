package com.cpen321group.accountability.mainscreen.dashboard.functionpack.report;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.mainscreen.dashboard.functionpack.report.reportpiechart.PieClickListener;
import com.cpen321group.accountability.mainscreen.dashboard.functionpack.report.reportpiechart.PieEntry;
import com.cpen321group.accountability.mainscreen.dashboard.functionpack.report.reportpiechart.ReportPieChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportDisplayActivity extends AppCompatActivity implements PieClickListener {
    private ReportPieChart reportPieChart;
    private String reportId;
    private String usertxt;
    private String recommendation = " ";

    double amount_daily_necessities = 0.0;
    double amount_food_drinks = 0.0;
    double amount_transportation = 0.0;
    double amount_housing = 0.0;
    double amount_education = 0.0;
    double amount_bills = 0.0;
    double amount_others = 0.0;

    private  String[] items = {"daily necessities", "food/drinks", "transportation", "housing", "education", "bills", "others"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_display);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.topAppBar_report_display);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        //Starting of this activity
        Bundle extras = getIntent().getExtras();
        reportId = extras.getString("reportId");
        usertxt = extras.getString("userID");
        Log.d("id",usertxt);
        reportPieChart = (ReportPieChart) findViewById(R.id.piechart);
        reportPieChart.setRadiusDefault(ReportPieChart.dp2px(this, 80));
        reportPieChart.setPieClickListener(this);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        getReport(pieEntries);

        FloatingActionButton add_button = findViewById(R.id.addCommentButton);

        if(FrontendConstants.isAccountant){
            myChildToolbar.setTitle("User Report");
            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reportDisplayIntent = new Intent(getApplicationContext(), AddCommentActivity.class);
                    reportDisplayIntent.putExtra("reportId", reportId);
                    reportDisplayIntent.putExtra("userID",usertxt);
                    startActivity(reportDisplayIntent);
                }
            });
        }else{
            add_button.setVisibility(View.GONE);
        }

        Button comment = findViewById(R.id.comment_button);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportDisplayIntent = new Intent(getApplicationContext(), CommentActivity.class);
                reportDisplayIntent.putExtra("recommendation", recommendation);
                startActivity(reportDisplayIntent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, items[position], Toast.LENGTH_SHORT).show();
    }

    private void getReport(ArrayList<PieEntry> pieEntries) {
        Log.d("reportId", reportId);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reports/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.getSpecificReport(usertxt, reportId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject JsonArray = response.body();
                if(JsonArray.get("recommendations")!=null){
                    recommendation = JsonArray.get("recommendations").getAsString();
                }
                JsonArray spendingArray = JsonArray.getAsJsonArray("spendings");
                int sizeOfSpendingArray = spendingArray.size();
                amount_daily_necessities = 0.0;
                amount_food_drinks = 0.0;
                amount_transportation = 0.0;
                amount_housing = 0.0;
                amount_education = 0.0;
                amount_bills = 0.0;
                amount_others = 0.0;
                if (sizeOfSpendingArray == 0) {
                    Toast.makeText(getApplicationContext(), "You don't have any spending records during this month", Toast.LENGTH_LONG).show();
                } else {
                    for (int i = 0; i < sizeOfSpendingArray; i++) {
                        JsonObject singleSpending = spendingArray.get(i).getAsJsonObject();
                        Log.d("singleSpending:", singleSpending.toString());
                        String spendingCategory = singleSpending.get("category").toString().replace("\"", "");
                        float amount = (float) (singleSpending.get("amount").getAsInt()/100.0);
                        Log.d("singleSpendingCategory: ", spendingCategory);
                        Log.d("singleSpendingAmount: ", "" + amount);
                        switch (spendingCategory) {
                            case "daily necessities":
                                amount_daily_necessities += amount;
                                break;
                            case "food/drinks":
                                amount_food_drinks += amount;
                                break;
                            case "transportation":
                                amount_transportation += amount;
                                break;
                            case "housing":
                                amount_housing += amount;
                                break;
                            case "education":
                                amount_education += amount;
                                break;
                            case "bills":
                                amount_bills += amount;
                                break;
                            case "others":
                                amount_others += amount;
                                break;
                            default:
                                amount_others += amount;
                                break;
                        }
                    }
                }
                Log.d("daily necessities", ""+amount_daily_necessities);
                Log.d("food/drinks", ""+amount_food_drinks);
                Log.d("transportation", ""+amount_transportation);
                Log.d("housing", ""+amount_housing);
                Log.d("education", ""+amount_education);
                Log.d("bills", ""+amount_bills);
                Log.d("others", ""+amount_others);
                pieEntries.add(new PieEntry((float)amount_daily_necessities, R.color.chart_orange, true, "daily necessities"));
                pieEntries.add(new PieEntry((float)amount_food_drinks, R.color.chart_green, false,"food/drinks"));
                pieEntries.add(new PieEntry((float)amount_transportation, R.color.chart_blue, false, "transportation"));
                pieEntries.add(new PieEntry((float)amount_housing, R.color.chart_purple, false, "housing"));
                pieEntries.add(new PieEntry((float)amount_education, R.color.chart_mblue, false, "education"));
                pieEntries.add(new PieEntry((float)amount_bills, R.color.chart_turquoise, false, "bills"));
                pieEntries.add(new PieEntry((float)amount_others, R.color.teal_700, false, "others"));
                reportPieChart.setPieEntries(pieEntries);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Try Again!",Toast.LENGTH_LONG).show();
            }
        });
    }
}