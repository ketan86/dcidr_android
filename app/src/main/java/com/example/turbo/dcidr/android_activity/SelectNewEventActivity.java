package com.example.turbo.dcidr.android_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.activity_helper.select_new_event_helper.SelectNewEventActivityHelper;
import com.example.turbo.dcidr.main.activity_helper.select_new_event_helper.SelectNewEventCustomArrayAdapter;
import com.example.turbo.dcidr.main.container.EventTypeContainer;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/17/2016.
 */
public class SelectNewEventActivity extends BaseActivity {
    private GridView mSelectNewEventCustomGridView;
    private SelectNewEventCustomArrayAdapter mSelectNewEventCustomArrayAdapter;
    private SelectNewEventActivityHelper mSelectNewEventActivityHelper;
    private ProgressDialog mProgressDialog;
    private EventTypeContainer mEventTypeContainer;
    private String mGroupIdStr;
    private String mBaseEventParentIdStr;
    private String mBaseEventParentTypeStr;
    /**
     * calling super class for basic initialization
     */
    public SelectNewEventActivity(){
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
        setContentView(R.layout.activity_select_new_event);

        // receive a new intent
        Intent intent = getIntent();
        mBaseEventParentIdStr = "-1"; //default parent is -1
        if(intent != null) {
            // extract source name
            String source = intent.getStringExtra(getResources().getString(R.string.source_key));
            if(source != null){
                if (source.equals(getResources().getString(R.string.event_timeline_activity_class_name))) {
                    mGroupIdStr = intent.getStringExtra(getResources().getString(R.string.group_id));
                    mBaseEventParentIdStr = intent.getStringExtra(getResources().getString(R.string.parent_event_id));
                    mBaseEventParentTypeStr = intent.getStringExtra(getResources().getString(R.string.parent_event_type));
                } else if(source.equals(getResources().getString(R.string.selected_group_event_activity_class_name))) {
                    mGroupIdStr = intent.getStringExtra(getResources().getString(R.string.selected_group_id));
                }
            }
        }

        // fetch event information from database and populate EventContainer using SelectNewEventActivityHelper
        mSelectNewEventActivityHelper = new SelectNewEventActivityHelper(this);
        mSelectNewEventActivityHelper.fetchEventTypes(0,100);

        // enable back button on actionbar. when selected, control goes to onOptionsItemSelected
        // with id as home.
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // display progress dialog right in the beginning and dismiss when EventContainer is populated.
        mProgressDialog = getAndShowProgressDialog(this, getResources().getString(R.string.loading_msg));

        // init mEventTypeContainer
        mEventTypeContainer = new EventTypeContainer();

        // init listview
        mSelectNewEventCustomGridView = (GridView) findViewById(R.id.select_new_event_custom_grid_view);
        mSelectNewEventCustomArrayAdapter = new SelectNewEventCustomArrayAdapter(SelectNewEventActivity.this, R.id.select_new_event_custom_grid_view, mEventTypeContainer.getEventTypeList());
        mSelectNewEventCustomGridView.setAdapter(mSelectNewEventCustomArrayAdapter);
        mSelectNewEventCustomGridView.setOnScrollListener(mSelectNewEventCustomGridViewOnScrollListener);
        mSelectNewEventCustomGridView.setOnItemClickListener(mSelectNewEventCustomGridViewOnItemClickListener);
    }

    /**
     * onScrollListener view listener for  list view
     */
    private ListView.OnScrollListener mSelectNewEventCustomGridViewOnScrollListener = new ListView.OnScrollListener(){

        int mFirstVisibleItem;
        int mVisibleItemCount;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == 0){
                mSelectNewEventActivityHelper.fetchEventTypes(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount - 1);}
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem != 0 && visibleItemCount != 0) {
                this.mFirstVisibleItem = firstVisibleItem;
                this.mVisibleItemCount = visibleItemCount;

            }
        }
    };

    /**
     * onItemClickListener for {@link #mSelectNewEventCustomGridView} item click
     */
    private GridView.OnItemClickListener mSelectNewEventCustomGridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView eventNameTextView = (TextView) view.findViewById(R.id.select_new_event_name_text);

            if(mGroupIdStr != null){
                // navigate user to selected event page
                String eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(eventNameTextView.getText().toString()) + "EventActivity";
                Intent newEventIntent = null;
                try {
                    newEventIntent = new Intent(SelectNewEventActivity.this, Class.forName(eventActivityClassName));
                } catch (ClassNotFoundException e) {
                    showAlertDialog(eventActivityClassName + " class name not found");
                }
                newEventIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.create_group_activity_class_name));
                newEventIntent.putExtra(getResources().getString(R.string.event_type_key), Utils.capitalizeFirstLetter(eventNameTextView.getText().toString()));
                newEventIntent.putExtra(getResources().getString(R.string.parent_event_id), mBaseEventParentIdStr);
                newEventIntent.putExtra(getResources().getString(R.string.parent_event_type), mBaseEventParentTypeStr);
                newEventIntent.putExtra(getString(R.string.group_id), mGroupIdStr);
                startActivity(newEventIntent);
            }else {
                Intent createGroupIntent = new Intent(SelectNewEventActivity.this, CreateGroupActivity.class);
                createGroupIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.select_new_event_activity_class_name));
                createGroupIntent.putExtra(getResources().getString(R.string.event_type_key), eventNameTextView.getText().toString());
                startActivity(createGroupIntent);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_new_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
        if (statusCode == 200){
            try {
                mEventTypeContainer.populateEventType(new JSONObject(new String(response)).getJSONArray("result"));
            } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                showAlertDialog("Unexpected Error occurred" + e.toString());
            }
            mEventTypeContainer.refreshEventTypeList();
            mSelectNewEventCustomArrayAdapter.notifyDataSetChanged();
            dismissProgressDialog(mProgressDialog);
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
}
