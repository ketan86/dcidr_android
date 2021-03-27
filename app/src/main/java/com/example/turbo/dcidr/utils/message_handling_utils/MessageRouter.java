package com.example.turbo.dcidr.utils.message_handling_utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.MainActivity;
import com.example.turbo.dcidr.android_activity.SelectedGroupEventActivity;
import com.example.turbo.dcidr.global.DcidrApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Borat on 3/12/2016.
 */
public class MessageRouter {
    private Context mBaseContext;
    private static final int MESSAGE_NOTIFICATION_ID = 12345;
    private int mNotificationCode;
    /**
     * constructor method
     * @param context BaseContext
     */
    public MessageRouter(Context context){
        mBaseContext = context;
    }

    /**
     * route message to appropriate activity
     * @param from source of the message
     * @param message data of the message
     */
    public void route(String from, String message, String metadata) {
        // setSharedPreferences by passing activity context.
        DcidrApplication.getInstance().getUserCache().setSharedPreferences(mBaseContext.getApplicationContext());
        // get userId from cache
        final String userId = DcidrApplication.getInstance().getUserCache().get("userId");
        // if user is null, dont need to do anything
        if (userId == null){
            return;
        }

        JSONObject jsonMetadataObject = null;
        try {
            jsonMetadataObject = new JSONObject(metadata);
            // get notification code and take action accordingly
            mNotificationCode = jsonMetadataObject.getInt("notificationCode");
            //        TODO : ignore messages received if not for current user. requires list of users in metatdata for verification. We will implement this if needed.
            //            if(!userId.equals(jsonMetadataObject.getString("targetUserIds"))){
            //    return;
            //}
        } catch (JSONException e) {
            Log.e("JSONException", "Data cannot be decoded as JSON. "  + e.toString());
        }

        JSONObject jsonDataObject = null;
        try {
            jsonDataObject = new JSONObject(message);
        } catch (JSONException e1) {
            Log.e("JSONException", "Data cannot be decoded as JSON");
            return;
        }
        if (jsonDataObject !=null) {
            parseAndNotify(jsonDataObject, jsonMetadataObject, from);
        }
    }

    /**
     * parse and notify use with notification
     * @param jsonObject json object
     * @param from source of the message
     */
    private void parseAndNotify(JSONObject jsonObject, JSONObject jsonMetadataObject, String from) {
        if (jsonObject.has("GROUP")) {
            handleGroupMessage(jsonObject, jsonMetadataObject, from);
        }
        if(jsonObject.has("EVENT")) {
            handleEventMessage(jsonObject, jsonMetadataObject, from);
        }
        if (jsonObject.has("CHAT")) {
            Intent myIntent = new Intent(mBaseContext.getApplicationContext(), MainActivity.class);
            PendingIntent myPendingIntent = PendingIntent.getActivity(mBaseContext.getApplicationContext(), 0, myIntent, 0);
            sendNotification("Dcidr Notification", "Chat notification received", myPendingIntent);
        }
    }

