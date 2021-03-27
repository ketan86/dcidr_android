package com.example.turbo.dcidr.httpclient;

import android.content.Context;
import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.user.UserEventStatus;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Turbo on 2/18/2016.
 */
public class EventAsyncHttpClient extends BaseAsyncHttpClient{
    public static final String TAG = "EventAsyncHttpClient";

    public EventAsyncHttpClient(Context context){
        super(context);
    }
    public void getEventTypes(String userIdStr, int offset, int limit, AsyncHttpResponseHandler responseHandler) {
        String USER_EVENT_TYPES_GET_URL = DcidrConstant.USER_EVENT_TYPES_GET_URL.replace(":userId", userIdStr);
        USER_EVENT_TYPES_GET_URL = USER_EVENT_TYPES_GET_URL + "?offset=" + offset + "&limit=" + limit;
        this.dcidrGet(USER_EVENT_TYPES_GET_URL, responseHandler);
    }

    public void getEvents(String userIdStr, String groupIdStr,int offset, int limit, AsyncHttpResponseHandler responseHandler) {
        String USER_GROUP_EVENTS_GET_URL = DcidrConstant.USER_GROUP_EVENTS_GET_URL.replace(":userId", userIdStr);
        USER_GROUP_EVENTS_GET_URL = USER_GROUP_EVENTS_GET_URL.replace(":groupId", groupIdStr);
        USER_GROUP_EVENTS_GET_URL = USER_GROUP_EVENTS_GET_URL + "?offset=" + offset + "&limit=" + limit;
        this.dcidrGet(USER_GROUP_EVENTS_GET_URL, responseHandler);
    }

    public void getEvent(String userIdStr, String groupIdStr, String eventIdStr, String eventTypeStr, AsyncHttpResponseHandler responseHandler) {
        if (BuildConfig.DEBUG){ Log.i(TAG, "[getEvent] userId is :" +  userIdStr);}
        String USER_GROUP_EVENT_GET_URL = DcidrConstant.USER_GROUP_EVENT_GET_URL.replace(":userId", userIdStr);
        USER_GROUP_EVENT_GET_URL = USER_GROUP_EVENT_GET_URL.replace(":groupId", groupIdStr);
        USER_GROUP_EVENT_GET_URL = USER_GROUP_EVENT_GET_URL.replace(":eventId", eventIdStr);
        USER_GROUP_EVENT_GET_URL = USER_GROUP_EVENT_GET_URL + "?eventType=" + eventTypeStr;
        this.dcidrGet(USER_GROUP_EVENT_GET_URL, responseHandler);
    }

