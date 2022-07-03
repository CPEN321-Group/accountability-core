package com.cpen321group.accountability.mainScreen.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.MainActivity;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private List<String> userList = new ArrayList<>();
    private RecyclerView userRecyclerView;
    private LinearLayoutManager layoutManager;
    private requestSetting adapter;
    private accountantSetting adapter_user;
    private String TAG = "Chat";

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
        adapter = new requestSetting(userList = getData());
        userRecyclerView.setLayoutManager(layoutManager);

        adapter_user = new accountantSetting(userList = getData());
        if(MainActivity.isAccountant == true){
            userRecyclerView.setAdapter(adapter);
        }else{
            userRecyclerView.setAdapter(adapter_user);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<String> getData(){
        List<String> list = new ArrayList<>();
        list.add(new String("David"));
        return list;
    }
}