package com.example.turbo.dcidr.main.activity_helper.single_fetch_helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 10/12/2016.
 */
public class GroupFetchHelper {
    private Context mContext;
    private String mUserIdStr;
    private GroupFetchInterface mGroupFetchInterface;
    public GroupFetchHelper(Context context){
        this.mContext = context;
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
    }

    public interface GroupFetchInterface {
        void onFetchDone();
    }

    public void onFetchDoneListener(GroupFetchInterface groupFetchInterface){
        this.mGroupFetchInterface = groupFetchInterface;
    }


    public void fetchGroup(final String groupIdStr) {
        final GroupAsyncHttpClient groupAsyncHttpClient = new GroupAsyncHttpClient(mContext);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                groupAsyncHttpClient.getGroup(mUserIdStr, groupIdStr, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                DcidrApplication.getInstance().getGlobalGroupContainer().populateGroup(new JSONObject(new String(responseBody)).getJSONObject("result"));
                                if(mGroupFetchInterface !=  null){
                                    mGroupFetchInterface.onFetchDone();
                                }
                            } catch (Exception e) {
                                Log.e("fetchGroup", "Error populating group");
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
                            Log.e("fetchGroup", "Error fetching group");
                        }
                    }
                });

            }

        });
    }
}
