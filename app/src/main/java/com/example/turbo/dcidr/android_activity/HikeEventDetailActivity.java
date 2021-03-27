package com.example.turbo.dcidr.android_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.event.HikeEvent;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.util.ArrayList;
import java.util.TimeZone;
/**
 * Created by Turbo on 2/21/2016.
 */
public class HikeEventDetailActivity extends BaseActivity {

    private ImageView mHikeEventDetailImage;
    private TextView mHikeEventDetailEventName;
    private TextView mHikeEventDetailPlace;
    private TextView mHikeEventDetailStartTime;
    private TextView mHikeEventDetailEndTime;
    private TextView mEventMemebersText;
    private Button mEventReCreateButton;
    private TextView mEventGroupNameTextView;
    private LinearLayout mEventMembersRelativeLayout;
    private BaseEvent mHikeEvent;
    private Long mGroupId;
    private Long mEventId;
    private BaseGroup mBaseGroup;


    /**
     * calling super class for basic initialization
     */
    public HikeEventDetailActivity(){
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
        setContentView(R.layout.activity_hike_event_detail);
        mHikeEventDetailImage = (ImageView) findViewById(R.id.hike_event_detail_image);
        mHikeEventDetailEventName = (TextView) findViewById(R.id.hike_event_detail_event_name);
        mHikeEventDetailPlace = (TextView) findViewById(R.id.hike_event_detail_event_place);
        mHikeEventDetailStartTime = (TextView) findViewById(R.id.hike_event_detail_event_start_time);
        mHikeEventDetailEndTime = (TextView) findViewById(R.id.hike_event_detail_event_end_time);
        mEventMembersRelativeLayout = (LinearLayout) findViewById(R.id.hike_event_detail_members);
        mEventMemebersText = (TextView) findViewById(R.id.hike_event_detail_members_text);
        mEventReCreateButton = (Button) findViewById(R.id.hike_event_detail_event_recreate_button);
        mEventGroupNameTextView = (TextView) findViewById(R.id.hike_event_group_name);

        //receive event data
        Intent eventIntent = getIntent();
        if (eventIntent != null) {
            String source = eventIntent.getStringExtra(getResources().getString(R.string.source_key));
            if (source.equals(getResources().getString(R.string.history_custom_array_adapter_class_name))) {
                String groupIdStr = eventIntent.getStringExtra(getResources().getString(R.string.group_id));
                String eventIdStr = eventIntent.getStringExtra(getResources().getString(R.string.event_id));
                mGroupId = Long.valueOf(groupIdStr);
                mEventId = Long.valueOf(eventIdStr);
                mBaseGroup = DcidrApplication.getInstance().getGlobalHistoryContainer().getGroupMap().get(mGroupId);
            }
        }
        mEventGroupNameTextView.setText(mBaseGroup.getGroupName());
        mHikeEvent = (HikeEvent) mBaseGroup.getEventContainer().getEventMap().get(mEventId);
        mHikeEventDetailImage.setBackgroundResource(mHikeEvent.getEventTypeObj().getEventTypeDrawableIcon());
        mHikeEventDetailEventName.setText(Utils.capitalizeFirstLetter(mHikeEvent.getEventName().toString()));
        mHikeEventDetailPlace.setText(mHikeEvent.getLocationName());
        mHikeEventDetailStartTime.setText(String.valueOf(Utils.convertEpochToDateTime(mHikeEvent.getStartTime(),
                TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));
        mHikeEventDetailEndTime.setText(String.valueOf(Utils.convertEpochToDateTime(mHikeEvent.getEndTime(),
                TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));
        mEventMemebersText.setText("Members (" + String.valueOf(mBaseGroup.getUserContainer().getUserList().size()) + ")");

        ArrayList<String> memberNameArrayList = new ArrayList<String>();
        for (User user : mBaseGroup.getUserContainer().getUserList()) {
            memberNameArrayList.add(user.getFirstName() + " " + user.getLastName());
        }
        for (String userName : memberNameArrayList){
            TextView mTextView = new TextView(this);
            mTextView.setText(userName);
            mTextView.setTextSize(20);
            mTextView.setPadding(0,10,0,10);
            mEventMembersRelativeLayout.addView(mTextView);
        }

        // enable back button on actionbar. when selected, control goes to onOptionsItemSelected
        // with id as home.
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEventReCreateButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                recreateActivity();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }else if (id == R.id.new_activity){
            recreateActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void recreateActivity(){
        String eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(mHikeEvent.getEventTypeStr()) + "EventActivity";
        try {
            Intent newEventIntent = new Intent(HikeEventDetailActivity.this,Class.forName(eventActivityClassName));
            newEventIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.hike_event_detail_activity_class_name));
            newEventIntent.putExtra(getResources().getString(R.string.group_id), mBaseGroup.getGroupIdStr());
            newEventIntent.putExtra(getResources().getString(R.string.event_id), mHikeEvent.getEventIdStr());
            startActivity(newEventIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_detail_menu, menu);
        return true;
    }
}
