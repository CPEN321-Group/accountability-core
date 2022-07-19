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

public class TransactionCreateActivity extends AppCompatActivity {
    private String transactionName;
    private String transactionCategory;
    private int transactionAmount;
    public static int year;
    public static int month;
    public static int day;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_create);
        Button createTransaction = findViewById(R.id.transactionCreateButton);
        createTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText transactionNameEditText = (TextInputEditText) findViewById(R.id.transactionNameInput);
                transactionName = transactionNameEditText.getText().toString();

                TextInputEditText transactionCategoryEditText = (TextInputEditText) findViewById(R.id.transactionCategoryInput);
                transactionCategory = transactionCategoryEditText.getText().toString();

                TextInputEditText transactionAmountEditText = (TextInputEditText) findViewById(R.id.transactionAmountPriceInput);
                String TransactionAmountText = transactionAmountEditText.getText().toString();

                date = year + "/" + month + "/" + day;
                Log.d("Date:", "" + date);

                if(!date.equals("") && !transactionName.equals("") && !TransactionAmountText.equals("")) {
                    transactionAmount = (int)Math.round((Double.parseDouble(TransactionAmountText)*100));
                    try {
                        createTransaction();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent TransactionSetIntent = new Intent(TransactionCreateActivity.this, TransactionSetActivity.class);
                            startActivity(TransactionSetIntent);
                        }
                    },2000);
                }else{
                    Toast.makeText(TransactionCreateActivity.this,"Some necessary information missing!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerTransactionFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void createTransaction() throws IOException{
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VariableStoration.baseURL + "/transactions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.postTransaction(VariableStoration.userID, this.transactionName, this.transactionCategory, this.date, this.transactionAmount, false, "null");

        Log.d("API url:", VariableStoration.baseURL + "/transactions/"+VariableStoration.userID+"/");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Toast.makeText(getApplicationContext(),"You have successfully added your new transaction",Toast.LENGTH_LONG).show();
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to add your new transaction, try again",Toast.LENGTH_LONG).show();
                Log.d("Message","error");
            }
        });
    }
}