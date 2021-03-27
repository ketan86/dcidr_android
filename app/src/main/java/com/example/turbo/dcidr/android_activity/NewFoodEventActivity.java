package com.example.turbo.dcidr.android_activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.create_event_image_helper.CustomBitmapArrayAdapter;
import com.example.turbo.dcidr.main.container.EventContainer;
import com.example.turbo.dcidr.main.container.GroupContainer;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.event.FoodEvent;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.utils.common_utils.CalendarHandler;
import com.example.turbo.dcidr.utils.common_utils.LocationEnabler;
import com.example.turbo.dcidr.utils.common_utils.MyCalendar;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.example.turbo.dcidr.utils.image_utils.image_selector.CustomBitmap;
import com.example.turbo.dcidr.utils.image_utils.image_selector.GoogleImageSelector;
import com.example.turbo.dcidr.utils.layout_utils.DatePickerFragment;
import com.example.turbo.dcidr.utils.layout_utils.HorizontalListView;
import com.example.turbo.dcidr.utils.layout_utils.TimePickerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/15/2016.
 */
public class NewFoodEventActivity extends BaseActivity implements DatePickerFragment.DatePickerOnDateSetInterface, TimePickerFragment.TimePickerFragmentOnTimeSetInterface, GoogleApiClient.OnConnectionFailedListener, Serializable {

    private EditText mEventName;
    private TextView mEventPlaceSelector;
    private TextView mEventStartDatePicker;
    private TextView mEventEndDatePicker;
    private TextView mEventStartTimePicker;
    private TextView mEventEndTimePicker;
    private TextView mEventDecideByDatePicker;
    private TextView mEventDecideByTimePicker;


    private Switch mAllowDiffTypeOfEventCreation;
    private Switch mAllowEventProposal;
    private Switch mEditableByOthersSwitch;
    private Switch mEventPlaceEditableSwitch;
    private Switch mEventTimeEditableSwitch;
    private ImageView mEventCreationOptions;


    private int PLACE_PICKER_REQUEST = 1;
    private FoodEvent mFoodEvent;
    private BaseEvent mParentEvent;
    private String mUserIdStr;
    private double mPlaceLat;
    private String mGroupIdStr;
    private long mGroupId;
    private double mPlaceLong;
    private String mEventType;
    private long mEventParentId;
    private String mParentEventTypeStr;
    private ProgressDialog mProgressDialog;
    private GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_LOCATION_ENABLE = 100;
    private CalendarHandler mCalendarHandler;
    private HorizontalListView mHorizontalImageListView;
    private ArrayAdapter<CustomBitmap> mBitmapListViewAdapter;
    private ArrayList<CustomBitmap> mBitmapArrayList;
    private EditText mEventNotes;
    private RelativeLayout mFoodEventDetailRelativeLayout;

