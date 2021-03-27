package com.example.turbo.dcidr.main.activity_helper.selected_group_event_activity_helper;

import android.app.ProgressDialog;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.SelectedGroupEventActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.main.fetch_manager.AsyncHttpFetchManager;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 4/7/2016.
 */
public class SelectedGroupEventActivityHelper {
    private SelectedGroupEventActivity mSelectedGroupEventActivity;
    private AsyncHttpFetchManager mAsyncHttpFetchManager;
    private ProgressDialog mProgressDialog;
    private String mUserIdStr;
    public SelectedGroupEventActivityHelper(SelectedGroupEventActivity selectedGroupEventActivity) {
        this.mSelectedGroupEventActivity = selectedGroupEventActivity;
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
        // create fetch manager with default start and end index, later it can be overridden with initFetchManager
        mAsyncHttpFetchManager = new AsyncHttpFetchManager(this.mSelectedGroupEventActivity, 5, 10);
    }

    public void setFetchManagerEndIndex(int endIndex){
        // destroy old and creating a new fetch manager
        mAsyncHttpFetchManager.getFetchManager().setEndIndex(endIndex);
    }

    /**
     * initialize groups from dcidr server
     */
    public void fetchEvents(int visibleStartIndex, int visibleEndIndex) {
        final EventAsyncHttpClient eventAsyncHttpClient = new EventAsyncHttpClient(this.mSelectedGroupEventActivity.getApplicationContext());
        mAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                // show progress bar
                mProgressDialog = mSelectedGroupEventActivity.getAndShowProgressDialog(mSelectedGroupEventActivity, mSelectedGroupEventActivity.getResources().getString(R.string.loading_msg));
                eventAsyncHttpClient.getEvents(mUserIdStr, mSelectedGroupEventActivity.getBaseGroup().getGroupIdStr(), offset, limit, mAsyncHttpFetchManager.getAsyncHttpResponseHandler());
            }

            @Override
            public int postFetchSetEndIndex() {
                return DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(mSelectedGroupEventActivity.getBaseGroup().getGroupId()).getEventContainer().getEventList().size();
            }

            @Override
            public void onFetchStart() {

            }

            @Override
            public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
                mSelectedGroupEventActivity.dismissProgressDialog(mProgressDialog);
                mSelectedGroupEventActivity.onFetchSuccess(statusCode,headers, response);
            }

            @Override
            public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                mSelectedGroupEventActivity.onFetchFailure(statusCode, headers, errorResponse, e);
            }

        });
    }
}
