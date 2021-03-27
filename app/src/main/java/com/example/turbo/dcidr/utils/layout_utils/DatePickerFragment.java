package com.example.turbo.dcidr.utils.layout_utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Turbo on 4/8/2016.
 */
public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

    private DatePickerDialog mDatePickerDialog;
    private String mTitle;
    private View mCustomTitleView;
    private DatePickerOnDateSetInterface mDatePickerOnDateSetInterface;
    public DatePickerFragment(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        mDatePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        if(mTitle != null) {
            mDatePickerDialog.setTitle(mTitle);
        }
        if(mCustomTitleView != null){
            mDatePickerDialog.setCustomTitle(mCustomTitleView);
        }
        return mDatePickerDialog;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public void setCustomTitle(View view){
        mCustomTitleView = view;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // months are indexed from 0 to 11
        month++;
        if (view.isShown()) {
            if(getArguments() != null) {
                DatePickerOnDateSetInterface datePickerOnDateSetInterface = (DatePickerOnDateSetInterface) getArguments().getSerializable("this");
                int requestCode = getArguments().getInt("requestCode");
                datePickerOnDateSetInterface.onDateSet(requestCode, view, year, month, day);
            }else {
                Log.e("Dcidr", "getArguments() is null. please check if setArguments(bundle) is done properly");
            }
        }
    }

    public static interface DatePickerOnDateSetInterface {
        public void onDateSet(int requestCode, DatePicker view, int year, int month, int day);
    }


}
