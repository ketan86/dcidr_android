package com.example.turbo.dcidr.gcm;

import android.os.Bundle;
import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Turbo on 2/22/2016.
 */

public class GcmMessageHandler extends GcmListenerService {

    /**
     * messages are received here when gcm pushes it
     * @param from describes message sender.
     * @param data message data as String key/value pairs.
     */
    public static final String TAG = "GcmMessageHandler";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[onMessageReceived] GCM message received");}
        String message = data.getString("message");
        String metadata = data.getString("metadata");
        handleMessage("GCM", message, metadata);
    }


    public void handleMessage(String source, String message, String metadata ){
        // setSharedPreferences by passing activity context.
        DcidrApplication.getInstance().getUserCache().setSharedPreferences(getBaseContext());
        // get userId from cache
        final String userId = DcidrApplication.getInstance().getUserCache().get("userId");
        if (BuildConfig.DEBUG){Log.i(TAG, "[handleMessage] userId is " + userId);}

        // if user is null, do not need to do anything
        if (userId == null){
            if (BuildConfig.DEBUG){Log.i(TAG, "[handleMessage] userId is null. returning");}
            return;
        }

        JSONObject jsonMetadataObject = null;
        try {
            jsonMetadataObject = new JSONObject(metadata);
            // get notification code and take action accordingly
            //        TODO : ignore messages received if not for current user. requires list of users in metatdata for verification. We will implement this if needed.
            //            if(!userId.equals(jsonMetadataObject.getString("targetUserIds"))){
            //    return;
            //}
        } catch (JSONException e) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[handleMessage] JSONException : " + e.toString());}
        }

        JSONObject jsonDataObject = null;
        try {
            jsonDataObject = new JSONObject(message);
        } catch (JSONException e1) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[handleMessage] JSONException : " + e1.toString());}
            return;
        }
        if (jsonDataObject !=null) {
            parseAndNotify(jsonDataObject, jsonMetadataObject, source);
        }
    }

    /**
     * parse and notify use with notification
     * @param jsonObject json object
     * @param source of the message
     */
    private void parseAndNotify(JSONObject jsonObject, JSONObject jsonMetadataObject, String source) {
        if (jsonObject.has("GROUP")) {
            if (BuildConfig.DEBUG){Log.i(TAG, "[parseAndNotify] Received data for GROUP");}
            com.example.turbo.dcidr.main.message_handler.GcmMessageHandler mainActivityMessageHandler = new com.example.turbo.dcidr.main.message_handler.GcmMessageHandler(getBaseContext());
            mainActivityMessageHandler.handleGroupMessage(jsonObject, jsonMetadataObject, source);
        } else if (jsonObject.has("EVENT")) {
            if (BuildConfig.DEBUG){Log.i(TAG, "[parseAndNotify] Received data for EVENT");}
            com.example.turbo.dcidr.main.message_handler.GcmMessageHandler mainActivityMessageHandler = new com.example.turbo.dcidr.main.message_handler.GcmMessageHandler(getBaseContext());
            mainActivityMessageHandler.handleEventMessage(jsonObject, jsonMetadataObject, source);
        }else if(jsonObject.has("FRIEND")){
            if (BuildConfig.DEBUG){Log.i(TAG, "[parseAndNotify] Received data for FRIEND");}
            com.example.turbo.dcidr.main.message_handler.GcmMessageHandler mainActivityMessageHandler = new com.example.turbo.dcidr.main.message_handler.GcmMessageHandler(getBaseContext());
            mainActivityMessageHandler.handleFriendMessage(jsonObject, jsonMetadataObject, source);
        }else if (jsonObject.has("CHWEET")) {
            if (BuildConfig.DEBUG){Log.i(TAG, "[parseAndNotify] Received data for CHWEET");}
            com.example.turbo.dcidr.main.message_handler.GcmMessageHandler mainActivityMessageHandler = new com.example.turbo.dcidr.main.message_handler.GcmMessageHandler(getBaseContext());
            mainActivityMessageHandler.handleChweetMessage(jsonObject, jsonMetadataObject, source);
        }else{
            if (BuildConfig.DEBUG){Log.e(TAG, "[parseAndNotify] Received data for unknown category");}
        }
    }
}