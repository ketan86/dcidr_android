package com.example.turbo.dcidr.main.activity_helper.history_fragment_helper;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.HistoryFragment;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.HistoryAsyncHttpClient;
import com.example.turbo.dcidr.main.fetch_manager.AsyncHttpFetchManager;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/13/2016.
 */
public class HistoryFragmentHelper {
    private BaseActivity mBaseActivity;
    private HistoryFragment mHistoryFragment;
    private HistoryAsyncHttpClient mHistoryAsyncHttpClient;
    private AsyncHttpFetchManager mAsyncHttpFetchManager;
    private String mUserIdStr;
    public HistoryFragmentHelper(HistoryFragment historyFragment) {
        this.mHistoryFragment = historyFragment;
        this.mBaseActivity = historyFragment.getBaseActivity();
        mHistoryAsyncHttpClient = new HistoryAsyncHttpClient(this.mBaseActivity.getApplicationContext());
        mAsyncHttpFetchManager = new AsyncHttpFetchManager(mBaseActivity, 5, 10);
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");

    }

    /**
     * initialize Historys from dcidr server
     */
    public void fetchHistory(int visibleStartIndex, int visibleEndIndex) {

        mAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                mHistoryAsyncHttpClient.getHistory(mUserIdStr, offset, limit, mAsyncHttpFetchManager.getAsyncHttpResponseHandler());
            }

            @Override
            public int postFetchSetEndIndex() {
                return DcidrApplication.getInstance().getGlobalHistoryContainer().getEventCount();
            }

            @Override
            public void onFetchStart() {

            }

            @Override
            public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
                mHistoryFragment.onFetchSuccess(statusCode, headers, response);
            }

            @Override
            public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                mHistoryFragment.onFetchFailure(statusCode, headers, errorResponse, e);

            }

        });
    }

}
