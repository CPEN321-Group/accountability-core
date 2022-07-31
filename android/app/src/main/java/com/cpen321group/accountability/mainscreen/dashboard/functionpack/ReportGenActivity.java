package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;

import android.os.Bundle;
import android.widget.Toast;

import com.cpen321group.accountability.R;
import com.cpen321group.accountability.FrontendConstants;
import com.cpen321group.accountability.reportpiechart.PieClickListener;
import com.cpen321group.accountability.reportpiechart.PieEntry;
import com.cpen321group.accountability.reportpiechart.ReportPieChart;
import com.google.android.material.color.DynamicColors;

import java.util.ArrayList;
import java.util.List;

public class ReportGenActivity extends AppCompatActivity implements PieClickListener {

    private ReportPieChart reportPieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set Navigation Bar transparent
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_gen);
        if (FrontendConstants.is_darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //Starting of this activity
        reportPieChart = (ReportPieChart) findViewById(R.id.piechart);
        reportPieChart.setRadiusDefault(ReportPieChart.dp2px(this, 110));
        reportPieChart.setPieClickListener(this);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(1, R.color.chart_orange, true, "text1"));
        pieEntries.add(new PieEntry(1, R.color.chart_green, false, "text2"));
        pieEntries.add(new PieEntry(1, R.color.chart_blue, false, "text3"));
        pieEntries.add(new PieEntry(1, R.color.chart_purple, false, "text4"));
        pieEntries.add(new PieEntry(1, R.color.chart_mblue, false, "text5"));
        reportPieChart.setPieEntries(pieEntries);
    }
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Touched" + position, Toast.LENGTH_SHORT).show();
    }
}