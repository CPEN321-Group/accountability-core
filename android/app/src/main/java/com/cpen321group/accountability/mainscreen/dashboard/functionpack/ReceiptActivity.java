package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.cpen321group.accountability.R;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        String infoString = getIntent().getStringExtra("ocr");
        TextView ocr = findViewById(R.id.receiptText);
        ocr.setText(infoString);
    }
}