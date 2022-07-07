package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.VariableStoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        FloatingActionButton createXtendButton = (FloatingActionButton)findViewById(R.id.floating_action_button_transaction);
        createXtendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goalCreateIntent = new Intent(TransactionSetActivity.this, TransactionCreateActivity.class);
                startActivity(goalCreateIntent);
            }
        });
    }
}