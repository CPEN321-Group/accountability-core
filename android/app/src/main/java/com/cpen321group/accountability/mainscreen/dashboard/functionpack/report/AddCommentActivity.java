package com.cpen321group.accountability.mainscreen.dashboard.functionpack.report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.R;
import com.cpen321group.accountability.RetrofitAPI;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddCommentActivity extends AppCompatActivity {
    private String reportId;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        EditText comment = findViewById(R.id.commentInput);

        Bundle extras = getIntent().getExtras();
        reportId = extras.getString("reportId");
        userId = extras.getString("userID");

        Button commentButton = findViewById(R.id.commentUpdateButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getEditableText() != null) {
                    updateRecommendation(comment.getEditableText().toString().trim());
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Content", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateRecommendation(String str){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FrontendConstants.baseURL + "/reports/users/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<JsonObject> call = retrofitAPI.updateSpecificReport(userId,reportId,str);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.code()==200) {
                    Toast.makeText(getApplicationContext(), "You have successfully add comments", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), ReportDisplayActivity.class);
                    intent.putExtra("reportId", reportId);
                    intent.putExtra("userID",userId);
                    startActivity(intent);
                }
                Log.d("Message",response.toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed to add comment",Toast.LENGTH_LONG).show();
                Log.d("Err",t.toString());
            }
        });
    }
}