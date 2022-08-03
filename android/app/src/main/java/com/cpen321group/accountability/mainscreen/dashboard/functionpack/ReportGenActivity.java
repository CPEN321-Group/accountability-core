package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormatSymbols;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.RetrofitAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportGenActivity extends AppCompatActivity {

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

        FloatingActionButton createXtendButton = (FloatingActionButton)findViewById(R.id.floating_action_button_report);
        createXtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(ReportGenActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        String monthYear = "" + new DateFormatSymbols().getMonths()[selectedMonth] + " " + selectedYear;
                        createNewReport(monthYear);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
                builder.build().show();
            }
        });

        if(FrontendConstants.isAccountant){
            getSharedReports();
            myChildToolbar.setTitle("Shared Reports");
            createXtendButton.setEnabled(false);
        }else{
            getAllReports();
        }
    }

    private void createNewReport(String monthYear) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reports/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.postReport(FrontendConstants.userID, monthYear);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject jsonArray = response.body();
                Log.d("Message",response.toString());
                if(jsonArray!=null) {
                    Toast.makeText(getApplicationContext(),"You have successfully created your new report",Toast.LENGTH_LONG).show();
                    Log.d("Message",response.toString());
                    Intent refresh = new Intent(getApplicationContext(), ReportGenActivity.class);
                    startActivity(refresh);
                } else {
                    try {
                        String err = response.errorBody().string().replace("\"", "");
                        Log.v("Error code 400",err);
                        Toast.makeText(getApplicationContext(),err,Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Sorry, we encountered in errors, failed to create your report. check your internet and try Again",Toast.LENGTH_LONG).show();
                Log.d("Message","error");
            }
        });
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
                    if (jsonArray.size() == 0) {
                        Toast.makeText(getApplicationContext(),"You don't have any report, create new one first",Toast.LENGTH_LONG).show();
                    } else {
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
                    }
                } else {
                    try {
                        String err = response.errorBody().string().replace("\"", "");
                        Log.v("Error code 400",err);
                        Toast.makeText(getApplicationContext(),err,Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Internet connection failure, failed to fetch your reports, try again",Toast.LENGTH_LONG).show();
                Log.d("history",t.toString());
            }
        });
    }

    private void getSharedReports() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reports/accountants/")
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
                    if (jsonArray.size() == 0) {
                        Toast.makeText(getApplicationContext(),"You don't have any report, create new one first",Toast.LENGTH_LONG).show();
                    } else {
                        reportModelArrayList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.get(i);
                            String userID = jsonObject.get("userId").getAsString();
                            JsonArray reports = jsonObject.get("reports").getAsJsonArray();
                            for (int k = 0; k < reports.size(); k++) {
                                JsonObject json = reports.get(k).getAsJsonObject();
                                String id = json.get("_id").toString();
                                String monthYear = json.get("monthYear").toString().replace("\"", "");
                                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                                DateTimeFormatter outputFormatter_monthAndYear = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
                                DateTimeFormatter outputFormatter_monthOnly = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
                                LocalDate date = LocalDate.parse(monthYear, inputFormatter);
                                String formattedMonthAndYear = outputFormatter_monthAndYear.format(date);
                                String formattedMonthOnly = outputFormatter_monthOnly.format(date);
                                String reportName = "Financial Report (" + formattedMonthOnly + ")";
                                String reportDetail = "Your financial report in " + formattedMonthAndYear;
                                reportModelArrayList.add(new ReportModel(reportName, reportDetail, userID, id));
                            }
                        }
                        ReportAdapter reportAdapter = new ReportAdapter(reportModelArrayList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        reportRV.setLayoutManager(linearLayoutManager);
                        reportRV.setAdapter(reportAdapter);
                    }
                } else {
                    try {
                        String err = response.errorBody().string().replace("\"", "");
                        Log.v("Error code 400",err);
                        Toast.makeText(getApplicationContext(),err,Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Internet connection failure, failed to fetch your reports, try again",Toast.LENGTH_LONG).show();
                Log.d("history",t.toString());
            }
        });
    }
}