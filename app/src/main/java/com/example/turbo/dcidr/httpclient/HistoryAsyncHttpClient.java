package com.example.turbo.dcidr.httpclient;

import android.content.Context;

import com.example.turbo.dcidr.global.DcidrConstant;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by Turbo on 2/13/2016.
 */
public class HistoryAsyncHttpClient extends BaseAsyncHttpClient {
    public HistoryAsyncHttpClient(Context context){
        super(context);
    }

    public void getHistory(String userId, int offset, int limit, AsyncHttpResponseHandler responseHandler) {
        String USER_HISTORY_GET_URL = DcidrConstant.USER_HISTORY_GET_URL.replace(":userId", userId);
        USER_HISTORY_GET_URL = USER_HISTORY_GET_URL + "?offset=" + offset + "&limit=" + limit;
        this.dcidrGet(USER_HISTORY_GET_URL, responseHandler);
    }

}
