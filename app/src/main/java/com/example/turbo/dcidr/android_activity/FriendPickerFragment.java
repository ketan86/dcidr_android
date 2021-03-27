package com.example.turbo.dcidr.android_activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
//import com.example.turbo.dcidr.main.activity_helper.FetchInterface;
import com.example.turbo.dcidr.main.activity_helper.FetchManager;
import com.example.turbo.dcidr.main.activity_helper.friend_picker_fragment_helper.FriendPickerCustomBaseAdapter;
import com.example.turbo.dcidr.utils.common_utils.SmartSearch;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 4/9/2016.
 */
public class FriendPickerFragment extends DialogFragment {

    private ListView mFriendListView;
    private Button mFriendPickerOkBtn;
    private Button mFriendPickerCancelBtn;
    private EditText mFriendSearchView;
    private ImageView mSearchClearIcon;
    private CreateGroupActivity mCreateGroupActivity;
    private FetchManager mFetchManager;
    private String mUserIdStr;
    private ImageView mSearchIcon;
    private FriendPickerCustomBaseAdapter mFriendPickerCustomBaseAdapter;
    private UserAsyncHttpClient mUserAsyncHttpClient;
    private GroupAsyncHttpClient mGroupAsyncHttpClient;
    private SmartSearch mSmartSearch;
    private SmartSearch.OnFinishCallback mGetDataForSearchFinishCallback;
    private String mSearchStr;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFetchManager = new FetchManager(5,10);
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");

        mSmartSearch = new SmartSearch(mCreateGroupActivity);
        mSmartSearch.setDataForSearchCallback(new SmartSearch.GetDataForSearchInterface() {
            @Override
            public void get(String searchStr, SmartSearch.OnFinishCallback onFinishCallback) {
                mGetDataForSearchFinishCallback = onFinishCallback;
                mSearchStr = searchStr;
                mUserAsyncHttpClient.getActiveFriendsByQueryText(mUserIdStr, searchStr, 0, 100000, mFriendPickerUserAsyncHttpResponseHandler);
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = super.onCreateDialog(savedInstanceState);
        dlg.setTitle("Select friends,");
//        TextView titleTextView = (TextView) dlg.findViewById(android.R.id.title);
//        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
//        titleTextView.setTextSize(25);
        return dlg;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View friendPickerView = inflater.inflate(R.layout.fragment_friend_picker, container, false);
        mFriendListView = (ListView) friendPickerView.findViewById(R.id.friend_list_view);
        mFriendPickerOkBtn = (Button) friendPickerView.findViewById(R.id.friend_picker_ok_btn);
        mFriendPickerCancelBtn = (Button) friendPickerView.findViewById(R.id.friend_picker_cancel_btn);
        mFriendSearchView = (EditText) friendPickerView.findViewById(R.id.friend_search_view);
        mFriendSearchView.requestFocus();
        mSearchClearIcon = (ImageView) friendPickerView.findViewById(R.id.search_clear_icon);
        mSearchIcon = (ImageView) friendPickerView.findViewById(R.id.search_icon);
        //mSelectedFriendsRelativeLayout = (LinearLayout) friendPickerView.findViewById(R.id.selected_friends_relative_layout);
        mFriendPickerCustomBaseAdapter = new FriendPickerCustomBaseAdapter(mCreateGroupActivity);
        mFriendListView.setAdapter(mFriendPickerCustomBaseAdapter);
        mFriendPickerOkBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterFriendsList();
                mCreateGroupActivity.setSelectedFriends();
                dismiss();
            }
        });

        mFriendPickerCancelBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // go back to create group activity
                dismiss();
            }
        });

        mSearchClearIcon.setOnClickListener(new ImageView.OnClickListener(){

            @Override
            public void onClick(View v) {
                mFriendSearchView.setText("");
            }
        });

        mFriendSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mFriendPickerCustomBaseAdapter.clearArrayList();
                if (!s.toString().isEmpty()) {
                    mSearchIcon.setVisibility(View.GONE);
                    // lower the text
                    String newText = s.toString().toLowerCase();
                    filterFriendsList();
                    mSmartSearch.search(newText, new SmartSearch.OnSearchFinishCallback() {
                        @Override
                        public void call(ArrayList<?> arrayList) {
                            mFriendPickerCustomBaseAdapter.setData(arrayList);
                            mFriendPickerCustomBaseAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    mSearchIcon.setVisibility(View.VISIBLE);
                    mFriendPickerCustomBaseAdapter.notifyDataSetChanged();
                }
            }
        });


        return friendPickerView;
    }

    public void filterFriendsList(){
        // clear non-selected items

        Log.i("Ketan:F4", mCreateGroupActivity.getUserContainer().getUserMap().toString());
        Iterator<Long> userIt = mCreateGroupActivity.getUserContainer().getUserMap().keySet().iterator();
        while (userIt.hasNext()) {
            if (!mCreateGroupActivity.getUserContainer().getUserMap().get(userIt.next()).getIsSelected()) {
                userIt.remove();
            }
        }

        Iterator<Long> baseGroupIt = mCreateGroupActivity.getGroupContainer().getGroupMap().keySet().iterator();
        while (baseGroupIt.hasNext()) {
            if (!mCreateGroupActivity.getGroupContainer().getGroupMap().get(baseGroupIt.next()).getIsSelected()) {
                baseGroupIt.remove();
            }
        }

        Log.i("Ketan:F5", mCreateGroupActivity.getUserContainer().getUserMap().toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i("FLOW:Friend", "onAttach");
        mCreateGroupActivity = (CreateGroupActivity) activity;
        mUserAsyncHttpClient = new UserAsyncHttpClient(mCreateGroupActivity);
        mGroupAsyncHttpClient = new GroupAsyncHttpClient(mCreateGroupActivity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("FLOW:FriendContext", "onAttach");
        mCreateGroupActivity = (CreateGroupActivity) context;
        mUserAsyncHttpClient = new UserAsyncHttpClient(mCreateGroupActivity);
        mGroupAsyncHttpClient = new GroupAsyncHttpClient(mCreateGroupActivity);
    }

//    @Override
//    public void onFetch(int offset, int limit) {
//        if (mUserIdStr != null) {
//            mUserAsyncHttpClient.getActiveFriends(mUserIdStr, offset, limit, mFriendPickerUserAsyncHttpResponseHandler);
//        } else {
//            //// TODO: 2/14/2016
//            Log.i("Ketan", "User id is null");
//        }
//    }
//
//    @Override
//    public void onDestroy(int startIndex, int endIndex) {
//        // TODO -- Not needed now
//    }


    protected AsyncHttpResponseHandler mFriendPickerUserAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (statusCode == 200) {
                try {
                    Log.i("Ketan:F1", mCreateGroupActivity.getUserContainer().getUserMap().toString());

                    //scanSelected();
                    //mUserContainer.clearUserMap();
                    JSONArray jsonArray = new JSONObject(new String(response)).getJSONArray("result");
                    //populate the group container so as to create an array of basegroup objects
                    mCreateGroupActivity.getUserContainer().populateUser(jsonArray);
                    //for each row in the response, represented as a JSONObject in the JSONArray
                    //check whether groupId exists and if yes, then populate the EventContainer
                    //for that groupId with the newly received event information
                } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                    mCreateGroupActivity.showAlertDialog("Unexpected Error occurred" + e.toString());
                }

                mGroupAsyncHttpClient.getGroupsByQueryText(mUserIdStr,mSearchStr, 0, 100000, mFriendPickerGroupAsyncHttpResponseHandler);
                Log.i("Ketan:F2", mCreateGroupActivity.getUserContainer().getUserMap().toString());

            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            // if login is not successful, send failure code and handle it
            if(statusCode == 400 || statusCode == 403) {
                JSONObject jsonObject = null;
                String errorString = null;
                try {
                    jsonObject = new JSONObject(new String(errorResponse));
                    errorString = (String) jsonObject.get("error");
                } catch (JSONException error) {
                    Toast toast = Toast.makeText(mCreateGroupActivity, R.string.friends_search_failure_msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
                mCreateGroupActivity.showAlertDialog(errorString);
            }
        }
        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
        }
    };

    protected AsyncHttpResponseHandler mFriendPickerGroupAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (statusCode == 200) {
                try {
                    Log.i("Ketan:F1", mCreateGroupActivity.getGroupContainer().getGroupMap().toString());

                    //scanSelected();
                    //mUserContainer.clearUserMap();
                    JSONArray jsonArray = new JSONObject(new String(response)).getJSONArray("result");
                    //populate the group container so as to create an array of basegroup objects
                    mCreateGroupActivity.getGroupContainer().populateGroup(jsonArray);
                    //for each row in the response, represented as a JSONObject in the JSONArray
                    //check whether groupId exists and if yes, then populate the EventContainer
                    //for that groupId with the newly received event information
                } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                    mCreateGroupActivity.showAlertDialog("Unexpected Error occurred" + e.toString());
                }
                ArrayList<Object> finalUsersAndGroupsList = new ArrayList<>();
                finalUsersAndGroupsList.addAll(mCreateGroupActivity.getUserContainer().getUserList());
                finalUsersAndGroupsList.addAll(mCreateGroupActivity.getGroupContainer().getGroupList());
                mGetDataForSearchFinishCallback.call(finalUsersAndGroupsList);
            }
            Log.i("Ketan:F2", mCreateGroupActivity.getGroupContainer().getGroupMap().toString());
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            // if login is not successful, send failure code and handle it
            Log.i("Ketan:login", String.valueOf(statusCode));
            if(statusCode == 400 || statusCode == 403) {
                JSONObject jsonObject = null;
                String errorString = null;
                try {
                    jsonObject = new JSONObject(new String(errorResponse));
                    errorString = (String) jsonObject.get("error");
                } catch (JSONException error) {
                    Toast toast = Toast.makeText(mCreateGroupActivity, R.string.groups_search_failure_msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
                mCreateGroupActivity.showAlertDialog(errorString);
            }
        }
        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
        }
    };


}
