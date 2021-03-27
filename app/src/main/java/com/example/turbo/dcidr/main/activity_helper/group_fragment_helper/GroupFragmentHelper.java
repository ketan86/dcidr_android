package com.example.turbo.dcidr.main.activity_helper.group_fragment_helper;

import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.GroupFragment;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.fetch_manager.AsyncHttpFetchManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/25/2016.
 */
public class GroupFragmentHelper {
    private BaseActivity mBaseActivity;
    private GroupFragment mGroupFragment;
    private AsyncHttpFetchManager mAsyncHttpFetchManager;
    private String mUserIdStr;
    public static final String TAG = "GroupFragmentHelper";

    public GroupFragmentHelper(GroupFragment groupFragment) {
        this.mGroupFragment = groupFragment;
        this.mBaseActivity = groupFragment.getBaseActivity();
        mAsyncHttpFetchManager = new AsyncHttpFetchManager(mBaseActivity, 5, 10);
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
    }

    /**
     * initialize groups from dcidr server
     */
    public void fetchGroups(int visibleStartIndex, int visibleEndIndex) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[fetchGroups] Fetching groups");}
        mAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                if (mUserIdStr != null) {
                    GroupAsyncHttpClient groupAsyncHttpClient = new GroupAsyncHttpClient(mBaseActivity.getApplicationContext());
                    groupAsyncHttpClient.getGroups(mUserIdStr, offset, limit, mAsyncHttpFetchManager.getAsyncHttpResponseHandler());
                } else {
                    if (BuildConfig.DEBUG){Log.e(TAG, "[fetchGroups] userId is null");}
                }
            }

            @Override
            public int postFetchSetEndIndex() {
                if (BuildConfig.DEBUG){Log.i(TAG, "[fetchGroups] setting index to actual group size");}
                return DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().size();
            }

            @Override
            public void onFetchStart() {
            }

            @Override
            public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
                mGroupFragment.onFetchSuccess(statusCode, headers, response);
            }

            @Override
            public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                mGroupFragment.onFetchFailure(statusCode, headers, errorResponse, e);
            }
        });
    }

    public void setUnreadEventCount(String groupIdStr, int count){
        if (mUserIdStr != null) {
            try {
                GroupAsyncHttpClient groupAsyncHttpClient = new GroupAsyncHttpClient(mBaseActivity.getApplicationContext());
                groupAsyncHttpClient.setGroupUnreadEvents(mUserIdStr, groupIdStr, count, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        if(statusCode != 200) {
                            if (BuildConfig.DEBUG){Log.e(TAG, "[setUnreadEventCount.onSuccess] Error setting unread event");}
                        }else {
                            if (BuildConfig.DEBUG){Log.i(TAG, "[setUnreadEventCount.onSuccess] Unread events are set successfully");}
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        if (BuildConfig.DEBUG){Log.i(TAG, "[setUnreadEventCount.onFailure] Failure setting unread event");}
                        mGroupFragment.onFetchFailure(statusCode, headers, errorResponse, e);
                    }

                });
            } catch (JSONException e) {
                if (BuildConfig.DEBUG){Log.e(TAG, "[setUnreadEventCount] JSONException: " + e.toString());}
            } catch (UnsupportedEncodingException e) {
                if (BuildConfig.DEBUG){Log.e(TAG, "[setUnreadEventCount] UnsupportedEncodingException: " +e.toString());}
            }
        } else {
            if (BuildConfig.DEBUG){Log.e(TAG, "[setUnreadEventCount] userId is null");}
        }
    }

}
