package com.example.turbo.dcidr.main.activity_helper.single_fetch_helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 10/12/2016.
 */
public class EventFetchHelper {

    private Context mContext;
    private String mUserIdStr;
    private EventFetchInterface mEventFetchInterface;
    public EventFetchHelper(Context context){
        this.mContext = context;
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
    }

    public interface EventFetchInterface {
        void onFetchDone();
    }

    public void onFetchDoneListener(EventFetchInterface EventFetchInterface){
        this.mEventFetchInterface = EventFetchInterface;
    }


    public void fetchEvent(final String groupIdStr, final String eventIdStr, final String eventTypeIdStr) {
        final EventAsyncHttpClient EventAsyncHttpClient = new EventAsyncHttpClient(mContext);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                EventAsyncHttpClient.getEvent(mUserIdStr, groupIdStr, eventIdStr, eventTypeIdStr, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                BaseGroup baseGroup = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(Long.valueOf(groupIdStr));
                                baseGroup.getEventContainer().populateEvent(new JSONObject(new String(responseBody)).getJSONArray("result"));
                                if(mEventFetchInterface !=  null){
                                    mEventFetchInterface.onFetchDone();
                                }
                            } catch (Exception e) {
                                Log.e("fetchEvent", "Error populating Event");
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        JSONObject jsonObject = null;
                        String errorString = null;
                        try {
                            jsonObject = new JSONObject(new String(responseBody));
                            errorString = (String) jsonObject.get("error");
                        } catch (JSONException e) {
                            Log.e("fetchEvent", "Error fetching Event");
                        }
                    }
                });

            }

        });
    }
    
    
}
