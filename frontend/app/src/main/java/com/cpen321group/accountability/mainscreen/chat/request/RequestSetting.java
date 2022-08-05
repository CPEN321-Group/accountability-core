package com.cpen321group.accountability.mainscreen.chat.request;

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
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.mainscreen.chat.chatroom.ChattingActivity;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestSetting extends RecyclerView.Adapter<RequestSetting.ViewHolder>{
    private List<String> list;
    public RequestSetting(List<String> list){
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
                    FrontendConstants.receiverID = user_id.getText().toString();
                    request_button.setEnabled(false);
                    finish_button.setEnabled(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getRoomID(request_button,finish_button,context,0,0);
                        }
                    },1000);
                }
            });
        }
    }
    @NonNull
    @Override
    public RequestSetting.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new RequestSetting.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestSetting.ViewHolder holder, int position) {
        String name = list.get(position);
        holder.user_id.setText(name);
        holder.user_name_text.setText("User");
        holder.finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.request_button.setEnabled(false);
                holder.finish_button.setEnabled(false);
                FrontendConstants.receiverID = holder.user_id.getText().toString();
                getRoomID(holder.request_button,holder.finish_button, v.getContext(),1,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void getRoomID(Button request_button, Button finish_button,Context context, int code,int pos){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/messaging/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.findConversation(FrontendConstants.userID, FrontendConstants.receiverID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.body() != null) {
                        String id = response.body().get("_id").toString();
                        FrontendConstants.roomID = id.substring(1, id.length() - 1);
                        Log.d("getRoomId", id);
                        if(code==1){
                            updateFinish(request_button,finish_button,pos);
                        }else if(code==0){
                            request_button.setEnabled(true);
                            finish_button.setEnabled(true);
                            Intent settingsIntent = new Intent(context, ChattingActivity.class);
                            context.startActivity(settingsIntent);
                        }else{
                            request_button.setEnabled(true);
                            finish_button.setEnabled(true);
                        }
                    }else{
                        request_button.setEnabled(true);
                        finish_button.setEnabled(true);
                    }
                }catch(Exception e){
                    request_button.setEnabled(true);
                    finish_button.setEnabled(true);
                    Log.d("getRoomId",e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                request_button.setEnabled(true);
                finish_button.setEnabled(true);
                Log.d("getRoomId",t.toString());
            }
        });
    }



    private void updateFinish(Button request_button,Button finish_button,int pos){
        if(FrontendConstants.roomID!=null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FrontendConstants.baseURL + "/messaging/conversation/finished/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<JsonObject> call = retrofitAPI.updateFinished(FrontendConstants.roomID,true);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("updateFinish", "success");
                    request_button.setEnabled(true);
                    finish_button.setEnabled(true);
                    if(response.code()==200) {
                        list.remove(pos);  // remove the item from list
                        notifyItemRemoved(pos);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    request_button.setEnabled(true);
                    finish_button.setEnabled(true);
                    Log.d("updateFinish", t.toString());
                }
            });
        }
    }
}