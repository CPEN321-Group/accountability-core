package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.R;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.Viewholder> {
    private ArrayList<ReportModel> reportModelArrayList;

    public ReportAdapter(ArrayList<ReportModel> reportAdapterArrayList) {
        this.reportModelArrayList = reportAdapterArrayList;
    }

    @NonNull
    @Override
    public ReportAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout_report, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.Viewholder holder, int position) {
        ReportModel model = reportModelArrayList.get(position);
        holder.reportName.setText(model.getReport_name());
        holder.reportDetail.setText(model.getReport_detail());
        holder.reportDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Card touched.", Toast.LENGTH_LONG).show();
            }
        });
        holder.reportDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"delete button toched",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportModelArrayList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{
        private TextView reportName;
        private TextView reportDetail;
        private Button reportDelete;
        private CardView reportClickableCard;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            reportName = itemView.findViewById(R.id.reportName);
            reportDetail = itemView.findViewById(R.id.reportDetail);
            reportDelete = itemView.findViewById(R.id.reportDelete);
            reportClickableCard = itemView.findViewById(R.id.reportCard);
        }
    }
}
