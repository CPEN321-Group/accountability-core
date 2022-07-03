package com.cpen321group.accountability.mainScreen.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;

import java.util.List;

public class accountantSetting extends RecyclerView.Adapter<accountantSetting.ViewHolder>{
    private List<String> list;

    public accountantSetting(List<String> list){
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView accountant_name;
        Button send_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            layout = itemView.findViewById(R.id.account_info);
            accountant_name = itemView.findViewById(R.id.accountant_name);
            send_button = itemView.findViewById(R.id.request_button_1);
            send_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent settingsIntent = new Intent(context, ChattingActivity.class);
                    context.startActivity(settingsIntent);
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
}
