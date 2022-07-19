package com.cpen321group.accountability.mainScreen.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.google.android.material.color.DynamicColors;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChattingActivity extends AppCompatActivity {

    private List<Msg> msgList = new ArrayList<>();
    private RecyclerView msgRecyclerView;
    private EditText inputText;
    private Button send;
    private LinearLayoutManager layoutManager;
    private MsgSetting adapter;
    private Socket mSocket;
    private String TAG = "Chatting";
    private String roomName = "1";
    private List<Msg> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        if (VariableStoration.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //Starting of this activity
        getData();
        msgRecyclerView = findViewById(R.id.msg_view);
        inputText = findViewById(R.id.text_view);
        send = findViewById(R.id.send_button);
        layoutManager = new LinearLayoutManager(this);
        adapter = new MsgSetting(msgList);
        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setAdapter(adapter);

        try {
            //This address is the way you can connect to localhost with AVD(Android Virtual Device)
            mSocket = IO.socket("http://20.239.52.70:8000/");
            //Log.d("success", mSocket.id());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("fail", "Failed to connect");
        }
        mSocket.connect();
        mSocket.emit("addUser",VariableStoration.userID);
        //Register all the listener and callbacks here.
        mSocket.on("getMessage", onNewMessage);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!content.equals("")) {
                    msgList.add(new Msg(content,Msg.TYPE_SEND));
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                    mSocket.emit("sendMessage",VariableStoration.userID,VariableStoration.receiverID,content);
                    postMessage(content);
                }
            }
        });
    }

        private Emitter.Listener onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                ChattingActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String username;
                        String message;
                        try {
                            username = data.getString("userId");
                            message = data.getString("text");
                        } catch (JSONException e) {
                            return;
                        }
                        Log.d("Socket",username);
                        Log.d("Socket",message);
                        if(username.equals(VariableStoration.receiverID)) {
                            msgList.add(new Msg(message, Msg.TYPE_RECEIVED));
                            adapter.notifyItemInserted(msgList.size()-1);
                            msgRecyclerView.scrollToPosition(msgList.size()-1);
                        }
                    }
                });
            }
        };

    private void getData(){
        if (VariableStoration.roomID != null) {
            getHistory();
        } else {
            msgList.add(new Msg("Hello", Msg.TYPE_RECEIVED));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("getMessage", onNewMessage);
    }

    private void postMessage(String text){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/messaging/message/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.postMessage(VariableStoration.roomID,VariableStoration.userID,text);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("postMessage",t.toString());
            }
        });
    }

    private void getHistory(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/messaging/message/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllMessage(VariableStoration.roomID);

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
                            if (hisId.substring(1, hisId.length() - 1).equals(VariableStoration.userID)) {
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
                }catch (Exception e){
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