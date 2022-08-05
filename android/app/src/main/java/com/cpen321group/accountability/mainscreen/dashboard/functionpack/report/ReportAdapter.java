package com.cpen321group.accountability.mainscreen.dashboard.functionpack.report;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        if(FrontendConstants.isAccountant){
            holder.userId.setText("User Id: "+model.getUser_id());
            holder.reportDelete.setVisibility(View.GONE);
        }else{
            holder.userId.setVisibility(View.GONE);
        }
        holder.reportName.setText(model.getReport_name());
        holder.reportDetail.setText(model.getReport_detail());
        holder.reportClickableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reportId = model.getReport_id();
                Intent reportDisplayIntent = new Intent(view.getContext(), ReportDisplayActivity.class);
                reportDisplayIntent.putExtra("reportId", reportId.replace("\"", ""));
                reportDisplayIntent.putExtra("userID",model.getUser_id());
                view.getContext().startActivity(reportDisplayIntent);
            }
        });
        holder.reportDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FrontendConstants.userID;
                String reportId = model.getReport_id().replace("\"", "");
                Log.d("{userId, reportId} to be deleted: ", userId + ", " + reportId);
                deleteSpecificReport(userId, reportId, view, holder);
            }
        });
    }

    private void deleteSpecificReport(String userId, String reportId, View view, Viewholder holder) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reports/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<ResponseBody> call = retrofitAPI.deleteReport(userId, reportId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    Toast.makeText(view.getContext(), "You have successfully deleted your selected report", Toast.LENGTH_LONG).show();
                    Log.d("Delete", "success");
                    reportModelArrayList.remove(holder.getAdapterPosition());  // remove the item from list
                    notifyItemRemoved(holder.getAdapterPosition());
                } else if (response.code() == 400) {
                    String err = null;
                    try {
                        err = response.errorBody().string().replace("\"", "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.v("Error code 400",err);
                    Toast.makeText(view.getContext(),err,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(),"Failed to delete your selected report, you may try again",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(view.getContext(),"Failed to delete your selected report, you may try again",Toast.LENGTH_LONG).show();
                Log.d("err", t.toString());
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
        private TextView userId;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            reportName = itemView.findViewById(R.id.reportName);
            reportDetail = itemView.findViewById(R.id.reportDetail);
            reportDelete = itemView.findViewById(R.id.reportDelete);
            reportClickableCard = itemView.findViewById(R.id.reportCard);
            userId = itemView.findViewById(R.id.id_text);
        }
    }
}
