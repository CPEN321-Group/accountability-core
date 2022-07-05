package com.cpen321group.accountability.mainScreen.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.HomeScreenActivity;
import com.cpen321group.accountability.RetrofitAPI;
import com.cpen321group.accountability.VariableStoration;
import com.cpen321group.accountability.databinding.FragmentChatBinding;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private List<String> userList = new ArrayList<>();
    private RecyclerView userRecyclerView;
    private LinearLayoutManager layoutManager;
    private requestSetting adapter;
    private accountantSetting adapter_user;
    private String TAG = "Chat";
    //private List<String> accountList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        userRecyclerView = binding.chatRecycler;

        layoutManager = new LinearLayoutManager(getActivity());

        userRecyclerView.setLayoutManager(layoutManager);

        adapter_user = new accountantSetting(userList = getData());
        if(VariableStoration.isAccountant){
            adapter = new requestSetting(userList = getData());
            userRecyclerView.setAdapter(adapter);
        }else{
            adapter_user = new accountantSetting(userList = getData());
            userRecyclerView.setAdapter(adapter_user);
        }


        if(GoogleSignIn.getLastSignedInAccount(getActivity())!=null){
            GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());
            VariableStoration.userID = account.getId()+"go";
        }else{
            Profile profile = Profile.getCurrentProfile();
            VariableStoration.userID = profile.getId()+"fb";
        }

        return root;
    }

    private void getAccountant(List<String> accountList) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/accounts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAccountant();

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                ArrayList<JsonObject> jsonArray = response.body();
                for(int i=0;i<jsonArray.size();i++){
                    JsonObject jsonObject = jsonArray.get(i);
                    Log.d("Message",jsonObject.get("accountId").toString());
                    String string = jsonObject.get("accountId").toString();
                    accountList.add(string.substring(1,string.length()-1));
                    adapter_user.notifyItemInserted(accountList.size()-1);
                    userRecyclerView.scrollToPosition(accountList.size()-1);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d("Message", t.toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<String> getData(){
        List<String> list = new ArrayList<>();
        //list.add(new String("100141214588378665776go"));
        if(!VariableStoration.isAccountant){
            getAccountant(list);
        }else{
            getUser(list);
        }
        return list;
    }

    private void getUser(List<String> accountList) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://20.239.52.70:8000/messaging/conversation/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ArrayList<JsonObject>> call = retrofitAPI.getAllUsers(VariableStoration.userID);

        call.enqueue(new Callback<ArrayList<JsonObject>>() {
            @Override
            public void onResponse(Call<ArrayList<JsonObject>> call, Response<ArrayList<JsonObject>> response) {
                ArrayList<JsonObject> jsonArray = response.body();
                if(jsonArray!=null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject jsonObject = jsonArray.get(i);
                        Log.d("Message", jsonObject.get("members").toString());
                        String string = jsonObject.get("members").toString();
                        String bool = jsonObject.get("isFinished").toString();
                        Log.d("isFinished",bool);
                        if(bool.equals("false")){
                            String[] array = string.split(",", 2);
                            String s1 = array[0].substring(2, array[0].length() - 1);
                            String s2 = array[1].substring(1, array[1].length() - 2);
                            if (s1.equals(VariableStoration.userID)) {
                                accountList.add(s2);
                                adapter_user.notifyItemInserted(accountList.size() - 1);
                                userRecyclerView.scrollToPosition(accountList.size() - 1);
                            } else {
                                accountList.add(s1);
                                adapter_user.notifyItemInserted(accountList.size() - 1);
                                userRecyclerView.scrollToPosition(accountList.size() - 1);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<JsonObject>> call, Throwable t) {
                Log.d("Message", t.toString());
            }
        });
    }
}