package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionSetActivity extends AppCompatActivity {
    private RecyclerView transactionsRV;

    // Arraylist for storing data
    private ArrayList<TransactionModel> transactionModelArrayList;

    // disable system return button
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Not calling **super**, disables back button in current screen.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_set);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.topAppBar_transaction);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        transactionsRV = findViewById(R.id.transactionRV);
        getAllTransactions();

        FloatingActionButton createXtendButton = (FloatingActionButton)findViewById(R.id.floating_action_button_transaction);
        createXtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goalCreateIntent = new Intent(TransactionSetActivity.this, TransactionCreateActivity.class);
                startActivity(goalCreateIntent);
            }
        });
    }

//    transactionModelArrayList = new ArrayList<>();
//    transactionsRV = findViewById(R.id.transactionRV);
//        transactionModelArrayList.add(new TransactionModel("","","test","testcat", "2021/04/02", "56", true, ""));
//    TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactionModelArrayList);
//    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        transactionsRV.setLayoutManager(linearLayoutManager);
//        transactionsRV.setAdapter(transactionAdapter);

    private void getAllTransactions() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/transactions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllTransactions(FrontendConstants.userID);

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                ArrayList<JsonObject> jsonArray = response.body();
                Log.d("User's all goals:",response.toString());
                if(jsonArray!=null) {
                    transactionModelArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i);
                        String title = jsonObject.get("title").toString();
                        String id = jsonObject.get("_id").toString();
                        String category = jsonObject.get("category").toString();
                        int amount_cents = Integer.valueOf(jsonObject.get("amount").toString());
                        double price_dollar = amount_cents / 100.0;
                        String date = jsonObject.get("date").toString();
                        transactionModelArrayList.add(new TransactionModel(FrontendConstants.userID, id, title, category, date, price_dollar, false, "null"));
                        TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactionModelArrayList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        transactionsRV.setLayoutManager(linearLayoutManager);
                        transactionsRV.setAdapter(transactionAdapter);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"You don't have any transaction records.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {

            }
        });

    }
}