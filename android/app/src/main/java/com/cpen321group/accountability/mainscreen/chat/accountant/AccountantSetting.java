package com.cpen321group.accountability.mainscreen.chat.accountant;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.mainscreen.chat.chatroom.ChattingActivity;
import com.cpen321group.accountability.mainscreen.chat.accountant.history.HistoryActivity;
import com.cpen321group.accountability.mainscreen.chat.accountant.review.ReviewActivity;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountantSetting extends RecyclerView.Adapter<AccountantSetting.ViewHolder>{
    private List<NameID> list;
    private String TAG = "AccountantSetting";

    public AccountantSetting(List<NameID> list){
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView accountant_name;
        TextView accountant_id;
        Button send_button;
        Button history_button;
        Button reviews_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            layout = itemView.findViewById(R.id.account_info);
            accountant_name = itemView.findViewById(R.id.accountant_name);
            accountant_id = itemView.findViewById(R.id.accountantInfo);
            send_button = itemView.findViewById(R.id.request_button_1);
            history_button = itemView.findViewById(R.id.history_button);
            reviews_button=itemView.findViewById(R.id.review_button);
            send_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FrontendConstants.receiverID = accountant_id.getText().toString();
                    send_button.setEnabled(false);
                    postRoomId(send_button,context);
                }
            });

            history_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FrontendConstants.receiverID = accountant_id.getText().toString();
                    FrontendConstants.roomID = null;
                    history_button.setEnabled(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getHistoryRoomID(history_button,context);
                        }
                    },1000);
                }
            });

            reviews_button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    FrontendConstants.receiverID = accountant_id.getText().toString();
                    Intent settingsIntent = new Intent(context, ReviewActivity.class);
                    context.startActivity(settingsIntent);
                }
            });
        }
    }
    @NonNull
    @Override
    public AccountantSetting.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accountant_item,parent,false);
        return new AccountantSetting.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NameID name_id = list.get(position);
        holder.accountant_name.setText(name_id.getName());
        holder.accountant_id.setText(name_id.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void postRoomId(Button send_button, Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/messaging/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.createConversation(FrontendConstants.userID, FrontendConstants.receiverID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG,response.toString());
                getRoomID(send_button,context);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG,t.toString());
                Toast.makeText(context,"Fail to set the room",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRoomID(Button send_button, Context context){
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
                        updateFinish(send_button,context);
                    }
                }catch(Exception e){
                        Log.d("getRoomId",e.toString());
                        send_button.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,"Fail to enter the room",Toast.LENGTH_LONG).show();
                Log.d("getRoomId",t.toString());
                send_button.setEnabled(true);
            }
        });
    }

    private void getHistoryRoomID(Button send_button, Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/messaging/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.findConversation(FrontendConstants.userID, FrontendConstants.receiverID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("getRoomId", response.toString());
                try {
                    if(response.code()==404){
                        Intent settingsIntent = new Intent(context, HistoryActivity.class);
                        context.startActivity(settingsIntent);
                    }
                    if (response.body() != null) {
                        String id = response.body().get("_id").toString();
                        FrontendConstants.roomID = id.substring(1, id.length() - 1);
                        Log.d("getRoomId", id);
                        send_button.setEnabled(true);
                        Intent settingsIntent = new Intent(context, HistoryActivity.class);
                        context.startActivity(settingsIntent);
                    }
                }catch(Exception e){
                    send_button.setEnabled(true);
                    Log.d("getRoomId",e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("getRoomId",t.toString());
            }
        });
    }

    private void updateFinish(Button send_button, Context context){
        if(FrontendConstants.roomID!=null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(FrontendConstants.baseURL + "/messaging/conversation/finished/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<JsonObject> call = retrofitAPI.updateFinished(FrontendConstants.roomID,false);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("updateFinish", "success");
                    send_button.setEnabled(true);
                    if(response.code()==200) {
                        Intent settingsIntent = new Intent(context, ChattingActivity.class);
                        context.startActivity(settingsIntent);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("updateFinish", t.toString());
                    Toast.makeText(context,"Fail to enter the room",Toast.LENGTH_LONG).show();
                    send_button.setEnabled(true);
                }
            });
        }
    }
}
