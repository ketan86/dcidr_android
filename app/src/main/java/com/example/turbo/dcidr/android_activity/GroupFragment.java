package com.example.turbo.dcidr.android_activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.group_fragment_helper.GroupFragmentCustomArrayAdapter;
import com.example.turbo.dcidr.main.activity_helper.group_fragment_helper.GroupFragmentHelper;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 3/10/2016.
 */
public class GroupFragment extends Fragment {
    private BaseActivity mBaseActivity;
    private ListView mGroupListView;
    private GroupFragmentCustomArrayAdapter mGroupCustomArrayAdapter;
    private GroupFragmentHelper mGroupFragmentHelper;
    private View groupView;
    private ProgressDialog mProgressDialog;
    public static final String TAG = "GroupFragment";

    public BaseActivity getBaseActivity(){
        return mBaseActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create a time handler
        final Handler timerHandler = new Handler();
        // create a time runnable object
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                // get reference to decide by timer and update it
                if(mGroupCustomArrayAdapter != null) {
                    for (int i = 0; i < mGroupCustomArrayAdapter.getCount(); i++) {
                        BaseGroup baseGroup = mGroupCustomArrayAdapter.getItem(i);
                        if (baseGroup != null) {
                            View view = mGroupListView.getChildAt(i);
                            if (view != null) {
                                TextView textView = (TextView) view.findViewById(R.id.group_last_modified_time);
                                textView.setText(Utils.convertDateToMeaningfulText(baseGroup.getGroupLastModifiedTime(), false));
                            }
                        }
                    }
                }
                // set timer handler to run this runnable after every minute. It will set
                // this to run every time run method is executed
                timerHandler.postDelayed(this, 60000); //run every minute
                Log.i("[GroupFragment]",".timerRunnable's timer event callback");
            }
        };
        // run time runnable
        timerRunnable.run();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[onCreateView] Group fragment created");}
        if(groupView == null) {
            groupView = inflater.inflate(R.layout.fragment_group, container, false);
            if (mBaseActivity != null) {
                // init group list view
                mProgressDialog = mBaseActivity.getAndShowProgressDialog(mBaseActivity, getResources().getString(R.string.loading_msg));
                mGroupListView = (ListView) groupView.findViewById(R.id.group_list_view);
                mGroupCustomArrayAdapter = new GroupFragmentCustomArrayAdapter(mBaseActivity, R.id.group_list_view, DcidrApplication.getInstance().getGlobalGroupContainer().getGroupList());
                mGroupListView.setAdapter(mGroupCustomArrayAdapter);
                mGroupListView.setOnItemClickListener(mGroupItemClickListener);
                mGroupListView.setOnScrollListener(mGroupScrollListener);
                // create group fragment helper and initialize group
                mGroupFragmentHelper = new GroupFragmentHelper(this);
                mGroupFragmentHelper.fetchGroups(0, 5);
            }else {
                if (BuildConfig.DEBUG){Log.e(TAG, "[onCreateView] BaseActivity is null");}
            }
        }
        return groupView;
    }



    @Override
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[onAttach] Attaching group fragment");}
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    private ListView.OnScrollListener mGroupScrollListener = new ListView.OnScrollListener(){
        int mFirstVisibleItem;
        int mVisibleItemCount;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == 0){
                mGroupFragmentHelper.fetchGroups(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount - 1);
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            Log.i("a", "firstVisibleItem" + String.valueOf(firstVisibleItem));
            Log.i("a", "mFirstVisibleItem" + String.valueOf(mFirstVisibleItem));
//
//            if (mFirstVisibleItem <= firstVisibleItem ) {
//                ActionBar actionBar = mBaseActivity.getSupportActionBar();
//                actionBar.hide();
//                Log.i("a", "scrolling down...");
//            } else {
//                ActionBar actionBar = mBaseActivity.getSupportActionBar();
//                actionBar.show();
//                Log.i("a", "scrolling up...");
//            }



            if (firstVisibleItem != 0 && visibleItemCount != 0) {
                //final int currentFirstVisibleItem = mGroupListView.getFirstVisiblePosition();
                if (firstVisibleItem > mFirstVisibleItem) {
                    ActionBar actionBar = mBaseActivity.getSupportActionBar();
                    actionBar.hide();
                } else if (firstVisibleItem < mFirstVisibleItem) {
                    ActionBar actionBar = mBaseActivity.getSupportActionBar();
                    actionBar.show();
                }

                this.mFirstVisibleItem = firstVisibleItem;
                this.mVisibleItemCount = visibleItemCount;
            }




        }
    };

    private ListView.OnItemClickListener mGroupItemClickListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BaseGroup group = mGroupCustomArrayAdapter.getItem(position);

            // if unread count is already 0, no need to do anything
            if(group.getUnreadEventCount() > 0){
                if (BuildConfig.DEBUG){Log.i(TAG, "[onItemClick] Setting event unread count to 0");}
                group.setUnreadEventCount(0);
                // set unread event count to 0 on database
                mGroupFragmentHelper.setUnreadEventCount(group.getGroupIdStr(), 0);
                // refresh GroupContainer after setting unreadEventCount
                mGroupCustomArrayAdapter.notifyDataSetChanged();
            }

            if (BuildConfig.DEBUG){Log.i(TAG, "[onItemClick] Navigating to SelectedGroupEventActivity");}
            Intent selectedGroupEventActivity = new Intent(mBaseActivity, SelectedGroupEventActivity.class);
            selectedGroupEventActivity.putExtra(getString(R.string.selected_group_id), group.getGroupIdStr());
            selectedGroupEventActivity.putExtra(getString(R.string.source_key), "LOCAL");
            startActivity(selectedGroupEventActivity);
        }
    };

    public GroupFragmentCustomArrayAdapter getGroupCustomArrayAdapter(){
        return mGroupCustomArrayAdapter;
    }

    public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[onFetchSuccess] Success fetching groups");}
        if (statusCode == 200) {
            try {
                DcidrApplication.getInstance().getGlobalGroupContainer().populateGroup(new JSONObject(new String(response)).getJSONArray("result"));
            } catch (Exception e) {
                if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchSuccess] Exception: "+ e.toString());}
                mBaseActivity.showAlertDialog("Ops, we have encounter a problem. we are working on it.");
            }
            DcidrApplication.getInstance().getGlobalGroupContainer().refreshGroupList();
            mGroupCustomArrayAdapter.notifyDataSetChanged();
            mBaseActivity.dismissProgressDialog(mProgressDialog);

            // let the GC claim the progress bar memeory
            mProgressDialog = null;
        }
    }

    public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchFailure] Failure getting groups");}
        JSONObject jsonObject = null;
        String errorString = null;
        try {
            jsonObject = new JSONObject(new String(errorResponse));
            errorString = (String) jsonObject.get("error");
            if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchFailure] Error: " + errorString);}
        } catch (JSONException error) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchFailure] JSONException: " + error.toString());}
        }
        mBaseActivity.showAlertDialog("Ops, we have encounter a problem. we are working on it.");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG){Log.i(TAG, "[onResume] Refreshing listview");}
        mGroupCustomArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DcidrApplication.getInstance().getGlobalGroupContainer().clear();
        mProgressDialog = null;
    }
}

