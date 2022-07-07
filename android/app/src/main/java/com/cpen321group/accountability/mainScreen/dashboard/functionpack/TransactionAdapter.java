package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;

import java.util.ArrayList;

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