    public void createEvent(String userIdStr, String groupIdStr, BaseEvent baseEvent, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        String USER_GROUP_EVENTS_POST_URL = DcidrConstant.USER_GROUP_EVENTS_POST_URL.replace(":userId", userIdStr);
        USER_GROUP_EVENTS_POST_URL = USER_GROUP_EVENTS_POST_URL.replace(":groupId", groupIdStr);
        JSONObject eventJsonObject = new JSONObject(baseEvent.getEventDataAsMap());
        StringEntity entity = new StringEntity(eventJsonObject.toString());
        this.dcidrPost(USER_GROUP_EVENTS_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void getUserEventStatus(String userIdStr, String groupIdStr, String eventIdStr, String eventTypeStr, AsyncHttpResponseHandler responseHandler)throws UnsupportedEncodingException {
        String USER_EVENT_STATUS_GET_URL = DcidrConstant.USER_EVENT_STATUS_GET_URL.replace(":userId", userIdStr);
        USER_EVENT_STATUS_GET_URL = USER_EVENT_STATUS_GET_URL.replace(":groupId", groupIdStr);
        USER_EVENT_STATUS_GET_URL = USER_EVENT_STATUS_GET_URL.replace(":eventId", eventIdStr);
        USER_EVENT_STATUS_GET_URL = USER_EVENT_STATUS_GET_URL + "?eventType=" + eventTypeStr;
        this.dcidrGet(USER_EVENT_STATUS_GET_URL, responseHandler);
    }

    public void updateUserEventStatus(String parentEventIdStr, String parentEventTypeStr, UserEventStatus userEventStatus, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException, JSONException {
        String USER_EVENT_STATUS_PUT_URL = DcidrConstant.USER_EVENT_STATUS_PUT_URL.replace(":userId", userEventStatus.getUserIdStr());
        USER_EVENT_STATUS_PUT_URL = USER_EVENT_STATUS_PUT_URL.replace(":groupId", userEventStatus.getGroupIdStr());
        USER_EVENT_STATUS_PUT_URL = USER_EVENT_STATUS_PUT_URL.replace(":eventId", userEventStatus.getEventIdStr());
        USER_EVENT_STATUS_PUT_URL = USER_EVENT_STATUS_PUT_URL + "?eventType=" + userEventStatus.getEventTypeStr();
        USER_EVENT_STATUS_PUT_URL = USER_EVENT_STATUS_PUT_URL + "&parentEventType=" + parentEventTypeStr;
        JSONObject userEventStatusJsonObject = new JSONObject(userEventStatus.getUserEventMapRemote());
        userEventStatusJsonObject.put("parentEventId", parentEventIdStr);
        StringEntity entity = new StringEntity(userEventStatusJsonObject.toString());
        this.dcidrPut(USER_EVENT_STATUS_PUT_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void buzzUser(String userIdStr, String groupIdStr, String eventIdStr, String eventTypeStr, String buzzUserIdStr, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException, JSONException {
        String USER_EVENT_BUZZ_URL = DcidrConstant.USER_EVENT_BUZZ_URL.replace(":userId", userIdStr);
        USER_EVENT_BUZZ_URL = USER_EVENT_BUZZ_URL.replace(":groupId", groupIdStr);
        USER_EVENT_BUZZ_URL = USER_EVENT_BUZZ_URL.replace(":eventId", eventIdStr);
        USER_EVENT_BUZZ_URL = USER_EVENT_BUZZ_URL + "?eventType=" + eventTypeStr;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", buzzUserIdStr);
        StringEntity entity = new StringEntity(jsonObject.toString());
        this.dcidrPost(USER_EVENT_BUZZ_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void submitChweet(String userIdStr, String groupIdStr, String parentEventIdStr, String parentEventTypeStr,
                             String chatMsg, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException, JSONException {
        String USER_EVENT_CHWEET_POST_URL = DcidrConstant.USER_EVENT_CHWEET_POST_URL.replace(":userId", userIdStr);
        USER_EVENT_CHWEET_POST_URL = USER_EVENT_CHWEET_POST_URL.replace(":groupId", groupIdStr);
        USER_EVENT_CHWEET_POST_URL = USER_EVENT_CHWEET_POST_URL.replace(":parentEventId", parentEventIdStr);
        USER_EVENT_CHWEET_POST_URL = USER_EVENT_CHWEET_POST_URL + "?parentEventType=" + parentEventTypeStr;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("chweetText", chatMsg);
        StringEntity entity = new StringEntity(jsonObject.toString());
        this.dcidrPost(USER_EVENT_CHWEET_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void getChweet(String userIdStr, String groupIdStr, String parentEventIdStr, String parentEventTypeStr,
                          int offset, int limit, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException, JSONException {
        if (BuildConfig.DEBUG){ Log.i(TAG, "[getChweet] userId is :" +  userIdStr);}
        String USER_EVENT_CHWEET_GET_URL = DcidrConstant.USER_EVENT_CHWEET_GET_URL.replace(":userId", userIdStr);
        USER_EVENT_CHWEET_GET_URL = USER_EVENT_CHWEET_GET_URL.replace(":groupId", groupIdStr);
        USER_EVENT_CHWEET_GET_URL = USER_EVENT_CHWEET_GET_URL.replace(":parentEventId", parentEventIdStr);
        USER_EVENT_CHWEET_GET_URL = USER_EVENT_CHWEET_GET_URL + "?parentEventType=" + parentEventTypeStr;
        USER_EVENT_CHWEET_GET_URL = USER_EVENT_CHWEET_GET_URL + "&offset=" + offset;
        USER_EVENT_CHWEET_GET_URL = USER_EVENT_CHWEET_GET_URL + "&limit=" + limit;
        this.dcidrGet(USER_EVENT_CHWEET_GET_URL, responseHandler);
    }

}
