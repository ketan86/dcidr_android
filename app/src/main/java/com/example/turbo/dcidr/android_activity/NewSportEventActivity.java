//package com.example.turbo.dcidr.android_activity;
//
//import android.app.DialogFragment;
//import android.app.FragmentManager;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.DatePicker;
//import android.widget.TextView;
//import android.widget.TimePicker;
//
//import com.example.turbo.dcidr.R;
//import com.example.turbo.dcidr.main.event.BaseEvent;
//import com.example.turbo.dcidr.utils.common_utils.Utils;
//import com.example.turbo.dcidr.utils.layout_utils.DatePickerFragment;
//import com.example.turbo.dcidr.utils.layout_utils.TimePickerFragment;
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;
//
//import java.io.Serializable;
//
///**
// * Created by Turbo on 2/15/2016.
// */
//public class NewSportEventActivity extends BaseActivity implements DatePickerFragment.DatePickerOnDateSetInterface, TimePickerFragment.TimePickerFragmentOnTimeSetInterface, Serializable{
//
//    private TextView mEventSpinner;
//    private TextView mEventPlaceSelector;
//    private TextView mEventStartDatePicker;
//    private TextView mEventEndDatePicker;
//    private TextView mEventStartTimePicker;
//    private TextView mEventEndTimePicker;
//    private TextView mEventDecideDatePicker;
//    private TextView mEventDecideTimePicker;
//    private int PLACE_PICKER_REQUEST = 1;
//
//    /**
//     * calling super class for basic initialization
//     */
//    public NewSportEventActivity(){
//        super();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_new_sport_event);
//        mEventPlaceSelector = (TextView) findViewById(R.id.event_place_button);
//        mEventStartTimePicker = (TextView) findViewById(R.id.event_start_time_button);
//        mEventEndTimePicker = (TextView) findViewById(R.id.event_end_time_button);
//        mEventEndDatePicker = (TextView) findViewById(R.id.event_end_date_button);
//        mEventStartDatePicker = (TextView) findViewById(R.id.event_start_date_button);
//        mEventDecideDatePicker = (TextView) findViewById(R.id.event_decide_date_button);
//        mEventDecideTimePicker = (TextView) findViewById(R.id.event_decide_time_button);
//
//
//        mEventPlaceSelector.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent gpsOptionsIntent = new Intent(
//                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(gpsOptionsIntent);
//
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(NewSportEventActivity.this), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        mEventStartDatePicker.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new DatePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("this", NewSportEventActivity.this);
//                bundle.putInt("requestCode", 0);
//                newFragment.setArguments(bundle);
//                FragmentManager fm = NewSportEventActivity.this.getFragmentManager();
//                newFragment.show(fm,"datePicker");
//            }
//        });
//
//        mEventStartTimePicker.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new TimePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("this", NewSportEventActivity.this);
//                bundle.putInt("requestCode", 0);
//                newFragment.setArguments(bundle);
//                FragmentManager fm = NewSportEventActivity.this.getFragmentManager();
//                newFragment.show(fm,"timePicker");
//            }
//        });
//
//        mEventEndDatePicker.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new DatePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("this", NewSportEventActivity.this);
//                bundle.putInt("requestCode", 1);
//                newFragment.setArguments(bundle);
//                FragmentManager fm = NewSportEventActivity.this.getFragmentManager();
//                newFragment.show(fm,"datePicker");
//            }
//        });
//
//        mEventEndTimePicker.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new TimePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("this", NewSportEventActivity.this);
//                bundle.putInt("requestCode", 1);
//                newFragment.setArguments(bundle);
//                FragmentManager fm = NewSportEventActivity.this.getFragmentManager();
//                newFragment.show(fm,"timePicker");
//            }
//        });
//
//        mEventDecideDatePicker.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new DatePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("this", NewSportEventActivity.this);
//                bundle.putInt("requestCode", 2);
//                newFragment.setArguments(bundle);
//                FragmentManager fm = NewSportEventActivity.this.getFragmentManager();
//                newFragment.show(fm,"datePicker");
//            }
//        });
//
//        mEventDecideTimePicker.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new TimePickerFragment();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("this", NewSportEventActivity.this);
//                bundle.putInt("requestCode", 2);
//                newFragment.setArguments(bundle);
//                FragmentManager fm = NewSportEventActivity.this.getFragmentManager();
//                newFragment.show(fm,"timePicker");
//            }
//        });
//
//        BaseEvent event = (BaseEvent) getIntent().getSerializableExtra("Event");
//        if (event != null){
//            mEventPlaceSelector.setText(event.getLocationName().toString());
//            String[] startDateTimeArray =  Utils.convertEpochToDateTime(event.getStartTime());
//            String[] EndDateTimeArray =  Utils.convertEpochToDateTime(event.getEndTime());
//            mEventStartTimePicker.setText(String.valueOf(startDateTimeArray[1]));
//            mEventEndTimePicker.setText(String.valueOf(EndDateTimeArray[1]));
//            mEventDecideDatePicker.setText(String.valueOf(event.getDecideByTime()));
//        }
//
//        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        //setSupportActionBar(toolbar);
//        //getSupportActionBar().setHomeTextViewEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(this, data);
//                //String toastMsg = String.format("Place: %s", place.getName());
//                //String toastMsg = String.format("Place: %s", place.getAddress());
//                //Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//                mEventPlaceSelector.setText(place.getName());
//            }
//        }
//    }
//
//    @Override
//    public void onDateSet(int requestCode, DatePicker view, int year, int month, int day) {
//        if(requestCode == 0){
//            mEventStartDatePicker.setText(String.valueOf(month) + '/' + String.valueOf(day) + '/' + String.valueOf(year));
//            mEventStartTimePicker.performClick();
//        }else if(requestCode == 1){
//            mEventEndDatePicker.setText(String.valueOf(month) + '/' + String.valueOf(day) + '/' + String.valueOf(year));
//            mEventEndTimePicker.performClick();
//        } else if(requestCode == 2){
//            mEventDecideDatePicker.setText(String.valueOf(month) + '/' + String.valueOf(day) + '/' + String.valueOf(year));
//            mEventDecideTimePicker.performClick();
//        }
//    }
//
//    @Override
//    public void onTimeSet(int requestCode, TimePicker view, String ) {
//        if(requestCode == 0){
//            mEventStartTimePicker.setText(String.valueOf(hourOfDay) + ':' + String.valueOf(minute));
//            mEventEndDatePicker.performClick();
//        }else if(requestCode == 1){
//            mEventEndTimePicker.setText(String.valueOf(hourOfDay) + ':' + String.valueOf(minute));
//            mEventDecideDatePicker.performClick();
//        }else if(requestCode == 2){
//            mEventDecideTimePicker.setText(String.valueOf(hourOfDay) + ':' + String.valueOf(minute));
//        }
//    }
//}