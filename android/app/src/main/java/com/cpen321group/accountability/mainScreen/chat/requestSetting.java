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

public class requestSetting extends RecyclerView.Adapter<requestSetting.ViewHolder>{
    private String name;
    private List<String> list;
    public requestSetting(List<String> list){
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView user_id;
        TextView user_name_text;
        Button request_button;
        Button finish_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            layout = itemView.findViewById(R.id.user_request);
            user_id = itemView.findViewById(R.id.user_text);
            user_name_text = itemView.findViewById(R.id.user_name_text);
            request_button = itemView.findViewById(R.id.button_accept);
            finish_button = itemView.findViewById(R.id.button_finish);
            request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariableStoration.receiverID = user_id.getText().toString();
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
                            Intent settingsIntent = new Intent(context, ChattingActivity.class);
                            context.startActivity(settingsIntent);
                        }
                    },3000);
                }
            });

            finish_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariableStoration.receiverID = user_id.getText().toString();
                    getRoomID();
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           updateFinish();
                        }
                    },1500);
                }
            });
        }
    }
    @NonNull
    @Override
    public requestSetting.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new requestSetting.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull requestSetting.ViewHolder holder, int position) {
        String name = list.get(position);
        holder.user_id.setText(name);
        holder.user_name_text.setText("User");
    }

    @Override
    public int getItemCount() {
        return list.size();
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
                try {
                    if (response.body() != null) {
                        String id = response.body().get("_id").toString();
                        VariableStoration.roomID = id.substring(1, id.length() - 1);
                        Log.d("getRoomId", id);
                    }
                }catch(Exception e){
                    Log.d("getRoomId",e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("getRoomId",t.toString());
            }
        });
    }

    private String getName(String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/accounts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.getAccount(VariableStoration.userID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.body()!=null) {
                    name = response.body().get("firstname").toString();
                    Log.d("Message", name);
                }else{
                    name = "User";
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Message",t.toString());
                name = "User";
            }
        });
        return name;
    }

    private void updateFinish(){
        if(VariableStoration.roomID!=null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://20.239.52.70:8000/messaging/conversation/finished/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<String> call = retrofitAPI.updateFinished(VariableStoration.roomID,true);

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
