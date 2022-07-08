package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.Viewholder>{

    private Context context;
    private ArrayList<TransactionModel> transactionModelArrayList;

    public TransactionAdapter(Context context,ArrayList<TransactionModel> transactionModelArrayList) {
        this.context = context;
        this.transactionModelArrayList = transactionModelArrayList;
    }

    @NonNull
    @Override
    public TransactionAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_transaction, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.Viewholder holder, int position) {
        TransactionModel model = transactionModelArrayList.get(position);
        holder.transactionName.setText(model.getTransaction_title().replace("\"", ""));
        holder.transactionDetails.setText("Category: " + model.getTransaction_category() + " | Amount: " + model.getTransaction_cents());
        holder.deleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transactionIdRaw = model.getTransaction_id();
                String transactionId = transactionIdRaw.replace("\"", "");
                String userId = model.getUser_id();
                Log.d ("UserId + TransactionId", userId+" "+transactionId);
                deleteTransaction(userId, transactionId, view);
            }
        });
    }

    private void deleteTransaction(String userId, String transactionId, View view) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/transactions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.deleteSpecificTransaction(userId, transactionId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Toast.makeText(view.getContext(),"You have successfully deleted your selected transaction",Toast.LENGTH_LONG).show();
                Log.d("Delete", "success");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //Toast.makeText(view.getContext(),"Failed to delete your selected transaction, you may try again",Toast.LENGTH_LONG).show();
                Log.d("Delete", "failed");
            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView transactionName, transactionDetails;
        private Button deleteTransaction;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            transactionName = itemView.findViewById(R.id.transactionName);
            transactionDetails = itemView.findViewById(R.id.transactionDetail);
            deleteTransaction = itemView.findViewById(R.id.transactionDelete);
        }
    }
}