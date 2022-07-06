package com.cpen321group.accountability;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.Viewholder> {

    private Context context;
    private ArrayList<GoalsModel> goalsModelArrayList;

    public GoalsAdapter(Context context, ArrayList<GoalsModel> goalsModelArrayList) {
        this.context = context;
        this.goalsModelArrayList = goalsModelArrayList;
    }

    @NonNull
    @Override
    public GoalsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalsAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        GoalsModel model = goalsModelArrayList.get(position);
        holder.goalName.setText(model.getGoal_name());
        holder.goalPrice.setText("$" + model.getGoal_price());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return goalsModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView goalName, goalPrice;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            goalName = itemView.findViewById(R.id.goalName);
            goalPrice = itemView.findViewById(R.id.goalPrice);
        }
    }
}