    /**
     * method to push notification to user
     * @param title title of the notification
     * @param body body or desc of the notification
     * @param pendingIntent pendingIntent to be launched when notification is action is clicked
     */
    private void sendNotification(String title, String body, PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mBaseContext)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) mBaseContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * handle specific message
     * @param jsonObject json object
     * @param from source of the message
     */
    private void handleGroupMessage(JSONObject jsonObject, JSONObject jsonMetadataObject, String from){
        if(mNotificationCode > 0 && mNotificationCode < 100){
            // group related info change code range
            // local broadcast to populate data
            Intent localBroadcastIntent = new Intent("MainActivityGroupNotificationAction");
            localBroadcastIntent.putExtra("SOURCE", from);
            localBroadcastIntent.putExtra("TARGET", "GROUP");
            try {
                JSONObject groupJsonObj = jsonObject.getJSONObject("GROUP");
                localBroadcastIntent.putExtra("MSG", groupJsonObj.toString());
                LocalBroadcastManager.getInstance(mBaseContext.getApplicationContext()).sendBroadcast(localBroadcastIntent);
                // create pending intent for notification (no need to send actual data here)
                Intent notificationIntent = new Intent(mBaseContext.getApplicationContext(), MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mBaseContext.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification",
                        groupJsonObj.getString("groupName") + " Group created by " + jsonMetadataObject.getString("srcUserName"), pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "group key not found in jsonObject. " + e.toString());
            }
        }

    }
    /**
     * handle specific message
     * @param jsonObject json object
     * @param from source of the message
     */
    private void handleEventMessage(JSONObject jsonObject, JSONObject jsonMetadataObject, String from){

        if(mNotificationCode > 2999 && mNotificationCode < 3100) {
            // event specific info change code range
            // local broadcast to populate data
            Intent localBroadcastIntent = new Intent("MainActivityGroupNotificationAction");
            localBroadcastIntent.putExtra("SOURCE", from);
            localBroadcastIntent.putExtra("TARGET", "EVENT");
            JSONObject eventJsonObj = null;
            try {
                eventJsonObj = jsonObject.getJSONObject("EVENT");
                localBroadcastIntent.putExtra("MSG", eventJsonObj.toString());
            } catch (JSONException e) {
                Log.e("JSONException", "event key not found in jsonObject. " + e.toString());
            }
            LocalBroadcastManager.getInstance(mBaseContext.getApplicationContext()).sendBroadcast(localBroadcastIntent);
            // create pending intent for notification (need to send groupId to SelectedGroupEventActivity to open specific activity)
            Intent notificationIntent = new Intent(mBaseContext.getApplicationContext(), SelectedGroupEventActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // EVENT notification message is a list of event messages associated with diff groups, since we can't open all the groups
            // currently going to open the first SelectedGroupEventActivity.
            try {
                notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.selected_group_id), eventJsonObj.getString("groupId"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.event_id), eventJsonObj.getString("eventId"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.user_id), eventJsonObj.getString("userId"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.source_key), "GCM");
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.notification_code), mNotificationCode);
                PendingIntent pendingIntent = PendingIntent.getActivity(mBaseContext.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification", eventJsonObj.getString("eventName") + " Activity created under " + jsonMetadataObject.getString("groupName") + " by " + jsonMetadataObject.getString("srcUserName") + ".", pendingIntent);
                //sendNotification("Dcidr Notification", eventJsonObj.getString("eventName") + " Activity created in group " + jsonMetadataObject.getString("groupName"), pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "event not found in jsonObject. " + e.toString());
            }
        }

        else if(mNotificationCode > 3099 && mNotificationCode < 3200) {
            // event status change code range
            JSONObject eventJsonObj = null;
            try {
                eventJsonObj = jsonObject.getJSONObject("EVENT");

                // also send local broadcast for refresh data if selectedGroupEventActivity is currently open
                Intent localBroadcastIntent = new Intent(mBaseContext.getApplicationContext().getString(R.string.selected_group_event_notification_action_filter));
                localBroadcastIntent.putExtra("SOURCE", "LOCAL");
                localBroadcastIntent.putExtra("ACTION", "PUSH");

                localBroadcastIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.group_id), eventJsonObj.getString("groupId"));
                localBroadcastIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.event_id), eventJsonObj.getString("eventId"));
                localBroadcastIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.user_id), eventJsonObj.getString("userId"));
                localBroadcastIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.event_status_type), eventJsonObj.getString("eventStatusType"));
                localBroadcastIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.notification_code), String.valueOf(mNotificationCode));

                LocalBroadcastManager.getInstance(mBaseContext.getApplicationContext()).sendBroadcast(localBroadcastIntent);


                // create pending intent for notification (need to send groupId to SelectedGroupEventActivity to open specific activity)
                Intent notificationIntent = new Intent(mBaseContext.getApplicationContext(), SelectedGroupEventActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // EVENT notification message is a list of event messages associated with diff groups, since we can't open all the groups
                // currently going to open the first SelectedGroupEventActivity.

                notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.selected_group_id), eventJsonObj.getString("groupId"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.event_id), eventJsonObj.getString("eventId"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.user_id), eventJsonObj.getString("userId"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.event_status_type), eventJsonObj.getString("eventStatusType"));
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.source_key), "GCM");
                //notificationIntent.putExtra(mBaseContext.getApplicationContext().getString(R.string.notification_code), String.valueOf(mNotificationCode));
                PendingIntent pendingIntent = PendingIntent.getActivity(mBaseContext.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification", eventJsonObj.getString("eventName") + " Activity " + eventJsonObj.getString("eventStatusType").toLowerCase() + " by " + jsonMetadataObject.getString("srcUserName") + ".", pendingIntent);
                //sendNotification("Dcidr Notification", eventJsonObj.getString("eventName") + " Activity created in group " + jsonMetadataObject.getString("groupName"), pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "event not found in jsonObject. " + e.toString());
            }
        }

        else if(mNotificationCode > 4099 && mNotificationCode < 4200) {
            // buzz notification received
            // event status change code range
            JSONObject eventJsonObj = null;
            try {
                eventJsonObj = jsonObject.getJSONObject("EVENT");
                // create pending intent for notification (need to send groupId to SelectedGroupEventActivity to open specific activity)
                Intent notificationIntent = new Intent(mBaseContext.getApplicationContext(), SelectedGroupEventActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mBaseContext.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification", jsonMetadataObject.getString("srcUserName") + " just buzzed you for an activity " + eventJsonObj.getString("eventName") + ".", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "event not found in jsonObject. " + e.toString());
            }

            // vibration for 800 milliseconds
            ((Vibrator)mBaseContext.getSystemService(mBaseContext.VIBRATOR_SERVICE)).vibrate(2000);
        }

    }
}
