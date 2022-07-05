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
import com.cpen321group.accountability.VariableStoration;

import java.util.List;

public class requestSetting extends RecyclerView.Adapter<requestSetting.ViewHolder>{

    private List<String> list;
    public requestSetting(List<String> list){
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView user_name;
        Button request_button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            layout = itemView.findViewById(R.id.user_request);
            user_name = itemView.findViewById(R.id.user_text);
            request_button = itemView.findViewById(R.id.button_accept);
            request_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariableStoration.receiverID = user_name.getText().toString();
                    Intent settingsIntent = new Intent(context, ChattingActivity.class);
                    context.startActivity(settingsIntent);
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
        holder.user_name.setText(name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
