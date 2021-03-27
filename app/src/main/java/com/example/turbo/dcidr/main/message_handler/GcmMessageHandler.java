package com.example.turbo.dcidr.main.message_handler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.ContactInvitation;
import com.example.turbo.dcidr.android_activity.EventTimelineActivity;
import com.example.turbo.dcidr.android_activity.MainActivity;
import com.example.turbo.dcidr.android_activity.SelectedGroupEventActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.single_fetch_helper.EventFetchHelper;
import com.example.turbo.dcidr.main.activity_helper.single_fetch_helper.GroupFetchHelper;
import com.example.turbo.dcidr.main.container.EventContainer;
import com.example.turbo.dcidr.main.container.GroupContainer;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.main.user.Contact;
import com.example.turbo.dcidr.main.user.UserEventStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Turbo on 6/12/2016.
 */
public class GcmMessageHandler {
    private Context mContext;
    private int mNotificationCode;
    private final int MESSAGE_NOTIFICATION_ID = 1;
    public GcmMessageHandler(Context context){
        this.mContext = context;
    }
    
    public void handleGroupMessage(JSONObject jsonObject, JSONObject jsonMetadataObject, String from){
        try {
            mNotificationCode = jsonMetadataObject.getInt("notificationCode");
        } catch (JSONException e) {
            Log.e("handleGroupMessage", "Error parsing notification code");
            return;
        }

        if(mNotificationCode == NotificationCodes.GROUP_CREATE_NTF_CODE) {
            try {
                JSONObject groupJsonObj = jsonObject.getJSONObject("GROUP");
                DcidrApplication.getInstance().getGlobalGroupContainer().populateGroup(groupJsonObj);
                BaseGroup baseGroup = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupJsonObj.getLong("groupId"));
                baseGroup.incrementUnreadEventCount();
                // send local broadcast to main activity for mGroupCustomArrayAdapter refresh
                Intent localBroadcastIntent = new Intent("MainActivityGroupNotificationAction");
                localBroadcastIntent.putExtra("SOURCE", from);
                localBroadcastIntent.putExtra("TARGET", "GROUP");
                localBroadcastIntent.putExtra("ACTION", "REFRESH");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                Intent notificationIntent = new Intent(mContext, MainActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification",
                        groupJsonObj.getString("groupName") + " Group created by " + jsonMetadataObject.getString("srcUserName"), pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "group key not found in jsonObject. " + e.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleEventMessage(JSONObject jsonObject, JSONObject jsonMetadataObject, final String from){
        try {
            mNotificationCode = jsonMetadataObject.getInt("notificationCode");
        } catch (JSONException e) {
            Log.e("handleEventMessage", "Error parsing notification code");
            return;
        }

        if(mNotificationCode == NotificationCodes.EVENT_CREATE_NTF_CODE) {
            // event specific info change code range
            JSONObject eventJsonObj = null;
            try {
                eventJsonObj = jsonObject.getJSONObject("EVENT");
                Long groupId = eventJsonObj.getLong("groupId");
                if(groupId != null) {
                    GroupContainer globalGroupContainer = DcidrApplication.getInstance().getGlobalGroupContainer();
                    BaseGroup baseGroup = globalGroupContainer.getGroupMap().get(groupId);
                    Long groupLastModifiedTime = eventJsonObj.getLong("groupLastModifiedTime");
                    Long eventId = eventJsonObj.getLong("eventId");
                    Long parentEventId = eventJsonObj.getLong("parentEventId");
                    eventJsonObj.put("createdByName", jsonMetadataObject.getString("srcUserName"));
                    if(baseGroup!= null) {
                        if(groupLastModifiedTime != null){
                            baseGroup.setGroupLastModifiedTime(groupLastModifiedTime);
                        }

                        // check if event is child or parentEvent
                        if(parentEventId == -1) {
                            baseGroup.getEventContainer().populateEvent(eventJsonObj);
                            baseGroup.incrementUnreadEventCount();
                            baseGroup.incrementTotalEventCount();
                        }else {
                            if(eventId != null) {
                                EventContainer eventContainer  = baseGroup.getEventContainer();
                                BaseEvent parentEvent = eventContainer.getEventMap().get(parentEventId);
                                if(parentEvent != null) {
                                    parentEvent.getChildEventsContainer().populateEvent(eventJsonObj);
                                }else {
                                    //TODO need to fetch the parent event and push child event into it
                                }
                                baseGroup.incrementUnreadEventCount();
                            }
                        }

                        // send local broadcast to main activity for mGroupCustomArrayAdapter refresh, mainly for unreadEventCount
                        Intent mainActivityLocalBroadcastIntent = new Intent(mContext.getString(R.string.main_activity_notification_action_filter));
                        mainActivityLocalBroadcastIntent.putExtra("SOURCE", from);
                        mainActivityLocalBroadcastIntent.putExtra("TARGET", "GROUP");
                        mainActivityLocalBroadcastIntent.putExtra("ACTION", "REFRESH");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mainActivityLocalBroadcastIntent);


                        // send local broadcast to selectedGroupEventActivity to take care of the condition where activity is already open
                        Intent selectedGroupEventActivityLocalBroadcastIntent = new Intent(mContext.getString(R.string.selected_group_event_notification_action_filter));
                        selectedGroupEventActivityLocalBroadcastIntent.putExtra("SOURCE", "GCM");
                        selectedGroupEventActivityLocalBroadcastIntent.putExtra("ACTION", "REFRESH");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(selectedGroupEventActivityLocalBroadcastIntent);

                        // send local broadcast to main activity for mGroupCustomArrayAdapter refresh, mainly for unreadEventCount
                        Intent eventTimeLineBroadcastIntent = new Intent(mContext.getString(R.string.event_timeline_notification_action_filter));
                        eventTimeLineBroadcastIntent.putExtra("SOURCE", from);
                        eventTimeLineBroadcastIntent.putExtra("TYPE", "EVENT");
                        eventTimeLineBroadcastIntent.putExtra("ACTION", "REFRESH");
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(eventTimeLineBroadcastIntent);


                    }else {
                        // group is not yet fetched by user but received event notification.
                        // need to fetch group, update fetch manager end index
                        //mGroupFragmentHelper.fetchGroup(String.valueOf(groupId));
                        //mGroupFragmentHelper.getFetchManager().incrementEndIndex();
                        GroupFetchHelper groupFetchHelper = new GroupFetchHelper(mContext);
                        groupFetchHelper.fetchGroup(String.valueOf(groupId));
                        groupFetchHelper.onFetchDoneListener(new GroupFetchHelper.GroupFetchInterface() {
                            @Override
                            public void onFetchDone() {
                                // send local broadcast to main activity for mGroupCustomArrayAdapter refresh, mainly for unreadEventCount
                                Intent mainActivityLocalBroadcastIntent = new Intent(mContext.getString(R.string.main_activity_notification_action_filter));
                                mainActivityLocalBroadcastIntent.putExtra("SOURCE", from);
                                mainActivityLocalBroadcastIntent.putExtra("TARGET", "GROUP");
                                mainActivityLocalBroadcastIntent.putExtra("ACTION", "REFRESH");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(mainActivityLocalBroadcastIntent);


                                // send local broadcast to selectedGroupEventActivity to take care of the condition where activity is already open
                                Intent selectedGroupEventActivityLocalBroadcastIntent = new Intent(mContext.getString(R.string.selected_group_event_notification_action_filter));
                                selectedGroupEventActivityLocalBroadcastIntent.putExtra("SOURCE", "GCM");
                                selectedGroupEventActivityLocalBroadcastIntent.putExtra("ACTION", "REFRESH");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(selectedGroupEventActivityLocalBroadcastIntent);

                                // send local broadcast to main activity for mGroupCustomArrayAdapter refresh, mainly for unreadEventCount
                                Intent eventTimeLineBroadcastIntent = new Intent(mContext.getString(R.string.event_timeline_notification_action_filter));
                                eventTimeLineBroadcastIntent.putExtra("SOURCE", from);
                                eventTimeLineBroadcastIntent.putExtra("TYPE", "EVENT");
                                eventTimeLineBroadcastIntent.putExtra("ACTION", "REFRESH");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(eventTimeLineBroadcastIntent);

                            }
                        });
                    }
                }




            } catch (JSONException e) {
                Log.e("JSONException", "event key not found in jsonObject. " + e.toString());
            }

            // create pending intent for notification (need to send groupId to SelectedGroupEventActivity to open specific activity)
            Intent notificationIntent = new Intent(mContext, SelectedGroupEventActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // EVENT notification message is a list of event messages associated with diff groups, since we can't open all the groups
            // currently going to open the first SelectedGroupEventActivity.
            try {
                notificationIntent.putExtra(mContext.getString(R.string.selected_group_id), eventJsonObj.getString("groupId"));
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification", eventJsonObj.getString("eventName") + " Activity created under " + jsonMetadataObject.getString("groupName") + " by " + jsonMetadataObject.getString("srcUserName") + ".", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "event not found in jsonObject. " + e.toString());
            }
        }

        else if(mNotificationCode > 3099 && mNotificationCode < 3200) {
            // event status change code range
            JSONObject eventJsonObj = null;
            try {
                eventJsonObj = jsonObject.getJSONObject("EVENT");
                Long groupId = eventJsonObj.getLong("groupId");
                Long eventId = eventJsonObj.getLong("eventId");
                Long userId = eventJsonObj.getLong("userId");
                Long parentEventId = eventJsonObj.getLong("parentEventId");
                String eventStatusType = eventJsonObj.getString("eventStatusType");
                BaseGroup baseGroup = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupId);

                BaseEvent baseEvent = null;
                EventContainer eventContainer = baseGroup.getEventContainer();
                if(eventContainer != null ) {
                    if (parentEventId == -1) {
                        baseEvent = baseGroup.getEventContainer().getEventMap().get(eventId);
                        // update last modified time of parent event for sorting
                        baseEvent.setEventLastModifiedTime(System.currentTimeMillis());
                    } else {
                        BaseEvent parentEvent = baseGroup.getEventContainer().getEventMap().get(parentEventId);
                        if (parentEvent != null) {
                            // update last modified time of parent event for sorting
                            parentEvent.setEventLastModifiedTime(System.currentTimeMillis());
                            baseEvent = parentEvent.getChildEventsContainer().getEventMap().get(eventId);
                        }

                        // change user event status for parent event to decline events to decline
                        UserEventStatus userEventStatus = parentEvent.getUserEventStatusContainer().getUserEventStatusObj(userId);
                        if (userEventStatus != null) {
                            userEventStatus.setEventStatusType(UserEventStatus.EventStatusType.DECLINED);
                        }
                    }

                    // change user event status for the event that user has accepted or declined
                    if (baseEvent != null) {
                        UserEventStatus userEventStatus = baseEvent.getUserEventStatusContainer().getUserEventStatusObj(userId);
                        if (userEventStatus != null) {
                            userEventStatus.setEventStatusType(UserEventStatus.EventStatusType.valueOf(eventStatusType));
                        }

                        // for all child events
                        for(BaseEvent childEvent : baseEvent.getChildEventsContainer().getEventList()){
                            UserEventStatus childEventUserEventStatus = childEvent.getUserEventStatusContainer().getUserEventStatusObj(userId);
                            if(childEventUserEventStatus != null) {
                                childEventUserEventStatus.setEventStatusType(UserEventStatus.EventStatusType.DECLINED);
                            }
                        }
                    }

                }
                // send local broadcast to selectedGroupEventActivity to take care of the condition where activity is already open
                Intent localBroadcastIntent = new Intent(mContext.getString(R.string.event_timeline_notification_action_filter));
                localBroadcastIntent.putExtra("SOURCE", from);
                localBroadcastIntent.putExtra("TYPE", "EVENT");
                localBroadcastIntent.putExtra("ACTION", "REFRESH");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                // send local broadcast to selectedGroupEventActivity to take care of the condition where activity is already open
                Intent localBroadcastIntent2 = new Intent(mContext.getString(R.string.selected_group_event_notification_action_filter));
                localBroadcastIntent2.putExtra("SOURCE", from);
                localBroadcastIntent2.putExtra("ACTION", "REFRESH");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent2);

                // create pending intent for notification (need to send groupId to SelectedGroupEventActivity to open specific activity)
                Intent notificationIntent = new Intent(mContext, EventTimelineActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // EVENT notification message is a list of event messages associated with diff groups, since we can't open all the groups
                // currently going to open the first SelectedGroupEventActivity.

                notificationIntent.putExtra(mContext.getString(R.string.selected_group_id), eventJsonObj.getString("groupId"));
                if(parentEventId == -1) {
                    notificationIntent.putExtra(mContext.getString(R.string.selected_event_id), eventJsonObj.getString("eventId"));
                }else {
                    notificationIntent.putExtra(mContext.getString(R.string.selected_event_id), eventJsonObj.getString("parentEventId"));

                }
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification", eventJsonObj.getString("eventName") + " Activity " + eventJsonObj.getString("eventStatusType").toLowerCase() + " by " + jsonMetadataObject.getString("srcUserName") + ".", pendingIntent);
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
                Intent notificationIntent = new Intent(mContext, SelectedGroupEventActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification", jsonMetadataObject.getString("srcUserName") + " just buzzed you for an activity " + eventJsonObj.getString("eventName") + ".", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "event not found in jsonObject. " + e.toString());
            }

            // vibration for 800 milliseconds
            ((Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE)).vibrate(2000);
        }
    }

    /**
     * method to push notification to user
     * @param title title of the notification
     * @param body body or desc of the notification
     * @param pendingIntent pendingIntent to be launched when notification is action is clicked
     */
    private void sendNotification(String title, String body, PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }


    public void handleChweetMessage(final JSONObject jsonObject, final JSONObject jsonMetadataObject, final String source){
        try {
            mNotificationCode = jsonMetadataObject.getInt("notificationCode");
        } catch (JSONException e) {
            Log.e("handleFriendMessage", "Error parsing notification code");
            return;
        }

        if (mNotificationCode == NotificationCodes.CHWEET_UPDATE_NTF_CODE) {
            JSONObject chweetJsonObj = null;
            try {
                chweetJsonObj = jsonObject.getJSONObject("CHWEET");
                final Long groupId = chweetJsonObj.getLong("groupId");
                final Long parentEventId = chweetJsonObj.getLong("eventId");
                final int parentEventTypeId = chweetJsonObj.getInt("eventTypeId");
                Long userId = chweetJsonObj.getLong("userId");

                final BaseGroup baseGroup = DcidrApplication.getInstance().getGlobalGroupContainer().getGroupMap().get(groupId);
                if(baseGroup == null) {
                    GroupFetchHelper groupFetchHelper = new GroupFetchHelper(mContext);
                    groupFetchHelper.fetchGroup(String.valueOf(groupId));
                    final JSONObject finalChweetJsonObj = chweetJsonObj;
                    groupFetchHelper.onFetchDoneListener(new GroupFetchHelper.GroupFetchInterface() {
                        @Override
                        public void onFetchDone() {
                            EventFetchHelper eventFetchHelper = new EventFetchHelper(mContext);
                            eventFetchHelper.fetchEvent(String.valueOf(groupId), String.valueOf(parentEventId), BaseEvent.EventType.getType(parentEventTypeId).toString());
                            eventFetchHelper.onFetchDoneListener(new EventFetchHelper.EventFetchInterface() {

                                @Override
                                public void onFetchDone() {
                                    // send broadcast message to contactInvitation activity if open
                                    Intent localBroadcastIntent = new Intent(mContext.getString(R.string.event_timeline_notification_action_filter));
                                    localBroadcastIntent.putExtra("SOURCE", source);
                                    localBroadcastIntent.putExtra("TYPE", "CHWEET");
                                    localBroadcastIntent.putExtra("ACTION", "REFRESH");
                                    localBroadcastIntent.putExtra("MSG", finalChweetJsonObj.toString());
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                                    Intent notificationIntent = new Intent(mContext, EventTimelineActivity.class);
                                    notificationIntent.putExtra(mContext.getString(R.string.source_key), mContext.getString(R.string.gcm_message_handler_class_name));
                                    try {
                                        notificationIntent.putExtra(mContext.getString(R.string.selected_group_id), finalChweetJsonObj.getString("groupId"));
                                        notificationIntent.putExtra(mContext.getString(R.string.selected_event_id), finalChweetJsonObj.getString("eventId"));
                                        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        // send notification
                                        sendNotification("Dcidr Notification",
                                                jsonMetadataObject.getString("srcUserName") + " has sent you a chweet ", pendingIntent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    });
                }else {
                    BaseEvent baseEvent = baseGroup.getEventContainer().getEventMap().get(parentEventId);
                    if(baseEvent == null) {
                        EventFetchHelper eventFetchHelper = new EventFetchHelper(mContext);
                        eventFetchHelper.fetchEvent(String.valueOf(groupId), String.valueOf(parentEventId), BaseEvent.EventType.getType(parentEventTypeId).toString());
                        final JSONObject finalChweetJsonObj1 = chweetJsonObj;
                        eventFetchHelper.onFetchDoneListener(new EventFetchHelper.EventFetchInterface() {

                            @Override
                            public void onFetchDone() {
                                // send broadcast message to contactInvitation activity if open
                                Intent localBroadcastIntent = new Intent(mContext.getString(R.string.event_timeline_notification_action_filter));
                                localBroadcastIntent.putExtra("SOURCE", source);
                                localBroadcastIntent.putExtra("TYPE", "CHWEET");
                                localBroadcastIntent.putExtra("ACTION", "REFRESH");
                                localBroadcastIntent.putExtra("MSG", finalChweetJsonObj1.toString());
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                                Intent notificationIntent = new Intent(mContext, EventTimelineActivity.class);
                                notificationIntent.putExtra(mContext.getString(R.string.source_key), mContext.getString(R.string.gcm_message_handler_class_name));
                                try {
                                    notificationIntent.putExtra(mContext.getString(R.string.selected_group_id), finalChweetJsonObj1.getString("groupId"));
                                    notificationIntent.putExtra(mContext.getString(R.string.selected_event_id), finalChweetJsonObj1.getString("eventId"));
                                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    // send notification
                                    sendNotification("Dcidr Notification",
                                            jsonMetadataObject.getString("srcUserName") + " has sent you a chweet ", pendingIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }else {
                        // send broadcast message to contactInvitation activity if open
                        Intent localBroadcastIntent = new Intent(mContext.getString(R.string.event_timeline_notification_action_filter));
                        localBroadcastIntent.putExtra("SOURCE", source);
                        localBroadcastIntent.putExtra("TYPE", "CHWEET");
                        localBroadcastIntent.putExtra("ACTION", "REFRESH");
                        localBroadcastIntent.putExtra("MSG", chweetJsonObj.toString());
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                        Intent notificationIntent = new Intent(mContext, EventTimelineActivity.class);
                        notificationIntent.putExtra(mContext.getString(R.string.source_key), mContext.getString(R.string.gcm_message_handler_class_name));
                        try {
                            notificationIntent.putExtra(mContext.getString(R.string.selected_group_id), chweetJsonObj.getString("groupId"));
                            notificationIntent.putExtra(mContext.getString(R.string.selected_event_id), chweetJsonObj.getString("eventId"));
                            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            // send notification
                            sendNotification("Dcidr Notification",
                                    jsonMetadataObject.getString("srcUserName") + " has sent you a chweet ", pendingIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("JSONException", "error in json parsing. " + e.toString());
            }
        }
    }


    public void handleFriendMessage(JSONObject jsonObject, JSONObject jsonMetadataObject, String source) {
        try {
            mNotificationCode = jsonMetadataObject.getInt("notificationCode");
        } catch (JSONException e) {
            Log.e("handleFriendMessage", "Error parsing notification code");
            return;
        }

        if (mNotificationCode == NotificationCodes.FRIEND_INVITE_NTF_CODE) {
            JSONObject friendJsonObj = null;
            try {
                friendJsonObj = jsonObject.getJSONObject("FRIEND");
                // send broadcast message to contactInvitation activity if open
                Intent localBroadcastIntent = new Intent(mContext.getString(R.string.contact_invitation_notification_action_filter));
                localBroadcastIntent.putExtra("SOURCE", source);
                localBroadcastIntent.putExtra("ACTION", "INVITE");
                localBroadcastIntent.putExtra("MSG", friendJsonObj.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                Intent notificationIntent = new Intent(mContext, ContactInvitation.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification",
                        jsonMetadataObject.getString("srcUserName") + " invited you to become a friend ", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "error in json parsing. " + e.toString());
            }
        }

        if (mNotificationCode == NotificationCodes.FRIEND_REMIND_NTF_CODE) {
            try {
                Intent notificationIntent = new Intent(mContext, ContactInvitation.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification",
                        jsonMetadataObject.getString("srcUserName") + " has sent you a friend request reminder", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "error in json parsing. " + e.toString());
            }
        }


        if(mNotificationCode == NotificationCodes.FRIEND_ACCEPT_NTF_CODE){
            JSONObject friendJsonObj = null;
            try {
                friendJsonObj = jsonObject.getJSONObject("FRIEND");
                // update contact status if contact is in global container
                Contact c = DcidrApplication.getInstance().getGlobalContactContainer().getContactMap().get(friendJsonObj.getString("emailId"));
                if(c != null){
                    c.setStatusType(Contact.StatusType.FRIEND);
                    // send local broadcast to main activity for mContactsCustomArrayAdapter refresh
                    Intent mainActivityLocalBroadcastIntent = new Intent("MainActivityGroupNotificationAction");
                    mainActivityLocalBroadcastIntent.putExtra("SOURCE", source);
                    mainActivityLocalBroadcastIntent.putExtra("TARGET", "FRIEND");
                    mainActivityLocalBroadcastIntent.putExtra("ACTION", "REFRESH");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(mainActivityLocalBroadcastIntent);
                }

                // send broadcast message to contactInvitation activity if open
                Intent localBroadcastIntent = new Intent(mContext.getString(R.string.contact_invitation_notification_action_filter));
                localBroadcastIntent.putExtra("SOURCE", "GCM");
                localBroadcastIntent.putExtra("ACTION", "ACCEPT");
                localBroadcastIntent.putExtra("MSG", friendJsonObj.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                Intent notificationIntent = new Intent(mContext, ContactInvitation.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification",
                        jsonMetadataObject.getString("srcUserName") + " has accepted your friend request", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "error in json parsing. " + e.toString());
            }
        }


        if(mNotificationCode == NotificationCodes.FRIEND_DECLINE_NTF_CODE){
            JSONObject friendJsonObj = null;
            try {
                friendJsonObj = jsonObject.getJSONObject("FRIEND");
                // update contact status if contact is in global container
                Contact c = DcidrApplication.getInstance().getGlobalContactContainer().getContactMap().get(friendJsonObj.getString("emailId"));
                if(c != null){
                    c.setStatusType(Contact.StatusType.NOT_FRIEND);
                    // send local broadcast to main activity for mContactsCustomArrayAdapter refresh
                    Intent mainActivityLocalBroadcastIntent = new Intent("MainActivityGroupNotificationAction");
                    mainActivityLocalBroadcastIntent.putExtra("SOURCE", source);
                    mainActivityLocalBroadcastIntent.putExtra("TARGET", "FRIEND");
                    mainActivityLocalBroadcastIntent.putExtra("ACTION", "REFRESH");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(mainActivityLocalBroadcastIntent);
                }

                // send broadcast message to contactInvitation activity if open
                Intent localBroadcastIntent = new Intent(mContext.getString(R.string.contact_invitation_notification_action_filter));
                localBroadcastIntent.putExtra("SOURCE", "GCM");
                localBroadcastIntent.putExtra("ACTION", "DECLINE");
                localBroadcastIntent.putExtra("MSG", friendJsonObj.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(localBroadcastIntent);

                Intent notificationIntent = new Intent(mContext, ContactInvitation.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                // send notification
                sendNotification("Dcidr Notification",
                        jsonMetadataObject.getString("srcUserName") + " has declined your friend request", pendingIntent);
            } catch (JSONException e) {
                Log.e("JSONException", "error in json parsing. " + e.toString());
            }
        }
    }
}
