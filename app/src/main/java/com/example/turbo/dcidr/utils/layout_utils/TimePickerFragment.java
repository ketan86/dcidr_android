package com.example.turbo.dcidr.utils.layout_utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Turbo on 4/8/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private TimePickerDialog mTimePickerFragment;
    private String mTitle;
    private View mCustomTitleView;
    public TimePickerFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        mTimePickerFragment = new TimePickerDialog(getActivity(), this, hour, minute,false);
        if(mTitle != null) {
            mTimePickerFragment.setTitle(mTitle);
        }
        if(mCustomTitleView != null){
            mTimePickerFragment.setCustomTitle(mCustomTitleView);
        }
        return mTimePickerFragment;

    }

    public void setTitle(String title){
        mTitle = title;
    }

    public void setCustomTitle(View view){
        mCustomTitleView = view;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (view.isShown()) {
            if(getArguments() != null) {
                TimePickerFragmentOnTimeSetInterface datePickerOnDateSetInterface = (TimePickerFragmentOnTimeSetInterface) getArguments().get("this");
                int requestCode = getArguments().getInt("requestCode");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm aa");
                String time = mSDF.format(calendar.getTime());
                datePickerOnDateSetInterface.onTimeSet(requestCode, view, time);
            }else {
                Log.e("Dcidr", "getArguments() is null. please check if setArguments(bundle) is done properly");
            }
        }
    }

    public static interface TimePickerFragmentOnTimeSetInterface {
        public void onTimeSet(int requestCode, TimePicker view, String time);
    }
}
