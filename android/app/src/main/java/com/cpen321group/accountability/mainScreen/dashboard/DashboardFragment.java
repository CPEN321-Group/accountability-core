package com.cpen321group.accountability.mainScreen.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cpen321group.accountability.VariableStoration;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.GoalSetActivity;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.ReportGenActivity;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.SettingsActivity;
import com.cpen321group.accountability.databinding.FragmentDashboardBinding;
import com.cpen321group.accountability.mainScreen.dashboard.functionpack.TransactionSetActivity;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private TextView dashName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textDashboard;
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        dashName= binding.dashName;
        dashName.setText("Good Morning, "+ VariableStoration.userName+"!");
        Button settings = binding.homeSettings;
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });
        Button reports = binding.reportGenButton;
        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportsIntent = new Intent(getActivity(), ReportGenActivity.class);
                startActivity(reportsIntent);
            }
        });
        Button goals = binding.goalButton;
        goals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goalIntent = new Intent(getActivity(), GoalSetActivity.class);
                startActivity(goalIntent);
            }
        });

        Button transactionButton = binding.transactionButton;
        transactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent transactionIntent = new Intent(getActivity(), TransactionSetActivity.class);
                startActivity(transactionIntent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}