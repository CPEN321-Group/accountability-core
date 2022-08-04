package com.cpen321group.accountability.mainscreen.dashboard.functionpack.report;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen321group.accountability.R;

public class CommentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Bundle extras = getIntent().getExtras();
        String comment = extras.getString("recommendation");

        if(!comment.equals(" ")){
            TextView txt = findViewById(R.id.commentText);
            txt.setText(comment);
        }else{
            Toast.makeText(this,"No Comment",Toast.LENGTH_LONG).show();
        }

    }
}