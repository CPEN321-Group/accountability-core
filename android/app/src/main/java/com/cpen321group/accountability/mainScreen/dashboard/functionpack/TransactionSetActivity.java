package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.VariableStoration;

import java.util.ArrayList;

public class TransactionSetActivity extends AppCompatActivity {
    private RecyclerView transactionsRV;

    // Arraylist for storing data
    private ArrayList<TransactionModel> transactionModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_set);
        transactionModelArrayList = new ArrayList<>();
        transactionsRV = findViewById(R.id.transactionRV);
        transactionModelArrayList.add(new TransactionModel("","","test","testcat", "2021/04/02", "56", true, ""));
        TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactionModelArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        transactionsRV.setLayoutManager(linearLayoutManager);
        transactionsRV.setAdapter(transactionAdapter);
    }
}