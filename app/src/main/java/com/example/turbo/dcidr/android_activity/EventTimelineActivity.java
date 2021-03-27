package com.example.turbo.dcidr.android_activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.event_timeline_activity_helper.ChweetCustomArrayAdapter;
import com.example.turbo.dcidr.main.activity_helper.event_timeline_activity_helper.EventTimelineCustomArrayAdapter;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.event.Chweet;
import com.example.turbo.dcidr.main.fetch_manager.AsyncHttpFetchManager;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.example.turbo.dcidr.utils.layout_utils.HorizontalListView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 6/21/2016.
 */
public class EventTimelineActivity extends BaseActivity {
    private BaseEvent mBaseEvent;
    private String mGroupIdStr;
    private String mParentEventIdStr;
    ArrayList<BaseEvent> mAllEvents = new ArrayList<>();

    private AsyncHttpFetchManager mChweetAsyncHttpFetchManager;
    private HorizontalListView mHorizontalChweetListView;
    private TextView mNoChweetFoundMsg;
    private ArrayAdapter<Chweet> mHorizontalChweetListViewArrayAdapter;

    private ListView mEventTimelineListView;
    private EventTimelineCustomArrayAdapter mEventTimelineCustomArrayAdapter;
    public static final String TAG = "EventTimelineActivity";

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
        if (BuildConfig.DEBUG){Log.i(TAG, "[initActivity] init activity");}
        mChweetAsyncHttpFetchManager = new AsyncHttpFetchManager(this, 5, 10);
        setContentView(R.layout.activity_event_timeline);
        initCurrentLocation();
        Intent newIntent = getIntent();
        if(newIntent != null){
            if (BuildConfig.DEBUG){Log.i(TAG, "[initActivity] new intent received");}

            String source = newIntent.getStringExtra(getResources().getString(R.string.source_key));
            if (BuildConfig.DEBUG){Log.i(TAG, "[initActivity] intent source is :" + source);}

            if(source.equals(getResources().getString(R.string.selected_group_event_activity_class_name))) {
                mGroupIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_group_id));
                mParentEventIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_event_id));
                if (mParentEventIdStr != null && mGroupIdStr != null) {
                    long groupIdLong = Long.valueOf(mGroupIdStr);
                    long eventIdLong = Long.valueOf(mParentEventIdStr);
                    mBaseEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(eventIdLong);
                }
            }else if(source.equals(getResources().getString(R.string.gcm_message_handler_class_name))) {
                mGroupIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_group_id));
                mParentEventIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_event_id));
                if(mParentEventIdStr != null && mGroupIdStr != null){
                    long groupIdLong = Long.valueOf(mGroupIdStr);
                    long eventIdLong = Long.valueOf(mParentEventIdStr);
                    mBaseEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(eventIdLong);
                }
            }
        }

        //baseEvent has been initialized. Now fetch the first 5 chweets
        fetchChweet(0,5);

        mHorizontalChweetListView = (HorizontalListView) findViewById(R.id.chweet_horizontal_list_view);
        mHorizontalChweetListViewArrayAdapter = new ChweetCustomArrayAdapter(this, R.id.chweet_horizontal_list_view, mBaseEvent.getChweetContainer().getChweetList());
        mHorizontalChweetListView.setAdapter(mHorizontalChweetListViewArrayAdapter);
        mNoChweetFoundMsg = (TextView) findViewById(R.id.no_chweet_found_msg);
        mHorizontalChweetListView.setOnScrollListener(new HorizontalListView.OnScrollListener() {

            int mFirstVisibleItem;
            int mVisibleItemCount;

            @Override
            public void onScrollStateChanged(HorizontalListView view, int scrollState) {

            }

            @Override
            public void onScroll(HorizontalListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0 && visibleItemCount != 0) {
                    this.mFirstVisibleItem = firstVisibleItem;
                    this.mVisibleItemCount = visibleItemCount;
                    fetchChweet(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount);
                }
            }
        });

        mHorizontalChweetListView.setOnItemClickListener(new ListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chweet c = mHorizontalChweetListViewArrayAdapter.getItem(position);
                final Dialog dialog = new Dialog(EventTimelineActivity.this);
                dialog.setContentView(R.layout.chweet_bubble_dialog);
                TextView chweetTextView = (TextView) dialog.findViewById(R.id.chweet_textview);
                chweetTextView.setText(c.getChweetText());
                TextView chweet_user_info_date_textview = (TextView) dialog.findViewById(R.id.chweet_user_info_date_textview);
                chweet_user_info_date_textview.setText(c.getChweetUserFirstName() + " " + c.getChweetUserLastName() + " at " +
                        String.valueOf(Utils.convertEpochToDateTime(c.getChweetTime(),
                                TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));

                dialog.show();
            }
        });
        ImageView chweetImageView = (ImageView) findViewById(R.id.chweet_button);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.event_timeline_notification_action_filter));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);


        // clear userEventStatusContainer
        if(mBaseEvent != null){
            mBaseEvent.getUserEventStatusContainer().getUserEventStatusArray().clear();
        }

        createAllEventList();

        mEventTimelineListView = (ListView) findViewById(R.id.event_timeline_list_view);
        mEventTimelineCustomArrayAdapter  = new EventTimelineCustomArrayAdapter(this, R.layout.event_timeline_custom_view, mAllEvents);
        mEventTimelineListView.setAdapter(mEventTimelineCustomArrayAdapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set title to specific group name
        getSupportActionBar().setTitle("Activity Timeline");

        // create a time handler
        final Handler timerHandler = new Handler();
        // create a time runnable object
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                // get reference to decide by timer and update it
                if(mEventTimelineCustomArrayAdapter != null) {
                    for (int i = 0; i < mEventTimelineCustomArrayAdapter.getCount(); i++) {
                        BaseEvent baseEvent = mEventTimelineCustomArrayAdapter.getItem(i);
                        if (baseEvent != null) {
                            View view = mEventTimelineListView.getChildAt(i);
                            if (view != null) {
                                TextView textView = (TextView) view.findViewById(R.id.event_expire_time);
                                baseEvent.setViewWithTimeLeft(getApplicationContext(), baseEvent.getDecideByTime(), textView);
                            }
                        }
                    }
                }
                // set timer handler to run this runnable after every minute. It will set
                // this to run every time run method is executed
                timerHandler.postDelayed(this, 60000); //run every minute
                Log.i("[EventTimelineActivity]",".timerRunnable's timer event callback");
            }
        };
        // run time runnable
        timerRunnable.run();

        final EventAsyncHttpClient eventAsyncHttpClient = new EventAsyncHttpClient(this);
        chweetImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EventTimelineActivity.this);
                dialog.setContentView(R.layout.send_chweet_dialog);
                final EditText sendChweetEditText = (EditText) dialog.findViewById(R.id.send_chweet_edit_text);
                ImageView sendChweetClearButton = (ImageView) dialog.findViewById(R.id.send_chweet_clear_button);
                Button sendChweetCancelButton = (Button) dialog.findViewById(R.id.send_chweet_cancel_btn);
                Button sendChweetSendButton = (Button) dialog.findViewById(R.id.send_chweet_send_btn);
                final TextView sendChweetCharLeft = (TextView) dialog.findViewById(R.id.send_chweet_char_left_count);
                sendChweetEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length() >= 125) {
                            sendChweetCharLeft.setText(String.valueOf(125 - s.length()));
                            sendChweetCharLeft.setTextColor(ContextCompat.getColor(EventTimelineActivity.this, R.color.md_red_400));
                        }else {
                            sendChweetCharLeft.setText(String.valueOf(125 - s.length()));
                            sendChweetCharLeft.setTextColor(ContextCompat.getColor(EventTimelineActivity.this, R.color.md_green_400));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                sendChweetClearButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        sendChweetEditText.setText("");
                        sendChweetCharLeft.setTextColor(ContextCompat.getColor(EventTimelineActivity.this, R.color.md_green_400));
                    }
                });
                sendChweetCancelButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                sendChweetSendButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        try {
                            eventAsyncHttpClient.submitChweet(DcidrApplication.getInstance().getUser().getUserIdStr(),
                                    mGroupIdStr, mBaseEvent.getEventIdStr(), mBaseEvent.getEventTypeStr(),
                                    sendChweetEditText.getText().toString(), new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if (statusCode == 200) {
                                                // take id and poupate chweetContainer
                                                try {
                                                    long chweetId = new JSONObject(new String(responseBody)).getLong("chweetId");
                                                    Chweet c = new Chweet();
                                                    c.setChweetId(chweetId);
                                                    c.setChweetText(sendChweetEditText.getText().toString());
                                                    c.setChweetUserFirstName(DcidrApplication.getInstance().getUser().getFirstName());
                                                    c.setChweetUserLastName(DcidrApplication.getInstance().getUser().getLastName());
                                                    c.setChweetParentEventId(mBaseEvent.getEventId());
                                                    c.setChweetParentEventTypeId(mBaseEvent.getEventType().getValue());

                                                    mBaseEvent.getChweetContainer().getChweetList().add(c);
                                                    mNoChweetFoundMsg.setVisibility(View.GONE);
                                                    mBaseEvent.getChweetContainer().refreshChweetList();
                                                    mHorizontalChweetListViewArrayAdapter.notifyDataSetChanged();


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            dialog.dismiss();
                                            showAlertDialog("Error sending message");
                                        }
                                    });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public void createAllEventList(){
        mAllEvents.clear();
        if(mBaseEvent != null) {
            // add parent and child events to one temp container
            mAllEvents.add(mBaseEvent);

            // set event last modified time to 0 for expired events
            for (BaseEvent childEvent : mBaseEvent.getChildEventsContainer().getEventList()) {
                childEvent.setParentEventId(mBaseEvent.getEventId());
                childEvent.setParentEventType(mBaseEvent.getEventType());
                childEvent.getUserEventStatusContainer().getUserEventStatusArray().clear();
                if (childEvent.checkIfExpired()) {
                    childEvent.setEventLastModifiedTime(0);
                }
            }

            mBaseEvent.getChildEventsContainer().refreshEventList();

            mAllEvents.addAll(mBaseEvent.getChildEventsContainer().getEventList());
        }
    }
    public void fetchChweet(int visibleStartIndex, int visibleEndIndex) {
        mChweetAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                EventAsyncHttpClient eventAsyncHttpClient = new EventAsyncHttpClient(getApplicationContext());
                try {
                    eventAsyncHttpClient.getChweet(DcidrApplication.getInstance().getUser().getUserIdStr(),
                            mGroupIdStr, mParentEventIdStr, mBaseEvent.getEventTypeStr(), offset, limit, mChweetAsyncHttpFetchManager.getAsyncHttpResponseHandler());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public int postFetchSetEndIndex() {
                return mBaseEvent.getChweetContainer().getChweetList().size();
            }

            @Override
            public void onFetchStart() {

            }

            @Override
            public void onFetchSuccess(int statusCode, Header[] headers, final byte[] response) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONObject(new String(response)).getJSONArray("result");
                        if (jsonArray.length() > 0) {
                            mNoChweetFoundMsg.setVisibility(View.GONE);
                            mBaseEvent.getChweetContainer().populateMe(jsonArray);
                            mHorizontalChweetListViewArrayAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                JSONObject jsonObject = null;
                String errorString = null;
                try {
                    jsonObject = new JSONObject(new String(errorResponse));
                    errorString = (String) jsonObject.get("error");
                } catch (JSONException error) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Error fetching incoming invitations", Toast.LENGTH_SHORT);
                    toast.show();
                }
                showAlertDialog(errorString);
            }

        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String parentEventIdStr = intent.getStringExtra(getResources().getString(R.string.selected_event_id));
        if (BuildConfig.DEBUG){Log.i(TAG, "[onNewIntent] notification parentEventId is :" + parentEventIdStr);}
        if (BuildConfig.DEBUG){Log.i(TAG, "[onNewIntent] current parentEventId is :" + mBaseEvent.getEventIdStr());}

        if(!parentEventIdStr.equals(mBaseEvent.getEventIdStr())) {
            finish();
            startActivity(intent);
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
                    String type = intent.getStringExtra("TYPE");
                    Log.d("receiver", "Got message: " + msg);
                    if(action != null) {
                        if(action.equals("REFRESH")){
                            if (type != null) {
                                if (type.equals("EVENT")) {
                                    createAllEventList();
                                    mEventTimelineCustomArrayAdapter.notifyDataSetChanged();
                                } else if (type.equals("CHWEET")) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(msg);
                                        if(mBaseEvent.getEventId() == jsonObject.getLong("eventId")){
                                            mBaseEvent.getChweetContainer().populateMe(jsonObject);
                                            mBaseEvent.getChweetContainer().refreshChweetList();
                                            mHorizontalChweetListViewArrayAdapter.notifyDataSetChanged();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InstantiationException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_time_line_menu, menu);
        if(!mBaseEvent.getEventAttributeMask().isEventAttributeEditable(BaseEvent.EventAttribute.ALLOW_EVENT_PROPOSAL)){
            MenuItem menuItem = menu.findItem(R.id.new_activity);
            menuItem.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mBaseEvent.releaseMemory();
            finish();
        } else if(id == R.id.take_photos){
            Intent imageViewerActivityIntent = new Intent(this, ImageViewerActivity.class);
            startActivity(imageViewerActivityIntent);
        }else if (id == R.id.new_activity){
            String eventActivityClassName;

            if (mBaseEvent.getEventAttributeMask().isEventAttributeEditable( BaseEvent.EventAttribute.ALLOW_EDITABLE_DIFFERENT_EVENT_TYPES)) {
                eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "SelectNewEventActivity";
            } else {
                eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(mBaseEvent.getEventTypeStr()) + "EventActivity";
            }

            try {
                Intent newEventIntent = new Intent(EventTimelineActivity.this,Class.forName(eventActivityClassName));
                newEventIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.event_timeline_activity_class_name));
                //newEventIntent.putExtra(getResources().getString(R.string.user_container_obj), mUserContainer);
                newEventIntent.putExtra(getResources().getString(R.string.group_id), mBaseEvent.getGroupIdStr());
                newEventIntent.putExtra(getResources().getString(R.string.parent_event_id), mBaseEvent.getEventIdStr());
                newEventIntent.putExtra(getResources().getString(R.string.parent_event_type), mBaseEvent.getEventTypeStr());
                newEventIntent.putExtra(getResources().getString(R.string.event_type_key), mBaseEvent.getEventTypeStr());
                startActivity(newEventIntent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        for(int i= 0; i <this.mEventTimelineListView.getChildCount(); i ++){
//            View view = this.mEventTimelineListView.getChildAt(i);
//            HikeEvent.CustomViewHolder viewHolder = (HikeEvent.CustomViewHolder) view.getTag();
//            viewHolder.eventPic.setImageBitmap(null);
//        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        this.mEventTimelineListView = null;
        this.mEventTimelineCustomArrayAdapter = null;
        mAllEvents.clear();
    }
}
