package com.example.turbo.dcidr.main.activity_helper.select_new_event_helper;

import android.util.Log;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.SelectNewEventActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.FetchManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/18/2016.
 */
public class SelectNewEventActivityHelper {

    private BaseActivity mBaseActivity;
    private EventAsyncHttpClient mSelectNewEventAsyncHttpClient;
    private FetchManager mFetchManager;
    private String mUserIdStr;
    private SelectNewEventActivity mSelectNewEventActivity;
    public SelectNewEventActivityHelper (SelectNewEventActivity activity) {
        this.mSelectNewEventActivity = activity;
        mSelectNewEventAsyncHttpClient = new EventAsyncHttpClient(this.mSelectNewEventActivity.getApplicationContext());
        mFetchManager = new FetchManager(5,10);
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");

    }

    /**
     * initialize groups from dcidr server
     */
    public void fetchEventTypes(int visibleStartIndex, int visibleEndIndex) {
        mFetchManager.fetch(visibleStartIndex, visibleEndIndex, new FetchManager.FetchInterface(){
            @Override
            public void onFetch(int offset, int limit) {
                if (mUserIdStr != null) {
                    mSelectNewEventAsyncHttpClient.getEventTypes(mUserIdStr, offset, limit, mGetGroupAsyncHttpResponseHandler);
                } else {
                    //// TODO: 2/14/2016
                    Log.i("Ketan", "User id is null");
                }
            }
        });
    }



    private AsyncHttpResponseHandler mGetGroupAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            // called before request is started
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            mSelectNewEventActivity.onFetchSuccess(statusCode,headers, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            mSelectNewEventActivity.onFetchFailure(statusCode, headers, errorResponse, e);
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
            Log.e("Retry", "Retry");
        }
    };

}