    /**
     * calling super class for basic initialization
     */
    public NewFoodEventActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // make sure user is populated before proceeding, refer to base activity onCreate
        setUserFetchListener(new UserFetchListener() {
            @Override
            public void onFetchDone() {
                initActivity();
            }
        });
        super.onCreate(savedInstanceState);
    }
    public void initActivity(){
        setContentView(R.layout.activity_new_food_event);

        mFoodEventDetailRelativeLayout = (RelativeLayout) findViewById(R.id.food_event_detail_relative_layout);

        mAllowDiffTypeOfEventCreation = new Switch(this);
        mAllowDiffTypeOfEventCreation.setChecked(true);
        mAllowEventProposal = new Switch(this);
        mAllowEventProposal.setChecked(true);

        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
        mEventPlaceSelector = (TextView) findViewById(R.id.event_place_button);
        mEventName = (EditText) findViewById(R.id.event_name_edit_text);
        mEventStartTimePicker = (TextView) findViewById(R.id.event_start_time_button);
        mEventStartTimePicker.setText(new SimpleDateFormat("HH:mm aa").format(new Date()));
        mEventEndTimePicker = (TextView) findViewById(R.id.event_end_time_button);
        mEventEndTimePicker.setText(new SimpleDateFormat("HH:mm aa").format(new Date()));
        mEventEndDatePicker = (TextView) findViewById(R.id.event_end_date_button);
        mEventEndDatePicker.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        mEventStartDatePicker = (TextView) findViewById(R.id.event_start_date_button);
        mEventStartDatePicker.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        mEventDecideByDatePicker = (TextView) findViewById(R.id.event_decide_date_button);
        mEventDecideByDatePicker.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
        mEventDecideByTimePicker = (TextView) findViewById(R.id.event_decide_time_button);
        mEventDecideByTimePicker.setText(new SimpleDateFormat("HH:mm aa").format(new Date()));
        mEditableByOthersSwitch = (Switch) findViewById(R.id.editable_by_others_switch);
        mEventPlaceEditableSwitch = (Switch) findViewById(R.id.event_place_editable_switch);
        mEventTimeEditableSwitch = (Switch) findViewById(R.id.event_time_editable_switch);
        mEventCreationOptions = (ImageView) findViewById(R.id.event_creation_options);
        mEventNotes = (EditText) findViewById(R.id.event_notes);
		mBitmapArrayList = new ArrayList<CustomBitmap>();

        mEventParentId = -1;

        mHorizontalImageListView = (HorizontalListView) findViewById(R.id.event_place_pic_horizontal_list_view);
        mBitmapListViewAdapter = new CustomBitmapArrayAdapter(this, R.id.event_place_pic_horizontal_list_view, mBitmapArrayList);
        mHorizontalImageListView.setAdapter(mBitmapListViewAdapter);
        HorizontalListView.OnScrollListener mOnBitmapScroll = new HorizontalListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(HorizontalListView view, int scrollState) {

            }

            @Override
            public void onScroll(HorizontalListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
        mHorizontalImageListView.setOnScrollListener(mOnBitmapScroll);


        mEventCreationOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.custom_event_attribute_menu_popup, null);

                final Switch allowDiffTypeOfEventCreation = (Switch) popupView.findViewById(R.id.allow_diff_typeof_events_creation);
                Switch allowEventProposal = (Switch) popupView.findViewById(R.id.allow_event_proposal);

                if(mEventParentId != -1){
                    allowDiffTypeOfEventCreation.setEnabled(false);
                    allowEventProposal.setEnabled(false);
                }

                if(mAllowDiffTypeOfEventCreation.isChecked()){
                    allowDiffTypeOfEventCreation.setChecked(true);
                }else {
                    allowDiffTypeOfEventCreation.setChecked(false);
                }
                if(mAllowEventProposal.isChecked()){
                    allowEventProposal.setChecked(true);
                }else {
                    allowEventProposal.setChecked(false);
                }

                allowEventProposal.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            mAllowEventProposal.setChecked(true);
                            mAllowDiffTypeOfEventCreation.setChecked(true);
                            allowDiffTypeOfEventCreation.setChecked(true);
                        }else {
                            mAllowEventProposal.setChecked(false);
                            mAllowDiffTypeOfEventCreation.setChecked(false);
                            allowDiffTypeOfEventCreation.setChecked(false);
                        }
                    }
                });

                allowDiffTypeOfEventCreation.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            mAllowDiffTypeOfEventCreation.setChecked(true);
                        }else {
                            mAllowDiffTypeOfEventCreation.setChecked(false);
                        }
                    }
                });

                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                popupWindow.setOutsideTouchable(true);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //TODO do sth here on dismiss
                    }
                });

                popupWindow.showAsDropDown(v);

            }
        });

        mEditableByOthersSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!buttonView.isEnabled()) {
                    mEventPlaceEditableSwitch.setEnabled(true);
                    mEventTimeEditableSwitch.setEnabled(true);
                }else{
//                    if(mAllowDiffTypeOfEventCreation.isChecked()) {
//                        mEditableByOthersSwitch.setEnabled(false);
//                        mEventPlaceEditableSwitch.setEnabled(false);
//                        mEventTimeEditableSwitch.setEnabled(false);
//                    }
                }
                if(isChecked){
                    mEventPlaceEditableSwitch.setChecked(true);
                    mEventTimeEditableSwitch.setChecked(true);
                }else{
                    mEventPlaceEditableSwitch.setChecked(false);
                    mEventTimeEditableSwitch.setChecked(false);
                }
            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, NewFoodEventActivity.this)
                .build();

        // receive intent and populate data
        Intent intent = getIntent();
        if (intent != null) {
            String source = intent.getStringExtra(getResources().getString(R.string.source_key));
            if (source.equals(getResources().getString(R.string.create_group_activity_class_name))) {
                mGroupIdStr = intent.getStringExtra(getResources().getString(R.string.group_id));
                mGroupId = Long.valueOf(mGroupIdStr);
                mEventType = intent.getStringExtra(getResources().getString(R.string.event_type_key));
                String parentEventIdStr = intent.getStringExtra(getResources().getString(R.string.parent_event_id));
                if (parentEventIdStr == null) {
                    parentEventIdStr = "-1";
                }
                mParentEventTypeStr = intent.getStringExtra(getResources().getString(R.string.parent_event_type));
                mEventParentId = Long.valueOf(parentEventIdStr);
            }else if (source.equals(getString(R.string.event_timeline_activity_class_name))){
                mGroupIdStr = intent.getStringExtra(getResources().getString(R.string.group_id));
                mGroupId = Long.valueOf(mGroupIdStr);
                mEventType = Utils.capitalizeFirstLetter(intent.getStringExtra(getResources().getString(R.string.event_type_key)));
                mParentEventTypeStr = intent.getStringExtra(getResources().getString(R.string.parent_event_type));
                String parentEventIdStr = intent.getStringExtra(getResources().getString(R.string.parent_event_id));
                if (parentEventIdStr == null) {
                    parentEventIdStr = "-1";
                }
                mEventParentId = Long.valueOf(parentEventIdStr);
            } else if (source.equals(getString(R.string.food_event_detail_activity_class_name))) {
                mGroupIdStr = intent.getStringExtra(getResources().getString(R.string.group_id));
                String eventIdStr = intent.getStringExtra(getResources().getString(R.string.event_id));
                long groupIdLong = Long.valueOf(mGroupIdStr);
                long eventIdLong = Long.valueOf(eventIdStr);
                FoodEvent foodEvent = (FoodEvent) DcidrApplication.getInstance().getGlobalHistoryContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(eventIdLong);
                //mEventType = intent.getStringExtra(getResources().getString(R.string.event_type_key));
                //FoodEvent foodEvent = (FoodEvent) intent.getSerializableExtra(getResources().getString(R.string.event_object_key));
                if (foodEvent != null) {
                    mEventName.setText(foodEvent.getEventName());
                    mEventPlaceSelector.setText(foodEvent.getLocationName());
                    mEventType = Utils.capitalizeFirstLetter(foodEvent.getEventTypeStr());
                }
            } else if (source.equals(getString(R.string.selected_event_activity_class_name))) {
                mGroupIdStr = intent.getStringExtra(getResources().getString(R.string.group_id));
                String eventIdStr = intent.getStringExtra(getResources().getString(R.string.event_id));
                long groupIdLong = Long.valueOf(mGroupIdStr);
                long eventIdLong = Long.valueOf(eventIdStr);
                BaseEvent baseEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(eventIdLong);
                mEventType = Utils.capitalizeFirstLetter(baseEvent.getEventTypeStr());

                // set fields here

            }
            if(mEventParentId != -1){
                // disable event attributes switches if they are child events
                mAllowDiffTypeOfEventCreation.setEnabled(false);
                mEditableByOthersSwitch.setEnabled(false);
                mEventPlaceEditableSwitch.setEnabled(false);
                mEventTimeEditableSwitch.setEnabled(false);


                // set other attributes based on parent's mask
                mParentEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(mGroupId).getEventContainer().getEventMap().get(mEventParentId);
                BaseEvent.EventAttributeMask eventAttributeMask = mParentEvent.getEventAttributeMask();
                if(!eventAttributeMask.isEventAttributeEditable(BaseEvent.EventAttribute.ALLOW_EDITABLE_EVENT_LOCATION)) {
                    mEventPlaceSelector.setText(mParentEvent.getLocationName());
                    mEventPlaceSelector.setEnabled(false);
                }
                if(!eventAttributeMask.isEventAttributeEditable(BaseEvent.EventAttribute.ALLOW_EDITABLE_EVENT_TIME)) {
                    mEventStartDatePicker.setText(Utils.convertEpochToDateTime( mParentEvent.getStartTime(),TimeZone.getDefault(),"MM/dd/yyyy"));
                    mEventStartDatePicker.setEnabled(false);
                    mEventEndDatePicker.setText(Utils.convertEpochToDateTime( mParentEvent.getEndTime(),TimeZone.getDefault(),"MM/dd/yyyy"));
                    mEventEndDatePicker.setEnabled(false);
                    mEventDecideByDatePicker.setText(Utils.convertEpochToDateTime( mParentEvent.getDecideByTime(),TimeZone.getDefault(),"MM/dd/yyyy"));
                    mEventDecideByDatePicker.setEnabled(false);

                    mEventStartTimePicker.setText(Utils.convertEpochToDateTime( mParentEvent.getStartTime(),TimeZone.getDefault(),"HH:mm aa"));
                    mEventStartTimePicker.setEnabled(false);
                    mEventEndTimePicker.setText(Utils.convertEpochToDateTime( mParentEvent.getEndTime(),TimeZone.getDefault(),"HH:mm aa"));
                    mEventEndTimePicker.setEnabled(false);
                    mEventDecideByTimePicker.setText(Utils.convertEpochToDateTime( mParentEvent.getDecideByTime(),TimeZone.getDefault(),"HH:mm aa"));
                    mEventDecideByTimePicker.setEnabled(false);
                }


            }
        }

        mEventPlaceSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the mBitmapArrayList
                mBitmapArrayList.clear();
                LocationEnabler locationEnabler = new LocationEnabler(NewFoodEventActivity.this);
                locationEnabler.enableLocation(REQUEST_LOCATION_ENABLE);

                //mEventPlaceSelector.setText("Test Location");
                //mPlaceLat = Double.parseDouble("32.22");
                //mPlaceLong = Double.parseDouble("32.22");

                // TODO Enable this later
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(NewFoodEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        mEventStartDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setCustomTitle(getCustomDialogTitleTextView("Start Date", 30));
                Bundle bundle = new Bundle();
                bundle.putSerializable("this", NewFoodEventActivity.this);
                bundle.putInt("requestCode", 0);
                newFragment.setArguments(bundle);
                FragmentManager fm = NewFoodEventActivity.this.getFragmentManager();
                newFragment.show(fm, "datePicker");
            }
        });

        mEventStartTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setCustomTitle(getCustomDialogTitleTextView("Start Time", 30));
                Bundle bundle = new Bundle();
                bundle.putSerializable("this", NewFoodEventActivity.this);
                bundle.putInt("requestCode", 0);
                newFragment.setArguments(bundle);
                FragmentManager fm = NewFoodEventActivity.this.getFragmentManager();
                newFragment.show(fm, "timePicker");
            }
        });

        mEventEndDatePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setCustomTitle(getCustomDialogTitleTextView("End Date", 30));
                Bundle bundle = new Bundle();
                bundle.putSerializable("this", NewFoodEventActivity.this);
                bundle.putInt("requestCode", 1);
                newFragment.setArguments(bundle);
                FragmentManager fm = NewFoodEventActivity.this.getFragmentManager();
                newFragment.show(fm, "datePicker");
            }
        });

        mEventEndTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setCustomTitle(getCustomDialogTitleTextView("End Time", 30));
                Bundle bundle = new Bundle();
                bundle.putSerializable("this", NewFoodEventActivity.this);
                bundle.putInt("requestCode", 1);
                newFragment.setArguments(bundle);
                FragmentManager fm = NewFoodEventActivity.this.getFragmentManager();
                newFragment.show(fm, "timePicker");
            }
        });

        mEventDecideByDatePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setCustomTitle(getCustomDialogTitleTextView("Decide By Date", 30));
                Bundle bundle = new Bundle();
                bundle.putSerializable("this", NewFoodEventActivity.this);
                bundle.putInt("requestCode", 2);
                newFragment.setArguments(bundle);
                FragmentManager fm = NewFoodEventActivity.this.getFragmentManager();
                newFragment.show(fm, "datePicker");
            }
        });

        mEventDecideByTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setCustomTitle(getCustomDialogTitleTextView("Decide By Time", 30));
                Bundle bundle = new Bundle();
                bundle.putSerializable("this", NewFoodEventActivity.this);
                bundle.putInt("requestCode", 2);
                newFragment.setArguments(bundle);
                FragmentManager fm = NewFoodEventActivity.this.getFragmentManager();
                newFragment.show(fm, "timePicker");
            }
        });

        BaseEvent event = (BaseEvent) getIntent().getSerializableExtra("Event");
        if (event != null) {
            mEventPlaceSelector.setText(event.getLocationName().toString());
            String startTime = Utils.convertEpochToDateTime(event.getStartTime(), TimeZone.getDefault(), "hh:mm aa");
            String endTime = Utils.convertEpochToDateTime(event.getEndTime(), TimeZone.getDefault(), "hh:mm aa");
            mEventStartTimePicker.setText(startTime);
            mEventEndTimePicker.setText(endTime);
            mEventDecideByDatePicker.setText(String.valueOf(event.getDecideByTime()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mEventType + " Activity");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.start_event) {
            createEvent();
        }
        return super.onOptionsItemSelected(item);
    }

    public void createEvent() {
        mFoodEvent = new FoodEvent(getApplicationContext());
        // set event attribute mask
        FoodEvent.EventAttributeMask eventAttributeMask = mFoodEvent.getEventAttributeMask();
        if (mAllowDiffTypeOfEventCreation.isChecked()) {
            eventAttributeMask.setEventAttribute(FoodEvent.EventAttribute.ALLOW_EDITABLE_DIFFERENT_EVENT_TYPES);
        }
        if(mEventPlaceEditableSwitch.isChecked()) {
            eventAttributeMask.setEventAttribute(FoodEvent.EventAttribute.ALLOW_EDITABLE_EVENT_LOCATION);
        }
        if(mEventTimeEditableSwitch.isChecked()){
            eventAttributeMask.setEventAttribute(FoodEvent.EventAttribute.ALLOW_EDITABLE_EVENT_TIME);
        }
        if(mAllowEventProposal.isChecked()){
            eventAttributeMask.setEventAttribute(FoodEvent.EventAttribute.ALLOW_EVENT_PROPOSAL);
        }

        mFoodEvent.setCreatedByName(DcidrApplication.getInstance().getUser().getUserName());
        mFoodEvent.setEventType(BaseEvent.EventType.FOOD);
        mFoodEvent.setParentEventId(mEventParentId);

        if(mParentEventTypeStr != null) {
            mFoodEvent.setParentEventType(BaseEvent.EventType.valueOf(mParentEventTypeStr));
        }
        long eventTime = System.currentTimeMillis();
        mFoodEvent.setEventLastModifiedTime(eventTime);
        mFoodEvent.setLocationName(mEventPlaceSelector.getText().toString());
        if(mParentEvent != null && !mParentEvent.getEventAttributeMask().isEventAttributeEditable(BaseEvent.EventAttribute.ALLOW_EDITABLE_EVENT_LOCATION)) {
            mFoodEvent.setLocationCoordinates(mParentEvent.getLocationCoordinatesPoints().getLatitude(), mParentEvent.getLocationCoordinatesPoints().getLongitude());
        }else {
            mFoodEvent.setLocationCoordinates(mPlaceLat, mPlaceLong);
        }
        mFoodEvent.setEventName(mEventName.getText().toString());
        mFoodEvent.setEventNotes(mEventNotes.getText().toString());
        mFoodEvent.setStartTime(Utils.convertDateTimeToEpoch(mEventStartDatePicker.getText().toString(),
                mEventStartTimePicker.getText().toString(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa"));
        mFoodEvent.setEndTime(Utils.convertDateTimeToEpoch(mEventEndDatePicker.getText().toString(),
                mEventEndTimePicker.getText().toString(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa"));
        mFoodEvent.setDecideByTime(Utils.convertDateTimeToEpoch(mEventDecideByDatePicker.getText().toString(),
                mEventDecideByTimePicker.getText().toString(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa"));
        //iterate through the mBitmapArrayList and find out the selected image
        for (int i=0; i < mBitmapArrayList.size();i++){
            CustomBitmap cb = mBitmapArrayList.get(i);
            if (cb.mSelected) {
                String imageBase64Str  = Utils.encodeToBase64(cb.mBitmap);
                mFoodEvent.setEventBase64PicStr(imageBase64Str);
                break;
            }
        }

        // create group on server
        mProgressDialog = getAndShowProgressDialog(NewFoodEventActivity.this, getResources().getString(R.string.loading_msg));


        if(!hasPermission(Manifest.permission.READ_CALENDAR) && !hasPermission(Manifest.permission.WRITE_CALENDAR)){
            requestPermission(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    getResources().getString(R.string.camera_permission_rationale) ,
                    BaseActivity.CALENDAR_PERMISSION_REQUEST_CODE);
        }else {
            // pre-marshmallow releases. No run-time permission so start camera intent.
            pushEventToCalender();
        }



//            // add event to a calender
//            Intent intent = new Intent(Intent.ACTION_INSERT);
//            intent.setType("vnd.android.cursor.item/event");
//            intent.putExtra(CalendarContract.Events.TITLE, mFoodEvent.getEventName());
//            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
//                    mFoodEvent.getStartTime());
//            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
//                    mFoodEvent.getEndTime());
//            intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
//            intent.putExtra(CalendarContract.Events.DESCRIPTION,"New test event desc");
//            intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
//            startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_food_event_menu, menu);
        return true;
    }



    public void pushEventToCalender(){
        mCalendarHandler = new CalendarHandler(this);
        Integer noOfEvent = mCalendarHandler.getEventCount(mFoodEvent.getStartTime(), mFoodEvent.getEndTime());
        if(noOfEvent != null){
            if(noOfEvent > 0) {
                //showAlertDialog("There are " + String.valueOf(noOfEvent) + " events conflicting. Do you want to still continue ?");
                dismissProgressDialog(mProgressDialog);
                showCalendarConflictDialog(noOfEvent);
            }else {
                showCalendarSelectionDialog(mCalendarHandler.getCalendars());
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                GoogleImageSelector imageSelector = new GoogleImageSelector(place, mGoogleApiClient);
                mProgressDialog = getAndShowProgressDialog(NewFoodEventActivity.this, getResources().getString(R.string.loading_msg));
                imageSelector.onAsyncImageLoadDone(new GoogleImageSelector.ImageSelectorCallbackInterface() {
                    @Override
                    public void onAsyncImageLoadDone(ArrayList<CustomBitmap> bitmapArrayList) {
                        mBitmapArrayList.addAll(bitmapArrayList);
                        if (bitmapArrayList.size() != 0) {
                            mHorizontalImageListView.setMinimumHeight(500);
                            mBitmapListViewAdapter.notifyDataSetChanged();
                        }
                        mProgressDialog.dismiss();
                    }
                });
                imageSelector.loadImagesAsync();


                mEventPlaceSelector.setText(place.getName());
                String[] latLongArray = place.getLatLng().toString().split(",");
                mPlaceLat = Double.parseDouble(latLongArray[0].split("\\(")[1]);
                mPlaceLong = Double.parseDouble(latLongArray[1].split("\\)")[0]);
            }
        }else if(requestCode == REQUEST_LOCATION_ENABLE){

        }
    }

    @Override
    public void onDateSet(int requestCode, DatePicker view, int year, int month, int day) {
        if(requestCode == 0){
            mEventStartDatePicker.setText(String.valueOf(month) + '/' + String.valueOf(day) + '/' + String.valueOf(year));
            mEventStartTimePicker.performClick();
        }else if(requestCode == 1){
            mEventEndDatePicker.setText(String.valueOf(month) + '/' + String.valueOf(day) + '/' + String.valueOf(year));
            mEventEndTimePicker.performClick();
        } else if(requestCode == 2){
            mEventDecideByDatePicker.setText(String.valueOf(month) + '/' + String.valueOf(day) + '/' + String.valueOf(year));
            mEventDecideByTimePicker.performClick();
        }
    }

    @Override
    public void onTimeSet(int requestCode, TimePicker view, String timeStr) {
        if(requestCode == 0){
            mEventStartTimePicker.setText(timeStr);
            mEventEndDatePicker.performClick();
        }else if(requestCode == 1){
            mEventEndTimePicker.setText(timeStr);
            mEventDecideByDatePicker.performClick();
        }else if(requestCode == 2){
            mEventDecideByTimePicker.setText(timeStr);
        }
    }

    private AsyncHttpResponseHandler mCreateEventAsyncHttpResponseListener = new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            // called before request is started
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (statusCode == 201) {
                try {
                    mFoodEvent.setEventId(new JSONObject(new String(response)).getLong("eventId"));
                    //mBaseGroup.getEventContainer().populateEvent(new JSONObject(mFoodEvent.getEventDataAsMap()));
                    dismissProgressDialog(mProgressDialog);

                    JSONObject eventJsonObject = new JSONObject(mFoodEvent.getEventDataAsMap());
                    eventJsonObject.put("groupId", mGroupIdStr);
                    eventJsonObject.put("eventId", mFoodEvent.getEventIdStr());
                    eventJsonObject.put("eventTypeId", BaseEvent.EventType.valueOf(mFoodEvent.getEventTypeStr()).getValue());
                    eventJsonObject.put("userId", DcidrApplication.getInstance().getUser().getUserIdStr());
                    //eventJsonObject.put("groupLastModifiedTime", String.valueOf(System.currentTimeMillis()));
                    Long groupId = Long.valueOf(mGroupIdStr).longValue();
                    GroupContainer groupContainer = DcidrApplication.getInstance().getGlobalGroupContainer();
                    BaseGroup baseGroup = groupContainer.getGroupMap().get(groupId);
                    baseGroup.incrementTotalEventCount();
                    baseGroup.setGroupLastModifiedTime(System.currentTimeMillis());
                    groupContainer.refreshGroupList();

                    EventContainer eventContainer = baseGroup.getEventContainer();

//                    JSONObject jsonUserEventObject = new JSONObject();
//                    jsonUserEventObject.put("userId", DcidrApplication.getInstance().getUser().getUserIdStr());
//                    jsonUserEventObject.put("eventId",  mFoodEvent.getEventIdStr());
//                    jsonUserEventObject.put("eventStatusTypeId",  1);
//
//                    UserEventStatusContainer userEventStatusContainer;
                    if (mFoodEvent.getParentEventId() == -1) {
                        eventContainer.populateEvent(eventJsonObject);
//                        userEventStatusContainer = eventContainer.getEventMap().get(mFoodEvent.getEventId()).getUserEventStatusContainer();
//                        eventContainer.refreshEventList();
//                        userEventStatusContainer.setGroupId(groupId);
//                        userEventStatusContainer.populateMe(jsonUserEventObject);
                    }else {
                        eventContainer.getEventMap().get(mFoodEvent.getParentEventId()).getChildEventsContainer().populateEvent(eventJsonObject);
//                        userEventStatusContainer = eventContainer.getEventMap().get(mFoodEvent.getParentEventId()).getChildEventsContainer().getEventMap().get(mFoodEvent.getEventId()).getUserEventStatusContainer();
//                        eventContainer.refreshEventList();
//                        userEventStatusContainer.setGroupId(groupId);
//                        userEventStatusContainer.populateMe(jsonUserEventObject);
//
//                        // set parent and all other child event status to declined
//                        UserEventStatusContainer parentUserEventStatusContainer = eventContainer.getEventMap().get(mFoodEvent.getParentEventId()).getUserEventStatusContainer();
//                        parentUserEventStatusContainer.getCurrentUserEventStatusObj().setEventStatusType(UserEventStatus.EventStatusType.DECLINED);
//                        for(BaseEvent childEvent : eventContainer.getEventMap().get(mFoodEvent.getParentEventId()).getChildEventsContainer().getEventList()) {
//                            childEvent.getUserEventStatusContainer().getCurrentUserEventStatusObj().setEventStatusType(UserEventStatus.EventStatusType.DECLINED);
//                        }
                    }



                    //localBroadcastIntent.putExtra("MSG", jsonObject.toString());
                    //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);

                    // return to main activity
                    Intent mainActivityIntent = new Intent(NewFoodEventActivity.this, MainActivity.class);
                    mainActivityIntent.putExtra("Source", "NewFoodEventActivity");
                    startActivity(mainActivityIntent);

                    finish();

                } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                    showAlertDialog("Unexpected Error occurred" + e.toString());
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            JSONObject jsonObject = null;
            String errorString = null;
            try {
                jsonObject = new JSONObject(new String(errorResponse));
                errorString = (String) jsonObject.get("error");
            } catch (JSONException error) {
                Toast toast = Toast.makeText(NewFoodEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
            showAlertDialog(errorString);
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
            Log.e("Retry", "Retry");
        }
    };

    public void showCalendarConflictDialog(int noOfConflictEvents) {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.calendar_conflict_custom_alert_dialog_title, null);

        TextView calConflictMsg = (TextView) linearLayout.findViewById(R.id.calendar_conflict_message);
        calConflictMsg.setText(String.valueOf(noOfConflictEvents) + " existing event(s) are conflicting with your activity in your calendar.");

        ImageView gotoCalImageView = (ImageView) linearLayout.findViewById(R.id.goto_calendar_button);
        gotoCalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, mFoodEvent.getStartTime());
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(linearLayout);
        builder.setItems(new CharSequence[]
                        {"CONTINUE", "CANCEL", "OK"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                showCalendarSelectionDialog(mCalendarHandler.getCalendars());
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                        }
                    }
                });
        builder.create().show();

    }

    private void showCalendarSelectionDialog(final ArrayList<MyCalendar> myCalendars){
        CharSequence[] charSequence = new CharSequence[myCalendars.size()];
        int count = 0;
        for(MyCalendar myCalendar: myCalendars){
            charSequence[count] = myCalendar.getCalName();
            count++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showAlertDialog("you selected " + view.findViewById(R.id.textView));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setCustomTitle(getCustomDialogTitleTextView("Choose a calendar to add an activity.", 20));
        builder.setItems(charSequence,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mCalendarHandler.pushEventToCalendar(myCalendars.get(which).getCalId(), mFoodEvent);
                            EventAsyncHttpClient eventAsyncHttpClient = new EventAsyncHttpClient(getApplicationContext());
                            eventAsyncHttpClient.createEvent(mUserIdStr, mGroupIdStr, mFoodEvent,mCreateEventAsyncHttpResponseListener);
                        } catch (UnsupportedEncodingException e) {
                            showAlertDialog("Unexpected Error: " + e.toString());
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == BaseActivity.CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                pushEventToCalender();
            } else {
                // Permission Denied
                // nothing to do
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
        if(mFoodEvent != null) {
            mFoodEvent.releaseMemory();
            mFoodEvent = null;
        }
        if(mHorizontalImageListView != null) {
            mHorizontalImageListView = null;
        }

        if(mBitmapArrayList != null) {
            mBitmapArrayList.clear();
            mBitmapArrayList = null;
        }
        if(mBitmapListViewAdapter != null) {
            mBitmapListViewAdapter.clear();
            mBitmapListViewAdapter = null;
        }
    }
}