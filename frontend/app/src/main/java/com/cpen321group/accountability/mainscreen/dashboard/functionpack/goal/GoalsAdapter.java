package com.cpen321group.accountability.mainscreen.dashboard.functionpack.goal;

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
import com.cpen321group.accountability.FrontendConstants;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.Viewholder> {

    private ArrayList<GoalsModel> goalsModelArrayList;

    public GoalsAdapter(ArrayList<GoalsModel> goalsModelArrayList) {
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
        holder.dateTxt.setText("Deadline: "+model.getDate());
        holder.goalName.setText(model.getGoal_name().replace("\"", ""));
        holder.goalPrice.setText("Save: $" + model.getCurrent_saving() + " | Target: $" + model.getGoal_price());
        holder.goalDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goalIdRaw = model.getGoal_id();
                String userId = model.getUser_id();
                String goalId = goalIdRaw.replace("\"", "");
                Log.d(userId, goalId);
                deleteGoal(userId, goalId, v, holder);
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

    private void deleteGoal(String userId, String GoalId, View view, GoalsAdapter.Viewholder holder){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/goals/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> call = retrofitAPI.deleteGoal(userId, GoalId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(view.getContext(),"You have successfully deleted your selected goal",Toast.LENGTH_LONG).show();
                Log.d("Delete", "success");
                goalsModelArrayList.remove(holder.getAdapterPosition());  // remove the item from list
                notifyItemRemoved(holder.getAdapterPosition());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(view.getContext(),"Failed to delete your selected goal, you may try again",Toast.LENGTH_LONG).show();
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
        private TextView goalName;
        private TextView goalPrice;
        private Button goalDelete;
        private Button goalSave;
        private TextView dateTxt;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            goalName = itemView.findViewById(R.id.goalName);
            goalPrice = itemView.findViewById(R.id.goalPrice);
            goalDelete = itemView.findViewById(R.id.goalDelete);
            goalSave = itemView.findViewById(R.id.goalSave);
            dateTxt = itemView.findViewById(R.id.goalDateTxt);
        }
    }

}