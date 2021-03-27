package com.example.turbo.dcidr.android_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.event.BaseEvent;
//import com.example.turbo.dcidr.main.event.FoodEvent;
//import com.example.turbo.dcidr.main.event.SportEvent;
//import com.example.turbo.dcidr.main.event.SportEvent;
import com.example.turbo.dcidr.main.event.HikeEvent;
import com.example.turbo.dcidr.utils.common_utils.Utils;

/**
 * Created by Turbo on 2/21/2016.
 */
public class SportEventDetailActivity extends BaseActivity {

    private ImageView mSportEventDetailImage;
    private TextView mSportEventDetailEventName;
    private TextView mSportEventDetailPlace;
    private TextView mSportEventDetailStartTime;
    private TextView mSportEventDetailEndTime;
    private TextView mEventMemebersText;
    private Button mEventReCreateButton;
    //private ListView mSportEventDetailEventMembersListView;
    //private ArrayAdapter mEventMembersArrayAdapter;
    private LinearLayout mEventMemebersRelativeLayout;

    /**
     * calling super class for basic initialization
     */
    public SportEventDetailActivity(){
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
    public void initActivity() {

        setContentView(R.layout.activity_sport_event_detail);
        mSportEventDetailImage = (ImageView) findViewById(R.id.sport_event_detail_image);
        mSportEventDetailEventName = (TextView) findViewById(R.id.sport_event_detail_event_name);
        mSportEventDetailPlace = (TextView) findViewById(R.id.sport_event_detail_event_place);
        mSportEventDetailStartTime = (TextView) findViewById(R.id.sport_event_detail_event_start_time);
        mSportEventDetailEndTime = (TextView) findViewById(R.id.sport_event_detail_event_end_time);
        mEventMemebersRelativeLayout = (LinearLayout) findViewById(R.id.sport_event_detail_members);
        mEventMemebersText = (TextView) findViewById(R.id.sport_event_detail_members_text);
        mEventReCreateButton = (Button) findViewById(R.id.sport_event_detail_event_recreate_button);
        //mSportEventDetailEventMembersListView = (ListView) findViewById(R.id.sport_event_detail_event_members_list_view);
        //receive event data
        Intent eventIntent = getIntent();
        if (eventIntent != null) {
            BaseEvent event = (BaseEvent)eventIntent.getSerializableExtra("EVENT_OBJECT_KEY");
            if (event != null){
                if(event.getEventTypeStr().equals("HIKE")){
                    event = (HikeEvent) event;
//                }else if(event.getEventTypeStr().equals("FOOD")){
//                    event = (FoodEvent) event;
//                }else if(event.getEventTypeStr().equals("SPORT")){
//                    event = (SportEvent) event;
                }
                mSportEventDetailImage.setBackgroundResource(event.getEventTypeObj().getEventTypeDrawableIcon());
                mSportEventDetailEventName.setText(Utils.capitalizeFirstLetter(event.getType().toString()));
                mSportEventDetailPlace.setText(event.getLocationName());
                mSportEventDetailStartTime.setText(String.valueOf(event.getStartTime()));
                mSportEventDetailEndTime.setText(String.valueOf(event.getEndTime()));
                //TODO: Kanishka, set the members appropriately
                //mEventMemebersText.setText("Members (" + String.valueOf(event.getMemberCount()) + ")");
//                for (String eventName :event.getMemberNames()){
//                    TextView mTextView = new TextView(this);
//                    mTextView.setText(eventName);
//                    mTextView.setTextSize(20);
//                    mTextView.setPadding(0,10,0,10);
//                    //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    //params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
//                    //params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                    mEventMemebersRelativeLayout.addView(mTextView);
//                }
                //mEventMembersArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,event.getMemberNames());
                //mSportEventDetailEventMembersListView.setAdapter(mEventMembersArrayAdapter);
            }
        }

        // enable back button on actionbar. when selected, control goes to onOptionsItemSelected
        // with id as home.
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
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
        String eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(mSportEventDetailEventName.getText().toString()) + "EventActivity";
        try {
            Intent newEventIntent = new Intent(SportEventDetailActivity.this,Class.forName(eventActivityClassName));
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
