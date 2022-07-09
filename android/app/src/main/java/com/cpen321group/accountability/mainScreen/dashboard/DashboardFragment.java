package com.cpen321group.accountability.mainScreen.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.AppSettingsActivity;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.GoalSetActivity;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.GoalsAdapter;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.GoalsModel;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.ReportGenActivity;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.SettingsActivity;
import com.cpen321group.accountability.databinding.FragmentDashboardBinding;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.TransactionAdapter;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.TransactionModel;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.TransactionSetActivity;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private TextView dashName;
    private TextView notification_text;
    private TextView spending;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        dashName= binding.dashName;
        dashName.setText("Good Morning, "+ VariableStoration.userName+"!");
        Button settings = binding.homeSettings;
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getActivity(), AppSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        Button reports = binding.reportGenButton;
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportsIntent = new Intent(getActivity(), ReportGenActivity.class);
                startActivity(reportsIntent);
            }
        });
        Button goals = binding.goalButton;
        goals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goalIntent = new Intent(getActivity(), GoalSetActivity.class);
                startActivity(goalIntent);
            }
        });

        Button transactionButton = binding.transactionButton;
        transactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transactionIntent = new Intent(getActivity(), TransactionSetActivity.class);
                startActivity(transactionIntent);
            }
        });

        Button MoreButton = binding.goalbutton;
        MoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goalIntent = new Intent(getActivity(), GoalSetActivity.class);
                startActivity(goalIntent);
            }
        });

        notification_text = binding.notificationText;
        spending = binding.spendingText;

        getAllGoals();
        getAllTransactions();

        return root;
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
                try {
                    ArrayList<JsonObject> jsonArray = response.body();
                    Log.d("User's all goals:", response.toString());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.get(i);
                            String date = jsonObject.get("deadline").getAsString();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date dateGoal = formatter.parse(date);
                                Date a = new Date();
                                int num = (int) ((dateGoal.getTime() - a.getTime()) / (1000 * 3600 * 24));
                                Log.d("date", String.valueOf(num));
                                if (num >= 0 && num < 5) {
                                    notification_text.setText("The Deadline for Goal is Close!");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        notification_text.setText("No Expiring Goal!");
                    }
                }catch(Exception e){
                    
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d("history",t.toString());
            }
        });
    }

    private void getAllTransactions() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/transactions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllTransactions(VariableStoration.userID);

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                try {
                    ArrayList<JsonObject> jsonArray = response.body();
                    double total = 0.0;
                    Log.d("User's all goals:", response.toString());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.get(i);
                            int amount_cents = Integer.valueOf(jsonObject.get("amount").getAsString());
                            double price_dollar = amount_cents / 100.0;
                            total = total + price_dollar;
                        }
                        spending.setText("$ " + String.valueOf(total));
                    } else {
                        spending.setText("$");
                    }
                }catch(Exception e){

                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}