package com.example.turbo.dcidr.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/22/2016.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    private String mSource;
    public RegistrationIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String source = intent.getStringExtra("SOURCE");
        if (BuildConfig.DEBUG){Log.i(TAG, "[onHandleIntent] Received intent from: " + source);}

        if(source != null) {
            if(source.equals("GCM")){
                mSource = source;
            }else if(source.equals("LOCAL")){
                mSource = source;
            }else {
                if (BuildConfig.DEBUG){Log.i(TAG, "[onHandleIntent] Received intent from unknown" );}
            }
        }

        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_defaultSenderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            DcidrApplication.getInstance().getUserCache().set("gcmRegToken", token);
            if (BuildConfig.DEBUG){Log.i(TAG, "[onHandleIntent] Gcm token is : " + token );}
            updateUserDevice(token);
        } catch (IOException e) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[onHandleIntent] IOException : " + e.toString() );}
        }
    }

    /**
     * method sends registration token to dcidr server
     * @param token token value
     */
    private void updateUserDevice(String token) {
        // no need to send deviceId since it is already been created during user singin, gcmRegToken set happens right after that
        UserAsyncHttpClient userAsyncHttpClient = new UserAsyncHttpClient(this);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("gcmRegToken", token);
        if (BuildConfig.DEBUG){Log.i(TAG, "[updateUserDevice] Updating gcmRegToken for userDevice");}
        try {
            userAsyncHttpClient.updateUserDevice(String.valueOf(DcidrApplication.getInstance().getUser().getUserId()), hashMap, mSetGcmRegTokenAsyncHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[updateUserDevice] UnsupportedEncodingException: " + e.toString());}
        }

    }
    /**
     * response handler for setGcmRegToken callback
     */
    private AsyncHttpResponseHandler mSetGcmRegTokenAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            if (BuildConfig.DEBUG){Log.i(TAG, "[updateUserDevice] Request started");}
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (BuildConfig.DEBUG){Log.i(TAG, "[updateUserDevice.onSuccess] Success");}
            if(statusCode == 200){
                // make sure user gets navigated to main activity via getDeviceCallbackRunnable.
                // If source is GCM then we don't want to do this.
                if (BuildConfig.DEBUG){Log.i(TAG, "[updateUserDevice.onSuccess] Token updated by " + mSource);}
                if (!mSource.equals("GCM")) {
                    if (BuildConfig.DEBUG){Log.i(TAG, "[updateUserDevice.onSuccess] Sending user to Main Activity ");}
                    Handler mainHandler = new Handler(getBaseContext().getMainLooper());
                    mainHandler.post(DcidrApplication.getInstance().getDeviceCallbackRunnable());
                }else {
                    if (BuildConfig.DEBUG){Log.i(TAG, "[updateUserDevice.onSuccess] No need to send user to Main Activity");}
                }
            }else{
                if (BuildConfig.DEBUG){Log.e(TAG, "[updateUserDevice.onSuccess] status code is not 200");}
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[updateUserDevice.onFailure] Failure");}
            JSONObject jsonObject = null;
            String errorString = null;
            try {
                jsonObject = new JSONObject(new String(errorResponse));
                errorString = (String) jsonObject.get("error");
                if (BuildConfig.DEBUG){Log.e(TAG, "[updateUserDevice.onFailure] Error: " + errorString);}
            } catch (JSONException error) {
                if (BuildConfig.DEBUG){Log.e(TAG, "[updateUserDevice.onFailure] JSONException: " + error.toString());}
            }
        }

    };
}
