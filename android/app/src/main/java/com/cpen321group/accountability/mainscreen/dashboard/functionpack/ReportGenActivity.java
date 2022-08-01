package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.reportpiechart.PieClickListener;
import com.cpen321group.accountability.reportpiechart.PieEntry;
import com.cpen321group.accountability.reportpiechart.ReportPieChart;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportGenActivity extends AppCompatActivity implements PieClickListener {

    public static Context context;
    private RecyclerView reportRV;

    // Arraylist for storing data
    private ArrayList<ReportModel> reportModelArrayList;

    // disable system return button
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
        Intent backIntent = new Intent(ReportGenActivity.this, HomeScreenActivity.class);
        startActivity(backIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_gen);
        
        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.topAppBar_report);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        reportRV = findViewById(R.id.reportRV);
        getAllReports();
    }

    private void getAllReports() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reports/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllReports(FrontendConstants.userID);

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                ArrayList<JsonObject> jsonArray = response.body();
                Log.d("User's all reports:",response.toString());
                if(jsonArray!=null) {
                    reportModelArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i);
                        String id = jsonObject.get("_id").toString();
                        String monthYear = jsonObject.get("monthYear").toString().replace("\"", "");
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        DateTimeFormatter outputFormatter_monthAndYear = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
                        DateTimeFormatter outputFormatter_monthOnly = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
                        LocalDate date = LocalDate.parse(monthYear, inputFormatter);
                        String formattedMonthAndYear = outputFormatter_monthAndYear.format(date);
                        String formattedMonthOnly = outputFormatter_monthOnly.format(date);
                        String reportName = "Financial Report (" + formattedMonthOnly + ")";
                        String reportDetail = "Your financial report in " + formattedMonthAndYear;
                        reportModelArrayList.add(new ReportModel(reportName, reportDetail, FrontendConstants.userID, id));
                    }
                    ReportAdapter reportAdapter = new ReportAdapter(reportModelArrayList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    reportRV.setLayoutManager(linearLayoutManager);
                    reportRV.setAdapter(reportAdapter);
                } else {
                    Toast.makeText(getApplicationContext(),"You don't have any reports, create one first.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Internet connection failure, try again",Toast.LENGTH_LONG).show();
                Log.d("history",t.toString());
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Touched" + position, Toast.LENGTH_SHORT).show();
    }
}