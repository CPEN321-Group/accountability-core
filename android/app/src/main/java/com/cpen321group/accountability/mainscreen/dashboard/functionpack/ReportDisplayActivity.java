package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.reportpiechart.PieClickListener;
import com.cpen321group.accountability.reportpiechart.PieEntry;
import com.cpen321group.accountability.reportpiechart.ReportPieChart;

import java.util.ArrayList;

public class ReportDisplayActivity extends AppCompatActivity implements PieClickListener {
    private ReportPieChart reportPieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_display);

        //Starting of this activity
        reportPieChart = (ReportPieChart) findViewById(R.id.piechart);
        reportPieChart.setRadiusDefault(ReportPieChart.dp2px(this, 80));
        reportPieChart.setPieClickListener(this);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(1, R.color.chart_orange, true, "item1"));
        pieEntries.add(new PieEntry(1, R.color.chart_green, false,"item2"));
        pieEntries.add(new PieEntry(1, R.color.chart_blue, false, "item3"));
        pieEntries.add(new PieEntry(1, R.color.chart_purple, false, "item3"));
        pieEntries.add(new PieEntry(1, R.color.chart_mblue, false, "item4"));
        reportPieChart.setPieEntries(pieEntries);
    }
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Touched" + position, Toast.LENGTH_SHORT).show();
    }
}