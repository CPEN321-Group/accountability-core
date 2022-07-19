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

public class reviewSetting extends RecyclerView.Adapter<reviewSetting.ViewHolder>{
    private List<Review> list;
    public reviewSetting(List<Review> list){
        this.list = list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rate_text;
        private TextView content_text;
        private TextView date_text;
        private TextView title_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            rate_text=itemView.findViewById(R.id.rating_text);
            content_text = itemView.findViewById(R.id.review_content);
            date_text = itemView.findViewById(R.id.date_text);
            title_text = itemView.findViewById(R.id.review_title);
        }
    }
    @NonNull
    @Override
    public reviewSetting.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item,parent,false);
        return new reviewSetting.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = list.get(position);
        holder.title_text.setText(review.getTitle());
        holder.date_text.setText(review.getDate());
        holder.content_text.setText(review.getContent());
        holder.rate_text.setText(review.getRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
