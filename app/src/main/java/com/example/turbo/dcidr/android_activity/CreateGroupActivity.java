package com.example.turbo.dcidr.android_activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.container.GroupContainer;
import com.example.turbo.dcidr.main.container.UserContainer;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 4/9/2016.
 */
public class CreateGroupActivity extends  BaseActivity{
    private UserContainer mUserContainer;
    private GroupContainer mGroupContainer;
    private String mEventName;
    private Bitmap mGroupProfilePicBitmap;
    private String mUserIdStr;
    private BaseGroup mBaseGroup;
    private GroupAsyncHttpClient mGroupAsyncHttpClient;
    private ProgressDialog mProgressDialog;
    private LinearLayout mGroupProfilePicActionLinearLayout;
    private LinearLayout mMembersLinearLayout;
    private EditText mGroupNameEditText;
    private ImageView mGroupProfilePic;
    private boolean mGroupProfilePicSelected = false;
    private File sdImageFile;
    private File croppedSdImageFile;

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
        setContentView(R.layout.acivity_create_group_portrait);
        sdImageFile = new File(Environment.getExternalStorageDirectory(), "myImageFile.jpg");
        croppedSdImageFile = new File(Environment.getExternalStorageDirectory(), "croppedImageFile.jpg");

        // initialize create group activity. intent may need some of the values
        initCreateGroupActivity();

        // receive a new intent
        Intent intent = getIntent();
        if(intent != null){
            // extract source name
            String source = intent.getStringExtra(getResources().getString(R.string.source_key));
            mEventName = intent.getStringExtra(getResources().getString(R.string.event_type_key));
            if(mEventName != null) {
                if (source != null) {
                    if (source.equals(getResources().getString(R.string.select_new_event_activity_class_name))) {
                        // intent received from select_new_event_activity_class
                        // specific data handling from select_new_event_activity_class
                    } else if (source.equals(getResources().getString(R.string.history_custom_array_adapter_class_name))) {
                        // intent received from history_custom_array_adapter_class_name
                        ArrayList<Long> userIdArrayList = (ArrayList<Long>) intent.getSerializableExtra(getResources().getString(R.string.user_id_array_list_key));
                        String groupIdStr = (String) intent.getSerializableExtra(getResources().getString(R.string.group_id));
                        long groupId = Long.valueOf(groupIdStr);
                        ArrayList<User> userArrayList = new ArrayList<User>();
                        UserContainer userContainer = DcidrApplication.getInstance().getGlobalHistoryContainer().getGroupMap().get(groupId).getUserContainer();
                        for (int i=0;i<userIdArrayList.size();i++) {
                            HashMap<Long, User> userMap = userContainer.getUserMap();
                            Long userId = (Long)userIdArrayList.get(i);
                            userArrayList.add(userMap.get(userId));
                        }

                        if (userArrayList != null) {
                            for (User user : userArrayList) {
                                // push selected users to mUserContainer
                                mUserContainer.addToUserMap(user);
                            }
                        }
                        // call setSelectedFriends method to display users on screen
                        setSelectedFriends();
                    } else {
                        showAlertDialog("CreateGroupActivity received intent with source unknown");
                    }
                } else {
                    showAlertDialog("CreateGroupActivity received intent with source as null");
                }
            }else {
                showAlertDialog("CreateGroupActivity received intent with activity as null");
            }
        }
    }
    /**
     * initialize create group activity
     */
    public void initCreateGroupActivity(){
        // get userId from userCache

        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
        // init mGroupAsyncHttpClient for api request
        mGroupAsyncHttpClient = new GroupAsyncHttpClient(getApplicationContext());
        // init User and Group containers for storing selected users and groups
        mUserContainer = new UserContainer(this);
        mGroupContainer = new GroupContainer(getApplicationContext());

        // init xml views
        mGroupProfilePic = (ImageView) findViewById(R.id.group_profile_pic);
        mGroupNameEditText = (EditText) findViewById(R.id.group_name_edit_text);
        Button addNewButton = (Button) findViewById(R.id.add_new_person_button);
        //mCreateGroupDoneButton = (Button) findViewById(R.id.create_group_done_button);
        mMembersLinearLayout = (LinearLayout) findViewById(R.id.member_names_linear_layout);
        mGroupProfilePicActionLinearLayout = (LinearLayout) findViewById(R.id.group_profile_pic_action_linear_layout);
        RelativeLayout createGroupRelativeLayout = (RelativeLayout) findViewById(R.id.create_group_relative_layout);
        ImageView captureImageActionView = (ImageView) findViewById(R.id.capture_image_action);
        ImageView openPhotoLibraryActionView = (ImageView) findViewById(R.id.open_photo_library_action);

        // set onClickListener on relative layout to hide soft keyboard
        createGroupRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(CreateGroupActivity.this);
            }
        });

        // set app toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set onClickListener on group profiler pic click action
        mGroupProfilePic.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupProfilePicActionLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        // set onClickListener on for image capture click action
        captureImageActionView.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                // create Intent to take a picture and return control to the calling application
                Uri outputFileUri = Uri.fromFile(sdImageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                        ///method to get Images
                        // start the image capture Intent
                        startActivityForResult(intent, BaseActivity.CAPTURE_IMAGE_REQUEST_CODE);
                    }else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                            Toast.makeText(getApplicationContext(),"Your Permission is needed to get access the camera",Toast.LENGTH_LONG).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, BaseActivity.CAPTURE_IMAGE_REQUEST_CODE);
                    }
                }else{
                    // start the image capture Intent
                    startActivityForResult(intent, BaseActivity.CAPTURE_IMAGE_REQUEST_CODE);
                }

            }
        });

        // set onClickListener on for photo library selection click action
        openPhotoLibraryActionView.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                launchImageCaptureIntent();
            }
        });

