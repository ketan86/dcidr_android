package com.example.turbo.dcidr.android_activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.selected_group_event_activity_helper.SelectedGroupEventActivityHelper;
import com.example.turbo.dcidr.main.activity_helper.selected_group_event_activity_helper.SelectedGroupEventCustomArrayAdapter;
import com.example.turbo.dcidr.main.container.EventContainer;
import com.example.turbo.dcidr.main.group.BaseGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/25/2016.
 */
public class SelectedGroupEventActivity extends BaseActivity {
    private ListView mSelectedGroupEventListView;
    private SelectedGroupEventCustomArrayAdapter mSelectedGroupEventCustomArrayAdapter;
    private BaseGroup mBaseGroup;
    private SelectedGroupEventActivityHelper mSelectedGroupEventActivityHelper;
    /**
     * calling super class for basic initialization
     */
    public SelectedGroupEventActivity(){
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
        setContentView(R.layout.activity_selected_group_event);



        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.selected_group_event_notification_action_filter));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // receive intent and extract group id, eventId and userId
        String groupId  = getIntent().getStringExtra(getResources().getString(R.string.selected_group_id));
        if(groupId != null){
            long groupIdLong = Long.valueOf(groupId);
            mBaseGroup = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong);
        }

        // if group object is not null
        if (mBaseGroup != null){
            mBaseGroup.setUnreadEventCount(0);
//            if(mBaseGroup.getEventContainer() == null) {
//                mBaseGroup.setEventContainer(new EventContainer(this));
//            }
            mSelectedGroupEventActivityHelper = new SelectedGroupEventActivityHelper(this);
            // set title to specific group name
            getSupportActionBar().setTitle(mBaseGroup.getGroupName());

            // init list view and custom array adapter
            mSelectedGroupEventListView = (ListView) findViewById(R.id.selected_group_event_list_view);
            mSelectedGroupEventCustomArrayAdapter  = new SelectedGroupEventCustomArrayAdapter(this, R.layout.activity_selected_group_event_custom_view, mBaseGroup.getEventContainer().getEventList());
            mSelectedGroupEventListView.setAdapter(mSelectedGroupEventCustomArrayAdapter);
            mSelectedGroupEventListView.setOnScrollListener(mSelectedGroupEventListViewOnScrollListener);
            mSelectedGroupEventListView.setOnItemClickListener(mSelectedGroupEventListViewOnItemClickListener);
            // set end index based on fetched items
//            if(mBaseGroup.getEventContainer().getEventList().size() > 0){
//                mSelectedGroupEventActivityHelper.setFetchManagerEndIndex(mBaseGroup.getEventContainer().getEventList().size());
//            }
            mSelectedGroupEventActivityHelper.fetchEvents(mBaseGroup.getEventContainer().getEventList().size(), 5);
        }

    }


    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            String source = intent.getStringExtra("SOURCE");
            if(source != null ){
                if(source.equals("GCM") || source.equals("LOCAL")){
                    String msg = intent.getStringExtra("MSG");
                    String action = intent.getStringExtra("ACTION");
                    Log.d("receiver", "Got message: " + msg);
                    if(action != null) {
                        if(action.equals("REFRESH")){
                            EventContainer eventContainer = mBaseGroup.getEventContainer();
                            eventContainer.refreshEventList();
                            mSelectedGroupEventCustomArrayAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    };


    private ListView.OnItemClickListener mSelectedGroupEventListViewOnItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent eventTimelineActivity = new Intent(SelectedGroupEventActivity.this, EventTimelineActivity.class);
            eventTimelineActivity.putExtra(getString(R.string.selected_group_id), mBaseGroup.getGroupIdStr());
            eventTimelineActivity.putExtra(getString(R.string.selected_event_id), mBaseGroup.getEventContainer().getEventList().get(position).getEventIdStr());
            eventTimelineActivity.putExtra(getString(R.string.source_key), getString(R.string.selected_group_event_activity_class_name));
            startActivity(eventTimelineActivity);
        }
    };
    /**
     * onScrollListener view listener for list view
     */
    private ListView.OnScrollListener mSelectedGroupEventListViewOnScrollListener = new ListView.OnScrollListener(){

        int mFirstVisibleItem;
        int mVisibleItemCount;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == 0){
                mSelectedGroupEventActivityHelper.fetchEvents(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount - 1);}
            }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem != 0 && visibleItemCount != 0) {
                this.mFirstVisibleItem = firstVisibleItem;
                this.mVisibleItemCount = visibleItemCount;

            }
        }
    };

    public BaseGroup getBaseGroup(){
        return this.mBaseGroup;
    }

    public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
        if (statusCode == 200) {
            try {
                mBaseGroup.getEventContainer().setBaseGroup(mBaseGroup);
                mBaseGroup.getEventContainer().populateEvent(new JSONObject(new String(response)).getJSONArray("result"));
                EventContainer eventContainer = mBaseGroup.getEventContainer();
                //fetch all sub events

            } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                showAlertDialog("Unexpected Error occurred" + e.toString());
            }
            mBaseGroup.getEventContainer().refreshEventList();
            mSelectedGroupEventCustomArrayAdapter.notifyDataSetChanged();

        }
    }


    public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        JSONObject jsonObject = null;
        String errorString = null;
        try {
            jsonObject = new JSONObject(new String(errorResponse));
            errorString = (String) jsonObject.get("error");
        } catch (JSONException error) {
            Toast toast = Toast.makeText(this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
            toast.show();
        }
        showAlertDialog(errorString);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.selected_group_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // remove eventContainer from BaseGroup
            if(mBaseGroup.getEventContainer() != null) {
                mBaseGroup.getEventContainer().releaseMemory();
            }
            finish();
        }else if(id == R.id.create_event){
            Intent selectNewEventActivityIntent = new Intent(this, SelectNewEventActivity.class);
            selectNewEventActivityIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.selected_group_event_activity_class_name));
            selectNewEventActivityIntent.putExtra(getResources().getString(R.string.selected_group_id), mBaseGroup.getGroupIdStr());
            startActivity(selectNewEventActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        mSelectedGroupEventListView = null;
        mSelectedGroupEventActivityHelper = null;
        mSelectedGroupEventCustomArrayAdapter = null;

    }
}
