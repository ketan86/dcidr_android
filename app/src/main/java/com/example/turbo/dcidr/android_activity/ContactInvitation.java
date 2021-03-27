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
import android.widget.TextView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.contact_invitation_helper.InInvitationCustomArrayAdapter;
import com.example.turbo.dcidr.main.activity_helper.contact_invitation_helper.OutInvitationCustomArrayAdapter;
import com.example.turbo.dcidr.main.container.ContactContainer;
import com.example.turbo.dcidr.main.fetch_manager.AsyncHttpFetchManager;
import com.example.turbo.dcidr.main.user.Contact;
import com.example.turbo.dcidr.utils.layout_utils.HorizontalListView;
import com.example.turbo.dcidr.utils.progress_bar_utils.DottedProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 7/10/2016.
 */
public class ContactInvitation extends BaseActivity{
    private AsyncHttpFetchManager mInInvitationAsyncHttpFetchManager;
    private AsyncHttpFetchManager mOutInvitationAsyncHttpFetchManager;
    private UserAsyncHttpClient mUserAsyncHttpClient;
    private ContactContainer mInInvitationContactContainer;
    private ContactContainer mOutInvitationContactContainer;
    private TextView mNoInInvitationMsgTextView;
    private TextView mNoOutInvitationMsgTextView;
    private DottedProgressBar mInInvitationProgressBar;
    private DottedProgressBar mOutInvitationProgressBar;
    private HorizontalListView mInInvitationHorizontalListView;
    private HorizontalListView mOutInvitationHorizontalListView;
    private InInvitationCustomArrayAdapter mInInvitationCustomArrayAdapter;
    private OutInvitationCustomArrayAdapter mOutInvitationCustomArrayAdapter;
    private String mUserIdStr;
    private String mUserEmailId;

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
        setContentView(R.layout.activity_contact_invitation);

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.contact_invitation_notification_action_filter));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);

        // set app toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mInInvitationAsyncHttpFetchManager = new AsyncHttpFetchManager(this, 5, 10);
        mOutInvitationAsyncHttpFetchManager = new AsyncHttpFetchManager(this, 5, 10);
        mUserAsyncHttpClient = new UserAsyncHttpClient(this);
        mInInvitationContactContainer = new ContactContainer(this);
        mOutInvitationContactContainer = new ContactContainer(this);
        mUserIdStr = DcidrApplication.getInstance().getUser().getUserIdStr();
        mUserEmailId = DcidrApplication.getInstance().getUser().getEmailId();


        mInInvitationHorizontalListView = (HorizontalListView) findViewById(R.id.incoming_invitation_horizontal_list_view);
        mOutInvitationHorizontalListView = (HorizontalListView) findViewById(R.id.outgoing_invitation_horizontal_list_view);
        mInInvitationCustomArrayAdapter = new InInvitationCustomArrayAdapter(this, R.id.incoming_invitation_horizontal_list_view, mInInvitationContactContainer.getContactList());
        mOutInvitationCustomArrayAdapter = new OutInvitationCustomArrayAdapter(this, R.id.outgoing_invitation_horizontal_list_view, mOutInvitationContactContainer.getContactList());
        // Assign adapter to the HorizontalListView
        mInInvitationHorizontalListView.setAdapter(mInInvitationCustomArrayAdapter);
        mInInvitationHorizontalListView.setOnScrollListener(mInInvitationOnScroll);

        // Assign adapter to the HorizontalListView
        mOutInvitationHorizontalListView.setAdapter(mOutInvitationCustomArrayAdapter);
        mOutInvitationHorizontalListView.setOnScrollListener(mOutInvitationOnScroll);
        fetchInInvitations(0, 5);
        fetchOutInvitations(0, 5);

        mNoInInvitationMsgTextView = (TextView) findViewById(R.id.no_incoming_invitation_msg_text);
        mNoOutInvitationMsgTextView = (TextView) findViewById(R.id.no_outgoing_invitation_msg_text);
        mInInvitationProgressBar = (DottedProgressBar) findViewById(R.id.incoming_invitation_progress_bar);
        mOutInvitationProgressBar = (DottedProgressBar) findViewById(R.id.outgoing_invitation_progress_bar);
        mInInvitationProgressBar.startProgress();
        mOutInvitationProgressBar.startProgress();

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
                if(source.equals("GCM")){
                    String msg = intent.getStringExtra("MSG");
                    String action = intent.getStringExtra("ACTION");
                    Log.d("receiver", "Got message: " + msg);
                    if(action != null && msg != null) {
                        try {
                            if(action.equals("REFRESH")){
                                //
                            }else if(action.equals("INVITE")){
                                mInInvitationContactContainer.populateMe(new JSONObject(msg), Contact.ContactType.DCIDR);
                                mInInvitationContactContainer.refreshContactList();
                                mInInvitationCustomArrayAdapter.notifyDataSetChanged();
                                mNoInInvitationMsgTextView.setVisibility(View.GONE);
                            }else if(action.equals("ACCEPT") || action.equals("DECLINE")){
                                mInInvitationContactContainer.getContactMap().remove(new JSONObject(msg).getString("emailId"));
                                mInInvitationContactContainer.refreshContactList();
                                mInInvitationCustomArrayAdapter.notifyDataSetChanged();
                                if(mInInvitationContactContainer.getContactMap().size() == 0){
                                    mNoInInvitationMsgTextView.setVisibility(View.VISIBLE);
                                }
                                mOutInvitationContactContainer.getContactMap().remove(new JSONObject(msg).getString("emailId"));
                                mOutInvitationContactContainer.refreshContactList();
                                mOutInvitationCustomArrayAdapter.notifyDataSetChanged();
                                if(mOutInvitationContactContainer.getContactMap().size() == 0){
                                    mNoOutInvitationMsgTextView.setVisibility(View.VISIBLE);
                                }
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
                    }else{
                        showAlertDialog("Action or msg is null");
                    }
                }
            }
        }
    };


    public ContactContainer getInInvitationContactContainer(){
        return this.mInInvitationContactContainer;
    }

    public ContactContainer getOutInvitationContactContainer(){
        return this.mOutInvitationContactContainer;
    }

    public InInvitationCustomArrayAdapter getInInvitationCustomArrayAdapter(){
        return this.mInInvitationCustomArrayAdapter;
    }
    public OutInvitationCustomArrayAdapter getOutInvitationCustomArrayAdapter(){
        return this.mOutInvitationCustomArrayAdapter;
    }

    private HorizontalListView.OnScrollListener mOutInvitationOnScroll = new HorizontalListView.OnScrollListener(){

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
                fetchOutInvitations(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount);
            }
        }
    };

    private HorizontalListView.OnScrollListener mInInvitationOnScroll = new HorizontalListView.OnScrollListener(){

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
                fetchInInvitations(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount);
            }
        }
    };

    public void fetchInInvitations(int visibleStartIndex, int visibleEndIndex){
        mInInvitationAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                mUserAsyncHttpClient.getFriends(mUserIdStr, Contact.StatusType.INVITED.toString(), "true", offset, limit, mInInvitationAsyncHttpFetchManager.getAsyncHttpResponseHandler());
            }

            @Override
            public int postFetchSetEndIndex() {
                return mInInvitationContactContainer.getContactList().size();
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
                            mInInvitationProgressBar.stopProgress();
                            mInInvitationContactContainer.populateMe(jsonArray, Contact.ContactType.DCIDR);
                            mInInvitationCustomArrayAdapter.notifyDataSetChanged();
                        }else {
                            if(mInInvitationContactContainer.getContactMap().size() == 0) {
                                mInInvitationProgressBar.stopProgress();
                                mNoInInvitationMsgTextView.setVisibility(View.VISIBLE);
                            }
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
                    Toast toast = Toast.makeText(ContactInvitation.this, "Error fetching incoming invitations", Toast.LENGTH_SHORT);
                    toast.show();
                }
                showAlertDialog(errorString);
            }

        });
    }

    public void fetchOutInvitations(int visibleStartIndex, int visibleEndIndex){
        mOutInvitationAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                mUserAsyncHttpClient.getFriends(mUserIdStr, Contact.StatusType.INVITED.toString(), "false", offset, limit, mOutInvitationAsyncHttpFetchManager.getAsyncHttpResponseHandler());
            }

            @Override
            public int postFetchSetEndIndex() {
                return mOutInvitationContactContainer.getContactList().size();

            }

            @Override
            public void onFetchStart() {

            }

            @Override
            public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
                if (statusCode == 200) {
                    try {
                        JSONArray jsonArray = new JSONObject(new String(response)).getJSONArray("result");
                        if (jsonArray.length() > 0) {
                            mOutInvitationProgressBar.stopProgress();
                            mOutInvitationContactContainer.populateMe(jsonArray, Contact.ContactType.DCIDR);
                            mOutInvitationCustomArrayAdapter.notifyDataSetChanged();
                        }else{
                            if(mOutInvitationContactContainer.getContactMap().size() == 0) {
                                mOutInvitationProgressBar.stopProgress();
                                mNoOutInvitationMsgTextView.setVisibility(View.VISIBLE);
                            }
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
                    Toast toast = Toast.makeText(ContactInvitation.this, "Error fetching outgoing invitations", Toast.LENGTH_SHORT);
                    toast.show();
                }
                showAlertDialog(errorString);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_invitation_menu, menu);
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
            return true;
        }else if(id == R.id.invite_contact){
            Intent contactActivity = new Intent(this, InviteContactActivity.class);
            startActivity(contactActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInInvitationAsyncHttpFetchManager = null;
        mOutInvitationAsyncHttpFetchManager = null;
        mUserAsyncHttpClient = null;
        mInInvitationProgressBar = null;
        mOutInvitationProgressBar = null;
        mInInvitationHorizontalListView = null;
        mOutInvitationHorizontalListView = null;
        mInInvitationCustomArrayAdapter.clear();
        mOutInvitationCustomArrayAdapter.clear();
        mInInvitationCustomArrayAdapter = null;
        mOutInvitationCustomArrayAdapter = null;
    }
}