//        mCreateGroupDoneButton.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String groupName = mGroupNameEditText.getText().toString();
//
//            }
//        });

        // set onClickListener on for launching friends search fragment page
        addNewButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGroupProfilePicActionLinearLayout.setVisibility(View.GONE);
                FragmentManager fm = getFragmentManager();
                FriendPickerFragment friendPickerFragment = new FriendPickerFragment();
                friendPickerFragment.show(fm, "friend_picker");
            }
        });
    }

    // methods to get Users and Groups containers objects
    public UserContainer getUserContainer(){
        return mUserContainer;
    }
    public GroupContainer getGroupContainer(){
        return mGroupContainer;
    }

    /**
     * method to set selected friends and populate them on user interface
     */
    public void setSelectedFriends(){
        // inflater linear layout which holds selected users and groups
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMembersLinearLayout.removeAllViewsInLayout();
        // loop over all selected users, create relative layout and push it to linerlayout
        for(final User user : mUserContainer.getUserMap().values()){
            if(user.getIsSelected()) {
                // only display the ones which are selected
                final RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.name_capsule_custom_view, null);
                TextView textView = (TextView) relativeLayout.findViewById(R.id.capsule_name_text_view);
                ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.capsule_image_view);
                imageView.setImageBitmap(user.getUserProfilePicBitmap());
                TextView userTypeColor = (TextView) relativeLayout.findViewById(R.id.type_color);
                userTypeColor.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.md_blue_800));
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_blue_800));
                Button cancelButton = (Button) relativeLayout.findViewById(R.id.capsule_cancel_button);
                textView.setText(user.getFirstName() + " " + user.getLastName());
                // register a cancel (delete) action to selected user
                cancelButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUserContainer.getUserMap().remove(user.getUserId());
                        mMembersLinearLayout.removeView(relativeLayout);
                    }
                });
                mMembersLinearLayout.addView(relativeLayout);
            }
        }

        // loop over all selected groups, create relative layout and push it to linerlayout
        for(final BaseGroup baseGroup : mGroupContainer.getGroupMap().values()){
            if(baseGroup.getIsSelected()) {
                final RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.name_capsule_custom_view, null);
                TextView textView = (TextView) relativeLayout.findViewById(R.id.capsule_name_text_view);
                ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.capsule_image_view);
                imageView.setImageBitmap(baseGroup.getGroupProfilePicBitmap());
                TextView groupTypeColor = (TextView) relativeLayout.findViewById(R.id.type_color);
                groupTypeColor.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.md_red_500));
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.md_red_500));
                Button cancelButton = (Button) relativeLayout.findViewById(R.id.capsule_cancel_button);
                textView.setText(baseGroup.getGroupName());
                // register a cancel (delete) action to selected group
                cancelButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mGroupContainer.getGroupMap().remove(baseGroup.getGroupId());
                        mMembersLinearLayout.removeView(relativeLayout);
                    }
                });
                mMembersLinearLayout.addView(relativeLayout);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseActivity.CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                launchCropImageIntent(sdImageFile, croppedSdImageFile);
            }
        }

        if (requestCode == BaseActivity.CROP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Bitmap croppedBitmap = Utils.decodeUriAsBitmap(this, uri, mGroupProfilePic.getWidth(), mGroupProfilePic.getHeight());
                if (croppedBitmap == null) {
                    Bitmap bitmap = Utils.decodeUriAsBitmap(this, android.net.Uri.parse(sdImageFile.toURI().toString()), mGroupProfilePic.getWidth(), mGroupProfilePic.getHeight());
                    mGroupProfilePic.setImageBitmap(bitmap);
                    mGroupProfilePicBitmap = bitmap;
                } else {
                    mGroupProfilePic.setImageBitmap(croppedBitmap);
                    mGroupProfilePicBitmap = croppedBitmap;
                }

                mGroupProfilePicSelected = true;
                mGroupProfilePicActionLinearLayout.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }else if(id == R.id.create_group_button){
            // validate data before going forward
            if(mGroupNameEditText.getText().toString().isEmpty()){
                showAlertDialog(getString(R.string.group_name_mandatory_error_msg));
                return true;
            }
            if (mUserContainer.getUserMap().size() == 0  &&
                    mGroupContainer.getGroupMap().size() == 0) {
                showAlertDialog(getString(R.string.at_least_one_member_or_group_mandatory_error_msg));
                return true;
            }

            // if validation passes, create baseGroup object and send createGroup request to database
            try {

                mBaseGroup = new BaseGroup(getApplicationContext());
                mBaseGroup.setGroupName(mGroupNameEditText.getText().toString());

                String groupProfilePicStr;

                if (!mGroupProfilePicSelected) {
                    mGroupProfilePicBitmap = Utils.drawableToBitmap(ContextCompat.getDrawable(getApplicationContext(),R.drawable.group_pic_icon));
                    mBaseGroup.setGroupProfilePicBase64Str(null);
                }else {
                    groupProfilePicStr = Utils.encodeToBase64(mGroupProfilePicBitmap);
                    mBaseGroup.setGroupProfilePicBase64Str(groupProfilePicStr);

                }
                mGroupAsyncHttpClient.createGroup(mUserIdStr, mBaseGroup, mUserContainer, mGroupContainer, mCreateGroupAsyncHttpResponseListener);

            } catch (JSONException e) {
                showAlertDialog("Unexpected Error occurred: " + e.toString());
            } catch (UnsupportedEncodingException e) {
                showAlertDialog("Unexpected Error occurred: " + e.toString());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private AsyncHttpResponseHandler mCreateGroupAsyncHttpResponseListener = new AsyncHttpResponseHandler() {
        @Override
        public void onStart() {
            mProgressDialog = getAndShowProgressDialog(CreateGroupActivity.this, getResources().getString(R.string.loading_msg));
        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (statusCode == 201) {
                try {
                    // extract group id from response
                    mBaseGroup.setGroupId(new JSONObject(new String(response)).getLong("groupId"));
                    dismissProgressDialog(mProgressDialog);
                    mProgressDialog = null;

                    mBaseGroup.setGroupLastModifiedTime(System.currentTimeMillis());
                    JSONObject jsonObject = new JSONObject(mBaseGroup.getGroupMapForLocal());

                    DcidrApplication.getInstance().getGlobalGroupContainer().populateGroup(jsonObject);

                    // send local broadcast to main activity for mGroupCustomArrayAdapter refresh
                    Intent localBroadcastIntent = new Intent("MainActivityGroupNotificationAction");
                    localBroadcastIntent.putExtra("SOURCE", "LOCAL");
                    localBroadcastIntent.putExtra("TARGET", "GROUP");
                    localBroadcastIntent.putExtra("ACTION", "REFRESH");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);

                    // navigate user to selected event page
                    String eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(mEventName) + "EventActivity";
                    Intent newEventIntent = new Intent(CreateGroupActivity.this,Class.forName(eventActivityClassName));
                    newEventIntent.putExtra(getResources().getString(R.string.source_key), getResources().getString(R.string.create_group_activity_class_name));
                    newEventIntent.putExtra(getResources().getString(R.string.event_type_key), Utils.capitalizeFirstLetter(mEventName));
                    newEventIntent.putExtra(getString(R.string.group_id), mBaseGroup.getGroupIdStr());
                    startActivity(newEventIntent);

                    finish();

                } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                    showAlertDialog("Unexpected Error occurred: " + e.toString());
                }
            }else {
                showAlertDialog("CreateGroup api call received http response other than 201");
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
                Toast toast = Toast.makeText(CreateGroupActivity.this, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
            showAlertDialog(errorString);
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
            mProgressDialog = getAndShowProgressDialog(CreateGroupActivity.this, getResources().getString(R.string.retrying_msg));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mGroupProfilePicActionLinearLayout.getVisibility() == View.VISIBLE){
            mGroupProfilePicActionLinearLayout.setVisibility(View.GONE);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserContainer.clear();
        mGroupContainer.clear();
        mGroupProfilePic.setImageBitmap(null);
        mGroupProfilePicBitmap = null;
    }
}