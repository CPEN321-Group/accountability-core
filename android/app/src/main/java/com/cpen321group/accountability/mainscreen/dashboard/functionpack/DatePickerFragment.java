package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;
import android.widget.Toast;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    final long today = System.currentTimeMillis() - 1000;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        //If user tries to select date in past (or today)
        if (calendar.getTimeInMillis() < today)
        {
            //Make them try again
            GoalCreateActivity.year = 0;
            GoalCreateActivity.month = 0;
            GoalCreateActivity.day = 0;
            Toast.makeText(getContext(), "Invalid date, please select the date no earlier than today", Toast.LENGTH_LONG).show();
        }
        else
        {
            //success
            GoalCreateActivity.year = year;
            GoalCreateActivity.month = month+1;
            GoalCreateActivity.day = day;
        }
    }
}