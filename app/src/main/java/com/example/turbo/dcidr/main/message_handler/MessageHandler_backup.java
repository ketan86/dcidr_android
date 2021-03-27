//package com.example.turbo.dcidr.main.message_handler;
//
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import com.example.turbo.dcidr.android_activity.MainActivity;
//import com.example.turbo.dcidr.global.DcidrApplication;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by Turbo on 6/12/2016.
// */
//public class MessageHandler {
//
//    private Context mContext;
//    /**
//     * constructor method
//     * @param context BaseContext
//     */
//    public MessageHandler(Context context){
//        mContext = context;
//    }
//
//    /**
//     * route message to appropriate activity
//     * @param from source of the message
//     * @param message data of the message
//     */
//    public void route(String from, String message, String metadata) {
//        // setSharedPreferences by passing activity context.
//        DcidrApplication.getInstance().getUserCache().setSharedPreferences(mContext.getApplicationContext());
//        // get userId from cache
//        final String userId = DcidrApplication.getInstance().getUserCache().get("userId");
//        // if user is null, dont need to do anything
//        if (userId == null){
//            return;
//        }
//
//        JSONObject jsonMetadataObject = null;
//        try {
//            jsonMetadataObject = new JSONObject(metadata);
//            // get notification code and take action accordingly
//            //        TODO : ignore messages received if not for current user. requires list of users in metatdata for verification. We will implement this if needed.
//            //            if(!userId.equals(jsonMetadataObject.getString("targetUserIds"))){
//            //    return;
//            //}
//        } catch (JSONException e) {
//            Log.e("JSONException", "Data cannot be decoded as JSON. " + e.toString());
//        }
//
//        JSONObject jsonDataObject = null;
//        try {
//            jsonDataObject = new JSONObject(message);
//        } catch (JSONException e1) {
//            Log.e("JSONException", "Data cannot be decoded as JSON");
//            return;
//        }
//        if (jsonDataObject !=null) {
//            parseAndNotify(jsonDataObject, jsonMetadataObject, from);
//        }
//    }
//
//    /**
//     * parse and notify use with notification
//     * @param jsonObject json object
//     * @param from source of the message
//     */
//    private void parseAndNotify(JSONObject jsonObject, JSONObject jsonMetadataObject, String from) {
//        if (jsonObject.has("GROUP")) {
//            MainActivityMessageHandler mainActivityMessageHandler = new MainActivityMessageHandler(mContext);
//            mainActivityMessageHandler.handleGroupMessage(jsonObject, jsonMetadataObject, from);
//        }
//        if(jsonObject.has("EVENT")) {
//            MainActivityMessageHandler mainActivityMessageHandler = new MainActivityMessageHandler(mContext);
//            mainActivityMessageHandler.handleEventMessage(jsonObject, jsonMetadataObject, from);
//        }
//        if (jsonObject.has("CHAT")) {
//            Intent myIntent = new Intent(mContext.getApplicationContext(), MainActivity.class);
//            PendingIntent myPendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, myIntent, 0);
//            //sendNotification("Dcidr Notification", "Chat notification received", myPendingIntent);
//        }
//    }
//
//
//
//}
