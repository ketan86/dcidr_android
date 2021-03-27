package com.example.turbo.dcidr.main.fetch_manager;

import android.content.Context;

import com.example.turbo.dcidr.main.activity_helper.FetchManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 7/5/2016.
 */
public class AsyncHttpFetchManager {
    private Context mContext;
    private FetchManager mFetchManager;
    private AsyncHttpResponseHandler mAsyncHttpResponseHandler;
    private AsyncHttpFetchManagerInterface mAsyncHttpFetchManagerInterface;
    public interface AsyncHttpFetchManagerInterface{
        public void onFetchRequested(int offset, int limit);
        public int postFetchSetEndIndex();
        public void onFetchStart();
        public void onFetchSuccess(int statusCode, Header[] headers, byte[] response);
        public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e);
    }

    public AsyncHttpResponseHandler getAsyncHttpResponseHandler(){
        return this.mAsyncHttpResponseHandler;
    }

    public FetchManager getFetchManager(){
        return this.mFetchManager;
    }

    public AsyncHttpFetchManager(Context context, int minGap, int maxGap){
        this.mContext = context;
        this.mFetchManager = new FetchManager(minGap, maxGap);
        this.mAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                mAsyncHttpFetchManagerInterface.onFetchStart();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                mAsyncHttpFetchManagerInterface.onFetchSuccess(statusCode, headers, response);
                mFetchManager.setEndIndex(mAsyncHttpFetchManagerInterface.postFetchSetEndIndex());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                mAsyncHttpFetchManagerInterface.onFetchFailure(statusCode, headers, errorResponse, e);
            }
        };
    }

    public void fetch(int visibleStartIndex, int visibleEndIndex, final AsyncHttpFetchManager.AsyncHttpFetchManagerInterface asyncHttpFetchManagerInterface) {
        this.mAsyncHttpFetchManagerInterface = asyncHttpFetchManagerInterface;
        mFetchManager.fetch(visibleStartIndex, visibleEndIndex, new FetchManager.FetchInterface() {
            @Override
            public void onFetch(int offset, int limit) {
                asyncHttpFetchManagerInterface.onFetchRequested(offset, limit);
            }
        });
    }
}
