package com.cpen321group.accountability.mainscreen.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariablesSpace;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryActivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private MsgSetting adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if (VariablesSpace.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //Starting of this activity
        getData();
        msgRecyclerView = findViewById(R.id.historyView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new MsgSetting(msgList);
        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setAdapter(adapter);
    }

    private void getData(){
        if (VariablesSpace.roomID != null) {
            getHistory();
        } else {
            msgList.add(new Msg("Hello", Msg.TYPE_RECEIVED));
        }
    }

    private void getHistory(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VariablesSpace.baseURL + "/messaging/message/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllMessage(VariablesSpace.roomID);

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                try {
                    ArrayList<JsonObject> jsonArray = response.body();
                    Log.d("history", response.toString());
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonObject jsonObject = jsonArray.get(i);
                            Log.d("history", jsonObject.get("text").toString());
                            String string = jsonObject.get("text").toString();
                            String hisId = jsonObject.get("sender").toString();
                            Log.d("hisId", hisId);
                            if (hisId.substring(1, hisId.length() - 1).equals(VariablesSpace.userID)) {
                                msgList.add(new Msg(string.substring(1, string.length() - 1), Msg.TYPE_SEND));
                                adapter.notifyItemInserted(msgList.size() - 1);
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                            } else {
                                msgList.add(new Msg(string.substring(1, string.length() - 1), Msg.TYPE_RECEIVED));
                                adapter.notifyItemInserted(msgList.size() - 1);
                                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                            }
                        }
                    }
                }catch(Exception e){
                    Log.d("history",e.toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d("history",t.toString());
            }
        });
    }
}