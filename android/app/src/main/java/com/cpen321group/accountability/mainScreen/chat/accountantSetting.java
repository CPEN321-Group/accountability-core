package com.cpen321group.accountability.mainScreen.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class accountantSetting extends RecyclerView.Adapter<accountantSetting.ViewHolder>{
    private List<String> list;

    public accountantSetting(List<String> list){
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView accountant_name;
        Button send_button;
        Button history_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            layout = itemView.findViewById(R.id.account_info);
            accountant_name = itemView.findViewById(R.id.accountant_name);
            send_button = itemView.findViewById(R.id.request_button_1);
            history_button = itemView.findViewById(R.id.history_button);
            send_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariableStoration.receiverID = accountant_name.getText().toString();
                    postRoomId();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRoomID();
                        }
                    },1000);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateFinish();
                            Intent settingsIntent = new Intent(context, ChattingActivity.class);
                            context.startActivity(settingsIntent);
                        }
                    },3000);
                }
            });

            history_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariableStoration.receiverID = accountant_name.getText().toString();
                    postRoomId();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRoomID();
                        }
                    },1000);
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //updateFinish();
                            Intent settingsIntent = new Intent(context, HistoryActivity.class);
                            context.startActivity(settingsIntent);
                        }
                    },3000);
                }
            });
        }
    }
    @NonNull
    @Override
    public accountantSetting.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accountant_item,parent,false);
        return new accountantSetting.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = list.get(position);
        holder.accountant_name.setText(name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void postRoomId(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/messaging/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.postRoomId(VariableStoration.userID,VariableStoration.receiverID);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("postRoom",t.toString());
            }
        });
    }

    private void getRoomID(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/messaging/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.getRoomId(VariableStoration.userID,VariableStoration.receiverID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body()!=null) {
                    String id = response.body().get("_id").toString();
                    VariableStoration.roomID = id.substring(1, id.length() - 1);
                    Log.d("Message", id);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("getRoomId",t.toString());
            }
        });
    }

    private void updateFinish(){
        if(VariableStoration.roomID!=null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://20.239.52.70:8000/messaging/conversation/finished/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<String> call = retrofitAPI.updateFinished(VariableStoration.roomID,false);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d("Message", "success");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Message", t.toString());
                }
            });
        }
    }
}
