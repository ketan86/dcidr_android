package com.example.turbo.dcidr.android_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.main.user.UserEventStatus;
import com.example.turbo.dcidr.utils.image_utils.rounded_image_view.CircularImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 6/9/2016.
 */
public class SelectedEventActivity extends BaseActivity {
    private BaseEvent mBaseEvent;
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
        setContentView(R.layout.activity_selected_event);


        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent newIntent = getIntent();
        if (newIntent != null) {
            String groupIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_group_id));
            String parentEventIdStr = newIntent.getStringExtra(getResources().getString(R.string.parent_event_id));
            String eventIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_event_id));
            String source = newIntent.getStringExtra(getResources().getString(R.string.source_key));
            if (eventIdStr != null && groupIdStr != null && parentEventIdStr != null) {
                long groupIdLong = Long.valueOf(groupIdStr);
                long eventIdLong = Long.valueOf(eventIdStr);
                long parentEventIdLong = Long.valueOf(parentEventIdStr);
                // find out users associated with group
                if (parentEventIdLong == -1) {
                    mBaseEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(eventIdLong);
                    populateUserData();
                } else {
                    BaseEvent parentEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(parentEventIdLong);
                    mBaseEvent = parentEvent.getChildEventsContainer().getEventMap().get(eventIdLong);
                    populateUserData();
                }
            }
        }

        // set title to specific event name
        getSupportActionBar().setTitle(mBaseEvent.getEventName());
    }

    private void displayUserData() {
        LinearLayout statusDetailsLinearLayout = (LinearLayout) findViewById(R.id.status_details_linear_layout);

        for (final UserEventStatus userEventStatus : mBaseEvent.getUserEventStatusContainer().getUserEventStatusArray()) {


            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.event_status_details_layout, null);
            final CircularImageView userProfilePicImageView = (CircularImageView) linearLayout.findViewById(R.id.user_profile_pic);
            if(userEventStatus.getUserObj().getUserProfilePicBitmap() == null) {

                userEventStatus.getUserObj().onUserProfPicFetchDoneListener(new User.UserProfPicFetchInterface() {
                    @Override
                    public void onImageFetchDone(Bitmap bitmap) {
                        if (bitmap != null) {
                            userProfilePicImageView.setImageBitmap(userEventStatus.getUserObj().getUserProfilePicBitmap());
                        }
                    }

                    @Override
                    public int getImageWidth() {
                        return userProfilePicImageView.getWidth();
                    }

                    @Override
                    public int getImageHeight() {
                        return userProfilePicImageView.getHeight();
                    }
                });
            }else {
                userProfilePicImageView.setImageBitmap(userEventStatus.getUserObj().getUserProfilePicBitmap());
            }
            TextView userNameTextView = (TextView) linearLayout.findViewById(R.id.user_name);
            userNameTextView.setText(userEventStatus.getUserObj().getUserName());

            ImageView statusImageView = (ImageView) linearLayout.findViewById(R.id.user_event_status);
            if (userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.ACCEPTED) {
                statusImageView.setImageResource(R.drawable.accept_icon);
            } else if (userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.DECLINED) {
                statusImageView.setImageResource(R.drawable.decline_icon);
            } else if (userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.PENDING) {
                statusImageView.setImageResource(R.drawable.pending_icon);
            }

            ImageView buzzImageView = (ImageView) linearLayout.findViewById(R.id.user_buzz_button);
            buzzImageView.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EventAsyncHttpClient eventMembersAsyncHttpClient = new EventAsyncHttpClient(getApplicationContext());
                        eventMembersAsyncHttpClient.buzzUser(DcidrApplication.getInstance().getUser().getUserIdStr(), mBaseEvent.getGroupIdStr(), mBaseEvent.getEventIdStr(), mBaseEvent.getEventTypeStr(), userEventStatus.getUserIdStr(), new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        if (statusCode == 200) {
                                            showAlertDialog("You just buzzed " + userEventStatus.getUserObj().getUserName());
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
                                            Toast toast = Toast.makeText(SelectedEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                        showAlertDialog(errorString);
                                    }
                                }
                        );
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            statusDetailsLinearLayout.addView(linearLayout);
        }
    }

    private void populateUserData(){
        // check if user data is already there in data structure
        for(UserEventStatus userEventStatus: mBaseEvent.getUserEventStatusContainer().getUserEventStatusArray()){
            if(userEventStatus.getUserObj().getFirstName() != null){
                displayUserData();
                return;
            }
        }

        // else fetch members name
        GroupAsyncHttpClient groupMembersAsyncHttpClient = new GroupAsyncHttpClient(this);
        groupMembersAsyncHttpClient.getGroupMembers(DcidrApplication.getInstance().getUser().getUserIdStr(), mBaseEvent.getGroupIdStr(),
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        //get the memberNames from the JSONresponse

                        JSONObject jsonObject = null;
                        //UserContainer userContainer = new UserContainer(SelectedEventActivity.this);
                        try {
                            jsonObject = new JSONObject(new String(response));
                            JSONArray jsonUserArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonUserArray.length(); i++) {
                                JSONObject jsonUser = jsonUserArray.getJSONObject(i);
                                mBaseEvent.getUserEventStatusContainer().getUserEventStatusObj(jsonUser.getLong("userId")).getUserObj().populateMe(jsonUser);
                                //mBaseEvent.getUserEventStatusContainer().getUserEventStatusObj(jsonUser.getLong("userId")).getUserObj().setFirstName(jsonUser.getString("firstName"));
                               //mBaseEvent.getUserEventStatusContainer().getUserEventStatusObj(jsonUser.getLong("userId")).getUserObj().setLastName(jsonUser.getString("lastName"));
                            }
                            displayUserData();

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                            Toast toast = Toast.makeText(SelectedEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        showAlertDialog(errorString);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.selected_event_menu, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //            LinearLayout linearLayout = new LinearLayout(this);
//            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//            linearLayout.setWeightSum(1);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.setMargins(30, 20, 20, 10);
//
//
//            ImageView userProfilePicImageView = new ImageView(this);
//            userProfilePicImageView.setImageBitmap(Utils.drawableToBitmap(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_icon)));
//            LinearLayout.LayoutParams userProfilePicImageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.height = 80;
//            layoutParams.width = 80;
//            userProfilePicImageView.setLayoutParams(userProfilePicImageViewLayoutParams);
//            linearLayout.addView(userProfilePicImageView);
//
//
//            TextView userNameTextView = new TextView(this);
//            userNameTextView.setText(userEventStatus.getUserObj().getUserName());
//            userNameTextView.setTextSize(24);
//            LinearLayout.LayoutParams userNameTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            userNameTextViewLayoutParams.weight = 1;
//            userNameTextView.setLayoutParams(userNameTextViewLayoutParams);
//            linearLayout.addView(userNameTextView);
//
//            ImageView statusImageView = new ImageView(this);
//            if (userEventStatus.getEventStatusType() == UserEvent.EventStatusType.ACCEPTED) {
//                statusImageView.setBackgroundResource(R.drawable.accept_icon);
//            } else if (userEventStatus.getEventStatusType() == UserEvent.EventStatusType.DECLINED) {
//                statusImageView.setBackgroundResource(R.drawable.decline_icon);
//            } else if (userEventStatus.getEventStatusType() == UserEvent.EventStatusType.TENTATIVE) {
//                statusImageView.setBackgroundResource(R.drawable.tentative_icon);
//            } else if (userEventStatus.getEventStatusType() == UserEvent.EventStatusType.PENDING) {
//                statusImageView.setBackgroundResource(R.drawable.pending_icon);
//            }
//            LinearLayout.LayoutParams statusImageViewLayoutParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            statusImageViewLayoutParams.setMargins(30, 0, 30, 0);
//            linearLayout.addView(statusImageView, statusImageViewLayoutParams);
//
//            final ImageView buzzImageView = new ImageView(this);
//            buzzImageView.setOnClickListener(new ImageView.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        mEventMembersAsyncHttpClient.buzzUser(DcidrApplication.getInstance().getUser().getUserIdStr(), mBaseEvent.getGroupIdStr(), mBaseEvent.getEventIdStr(), userEventStatus.getUserIdStr(), new AsyncHttpResponseHandler() {
//                                    @Override
//                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                                        if (statusCode == 200) {
//                                            showAlertDialog("You just buzzed " + userEventStatus.getUserObj().getUserName());
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                                        JSONObject jsonObject = null;
//                                        String errorString = null;
//                                        try {
//                                            jsonObject = new JSONObject(new String(errorResponse));
//                                            errorString = (String) jsonObject.get("error");
//                                        } catch (JSONException error) {
//                                            Toast toast = Toast.makeText(SelectedEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
//                                            toast.show();
//                                        }
//                                        showAlertDialog(errorString);
//                                    }
//                                }
//                        );
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            buzzImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle));
//            //buzzImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.md_blue_400));
//            buzzImageView.setBackgroundResource(R.drawable.buzz_image_view_selector);
//
//            LinearLayout.LayoutParams buzzImageViewLayoutParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            buzzImageViewLayoutParams.setMargins(30, 0, 30, 0);
//            linearLayout.addView(buzzImageView, buzzImageViewLayoutParams);
//            mStatusDetailsLinearLayout.addView(linearLayout);
//        }
//    }
//



    //    private BaseEvent mBaseEvent;
//    private GroupAsyncHttpClient mGroupMembersAsyncHttpClient;
//    private EventAsyncHttpClient mEventMembersAsyncHttpClient;
//    private LinearLayout mStatusDetailsLinearLayout;
//    private String mGroupIdStr;
//    private String mEventIdStr;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_selected_event);
//        //mStatusDetailsLinearLayout = (LinearLayout) findViewById(R.id.status_details_linear_layout);
//        mEventMembersAsyncHttpClient = new EventAsyncHttpClient(this);
//        Intent newIntent = getIntent();
//        if(newIntent != null){
//            mGroupIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_group_id));
//            mEventIdStr = newIntent.getStringExtra(getResources().getString(R.string.selected_event_id));
//            String source = newIntent.getStringExtra(getResources().getString(R.string.source_key));
//            if(mEventIdStr != null && mGroupIdStr != null){
//                long groupIdLong = Long.valueOf(mGroupIdStr);
//                long eventIdLong = Long.valueOf(mEventIdStr);
//                // find out users associated with group
//                mBaseEvent = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupIdLong).getEventContainer().getEventMap().get(eventIdLong);
//                populateUserData();
//            }
//        }
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        // set title to specific group name
//        getSupportActionBar().setTitle(mBaseEvent.getEventName());
//
//    }
//
////    private void displayUserData(){
////
////        for(final UserEvent userEventStatus : mBaseEvent.getUserEventStatusContainer().getUserEventArray()){
////
////
////            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(
////                    R.layout.event_status_details_layout, null);
////
////            ImageView userProfilePicImageView = (ImageView)linearLayout.findViewById(R.id.user_profile_pic);
////
////            TextView userNameTextView = (TextView) linearLayout.findViewById(R.id.user_name);
////            userNameTextView.setText(userEventStatus.getUserObj().getUserName());
//
////            ImageView statusImageView = (ImageView) linearLayout.findViewById(R.id.user_event_status);
////            if(userEventStatus.getEventStatusType() == UserEvent.EventStatusType.ACCEPTED){
////                statusImageView.setImageResource(R.drawable.accept_icon);
////            }else if (userEventStatus.getEventStatusType() == UserEvent.EventStatusType.DECLINED) {
////                statusImageView.setImageResource(R.drawable.decline_icon);
////            }else if(userEventStatus.getEventStatusType() == UserEvent.EventStatusType.TENTATIVE) {
////                statusImageView.setImageResource(R.drawable.tentative_icon);
////            }else if(userEventStatus.getEventStatusType() == UserEvent.EventStatusType.PENDING){
////                statusImageView.setImageResource(R.drawable.pending_icon);
////            }
//
////            ImageView buzzImageView = (ImageView) linearLayout.findViewById(R.id.user_buzz_button);
////            buzzImageView.setOnClickListener(new ImageView.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    try {
////                        mEventMembersAsyncHttpClient.buzzUser(DcidrApplication.getInstance().getUser().getUserIdStr(), mBaseEvent.getGroupIdStr(), mBaseEvent.getEventIdStr(), userEventStatus.getUserIdStr(), new AsyncHttpResponseHandler() {
////                                    @Override
////                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
////                                        if (statusCode == 200) {
////                                            showAlertDialog("You just buzzed " + userEventStatus.getUserObj().getUserName());
////                                        }
////                                    }
////
////                                    @Override
////                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
////                                        JSONObject jsonObject = null;
////                                        String errorString = null;
////                                        try {
////                                            jsonObject = new JSONObject(new String(errorResponse));
////                                            errorString = (String) jsonObject.get("error");
////                                        } catch (JSONException error) {
////                                            Toast toast = Toast.makeText(SelectedEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
////                                            toast.show();
////                                        }
////                                        showAlertDialog(errorString);
////                                    }
////                                }
////                        );
////                    } catch (UnsupportedEncodingException e) {
////                        e.printStackTrace();
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////            });
//
////            mStatusDetailsLinearLayout.addView(linearLayout);
//
//
////            LinearLayout linearLayout = new LinearLayout(this);
////            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
////            linearLayout.setWeightSum(1);
////            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////            layoutParams.setMargins(30, 20, 20, 10);
//
//
////            ImageView userProfilePicImageView = new ImageView(this);
////            userProfilePicImageView.setImageBitmap(Utils.drawableToBitmap(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_icon)));
////            LinearLayout.LayoutParams userProfilePicImageViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
////            layoutParams.height = 80;
////            layoutParams.width = 80;
////            userProfilePicImageView.setLayoutParams(userProfilePicImageViewLayoutParams);
////            linearLayout.addView(userProfilePicImageView);
//
//
////            TextView userNameTextView = new TextView(this);
////            userNameTextView.setText(userEventStatus.getUserObj().getUserName());
////            userNameTextView.setTextSize(24);
////            LinearLayout.LayoutParams userNameTextViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
////            userNameTextViewLayoutParams.weight = 1;
////            userNameTextView.setLayoutParams(userNameTextViewLayoutParams);
////            linearLayout.addView(userNameTextView);
//
////            ImageView statusImageView = new ImageView(this);
////            if(userEventStatus.getEventStatusType() == UserEvent.EventStatusType.ACCEPTED){
////                statusImageView.setBackgroundResource(R.drawable.accept_icon);
////            }else if (userEventStatus.getEventStatusType() == UserEvent.EventStatusType.DECLINED) {
////                statusImageView.setBackgroundResource(R.drawable.decline_icon);
////            }else if(userEventStatus.getEventStatusType() == UserEvent.EventStatusType.TENTATIVE) {
////                statusImageView.setBackgroundResource(R.drawable.tentative_icon);
////            }else if(userEventStatus.getEventStatusType() == UserEvent.EventStatusType.PENDING){
////                statusImageView.setBackgroundResource(R.drawable.pending_icon);
////            }
////            LinearLayout.LayoutParams statusImageViewLayoutParams = new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////            statusImageViewLayoutParams.setMargins(30, 0, 30, 0);
////            linearLayout.addView(statusImageView, statusImageViewLayoutParams);
////
////            final ImageView buzzImageView = new ImageView(this);
////            buzzImageView.setOnClickListener(new ImageView.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    try {
////                        mEventMembersAsyncHttpClient.buzzUser(DcidrApplication.getInstance().getUser().getUserIdStr(), mBaseEvent.getGroupIdStr(), mBaseEvent.getEventIdStr(), userEventStatus.getUserIdStr(), new AsyncHttpResponseHandler() {
////                                    @Override
////                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
////                                        if (statusCode == 200) {
////                                            showAlertDialog("You just buzzed " + userEventStatus.getUserObj().getUserName());
////                                        }
////                                    }
////
////                                    @Override
////                                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
////                                        JSONObject jsonObject = null;
////                                        String errorString = null;
////                                        try {
////                                            jsonObject = new JSONObject(new String(errorResponse));
////                                            errorString = (String) jsonObject.get("error");
////                                        } catch (JSONException error) {
////                                            Toast toast = Toast.makeText(SelectedEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
////                                            toast.show();
////                                        }
////                                        showAlertDialog(errorString);
////                                    }
////                                }
////                        );
////                    } catch (UnsupportedEncodingException e) {
////                        e.printStackTrace();
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////            });
////
////            buzzImageView.setBackground(ContextCompat.getDrawable(this, R.drawable.circle));
////            //buzzImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.md_blue_400));
////            buzzImageView.setBackgroundResource(R.drawable.buzz_image_view_selector);
////
////            LinearLayout.LayoutParams buzzImageViewLayoutParams = new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////            buzzImageViewLayoutParams.setMargins(30, 0, 30, 0);
////            linearLayout.addView(buzzImageView, buzzImageViewLayoutParams);
////            mStatusDetailsLinearLayout.addView(linearLayout);
////        }
////    }
//
//    private void populateUserData(){
//
//        // check if user data is already there in data structure
//        for(UserEventStatus userEventStatus: mBaseEvent.getUserEventStatusContainer().getUserEventStatusArray()){
//            if(userEventStatus.getUserObj().getFirstName() != null){
//                //displayUserData();
//                return;
//            }
//        }
//
//        // else fetch members name
//        mGroupMembersAsyncHttpClient = new GroupAsyncHttpClient(this);
//        mGroupMembersAsyncHttpClient.getGroupMembers(DcidrApplication.getInstance().getUser().getUserIdStr(), mBaseEvent.getGroupIdStr(), mGetGroupMembersAsyncHttpResponseHandler);
//    }
//
//
//    private AsyncHttpResponseHandler mGetGroupMembersAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
//
//        @Override
//        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//            //get the memberNames from the JSONresponse
//
//            JSONObject jsonObject = null;
//            UserContainer userContainer = new UserContainer(SelectedEventActivity.this);
//            try {
//                jsonObject = new JSONObject(new String(response));
//                JSONArray jsonUserArray = jsonObject.getJSONArray("result");
//                for (int i = 0; i < jsonUserArray.length(); i++) {
//                    JSONObject jsonUser = jsonUserArray.getJSONObject(i);
//                    mBaseEvent.getUserEventStatusContainer().getUserEventStatusObj(jsonUser.getLong("userId")).getUserObj().setFirstName(jsonUser.getString("firstName"));
//                    mBaseEvent.getUserEventStatusContainer().getUserEventStatusObj(jsonUser.getLong("userId")).getUserObj().setLastName(jsonUser.getString("lastName"));
//                }
//                //displayUserData();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//            JSONObject jsonObject = null;
//            String errorString = null;
//            try {
//                jsonObject = new JSONObject(new String(errorResponse));
//                errorString = (String) jsonObject.get("error");
//            } catch (JSONException error) {
//                Toast toast = Toast.makeText(SelectedEventActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
//                toast.show();
//            }
//            showAlertDialog(errorString);
//        }
//
//        @Override
//        public void onRetry(int retryNo) {
//            // called when request is retried
//            Log.e("Retry", "Retry");
//        }
//    };
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.selected_event_menu, menu);
//        return true;
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
//        }else if (id == R.id.new_activity){
//
//            String eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(mBaseEvent.getEventTypeStr()) + "EventActivity";
//            try {
//                Intent newEventIntent = new Intent(SelectedEventActivity.this,Class.forName(eventActivityClassName));
//                newEventIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.selected_event_activity_class_name));
//                //newEventIntent.putExtra(getResources().getString(R.string.user_container_obj), mUserContainer);
//                newEventIntent.putExtra(getResources().getString(R.string.group_id), mBaseEvent.getGroupIdStr());
//                newEventIntent.putExtra(getResources().getString(R.string.event_id), mBaseEvent.getEventIdStr());
//                startActivity(newEventIntent);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
