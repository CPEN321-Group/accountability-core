package com.cpen321group.accountability.mainScreen.dashboard.functionpack;

import android.content.Context;
import android.content.Intent;
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
        holder.goalName.setText(model.getGoal_name().replace("\"", ""));
        holder.goalPrice.setText("Save: $" + model.getCurrent_saving() + " | Target: $" + model.getGoal_price());
        holder.goalDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalIdRaw = model.getGoal_id();
                String userId = model.getUser_id();
                String goalId = goalIdRaw.replace("\"", "");
                Log.d(userId, goalId);
                deleteGoal(userId, goalId, v);
            }
        });
        holder.goalSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String goalIdRaw = model.getGoal_id();
                String goalId = goalIdRaw.replace("\"", "");
                Intent goalUpdateIntent = new Intent(view.getContext(), GoalUpdateActivity.class);
                goalUpdateIntent.putExtra("goalId", goalId);
                view.getContext().startActivity(goalUpdateIntent);
            }
        });
    }

    private void deleteGoal(String userId, String GoalId, View view){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/goals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.deleteSpecificGoals(userId, GoalId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Toast.makeText(view.getContext(),"You have successfully deleted your selected goal",Toast.LENGTH_LONG).show();
                Log.d("Delete", "success");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //Toast.makeText(view.getContext(),"Failed to delete your selected goal, you may try again",Toast.LENGTH_LONG).show();
                Log.d("err", t.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return goalsModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView goalName, goalPrice;
        private Button goalDelete, goalSave;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            goalName = itemView.findViewById(R.id.goalName);
            goalPrice = itemView.findViewById(R.id.goalPrice);
            goalDelete = itemView.findViewById(R.id.goalDelete);
            goalSave = itemView.findViewById(R.id.goalSave);
        }
    }

}