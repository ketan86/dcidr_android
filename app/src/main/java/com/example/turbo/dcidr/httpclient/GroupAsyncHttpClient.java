package com.example.turbo.dcidr.httpclient;

import android.content.Context;
import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.container.GroupContainer;
import com.example.turbo.dcidr.main.container.UserContainer;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Turbo on 2/25/2016.
 */
public class GroupAsyncHttpClient extends BaseAsyncHttpClient{
    public static final String TAG = "GroupAsyncHttpClient";

    public GroupAsyncHttpClient(Context context){
        super(context);
    }

    public void getGroups(String userIdStr, int offset, int limit, AsyncHttpResponseHandler responseHandler) {
        String USER_GROUPS_GET_URL = DcidrConstant.USER_GROUPS_GET_URL.replace(":userId", userIdStr);
        USER_GROUPS_GET_URL = USER_GROUPS_GET_URL + "?offset=" + offset + "&limit=" + limit;
        this.dcidrGet(USER_GROUPS_GET_URL, responseHandler);
    }
    public void getGroup(String userIdStr, String groupIdStr, AsyncHttpResponseHandler responseHandler) {
        if (BuildConfig.DEBUG){ Log.i(TAG, "[getGroup] userId is :" +  userIdStr);}
        String USER_GROUP_GET_URL = DcidrConstant.USER_GROUP_GET_URL.replace(":userId", userIdStr);
        USER_GROUP_GET_URL = USER_GROUP_GET_URL.replace(":groupId", groupIdStr);
        this.dcidrGet(USER_GROUP_GET_URL, responseHandler);
    }

    public void getGroupsByQueryText(String userIdStr, String queryText, int offset, int limit, AsyncHttpResponseHandler responseHandler) {
        String USER_GROUPS_GET_URL = DcidrConstant.USER_GROUPS_GET_URL.replace(":userId", userIdStr);
        USER_GROUPS_GET_URL = USER_GROUPS_GET_URL + "?offset=" + offset + "&limit=" + limit + "&has=" + queryText ;
        this.dcidrGet(USER_GROUPS_GET_URL, responseHandler);
    }

    public void getGroupMembers(String userIdStr, String groupIdStr, AsyncHttpResponseHandler responseHandler) {
        String USER_GROUP_GET_MEMBERS_URL = DcidrConstant.USER_GROUP_GET_MEMBERS_URL.replace(":userId", userIdStr);
        USER_GROUP_GET_MEMBERS_URL = USER_GROUP_GET_MEMBERS_URL.replace(":groupId", groupIdStr);
        //TODO: Kanishka
        // we need to convert this API into an offset + limit based API just like other APIs. Currently keeping it without offset
        this.dcidrGet(USER_GROUP_GET_MEMBERS_URL, responseHandler);
    }
    // fetach unread events
    public void getGroupUnreadEvents(String userIdStr, String groupIdStr, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        String USER_GROUP_UNREAD_EVENTS_GET_URL = DcidrConstant.USER_GROUP_UNREAD_EVENTS_GET_URL.replace(":userId", userIdStr);
        USER_GROUP_UNREAD_EVENTS_GET_URL = USER_GROUP_UNREAD_EVENTS_GET_URL.replace(":groupId", groupIdStr);
        this.dcidrGet(USER_GROUP_UNREAD_EVENTS_GET_URL, responseHandler);
    }

    // we want user unread events are shown until user sees it.
    public void setGroupUnreadEvents(String userIdStr, String groupIdStr, int unreadEventsCount, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        String USER_GROUP_UNREAD_EVENTS_POST_URL = DcidrConstant.USER_GROUP_UNREAD_EVENTS_POST_URL.replace(":userId", userIdStr);
        USER_GROUP_UNREAD_EVENTS_POST_URL = USER_GROUP_UNREAD_EVENTS_POST_URL.replace(":groupId", groupIdStr);
        JSONObject unreadEvents = new JSONObject();
        unreadEvents.put("unreadEvents", unreadEventsCount);
        StringEntity entity = new StringEntity(unreadEvents.toString());
        this.dcidrPut(USER_GROUP_UNREAD_EVENTS_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void createGroup(String userIdStr, BaseGroup baseGroup, UserContainer userContainer,
                            GroupContainer groupContainer, AsyncHttpResponseHandler responseHandler)
            throws UnsupportedEncodingException, JSONException {
        String USER_GROUPS_POST_URL = DcidrConstant.USER_GROUPS_POST_URL.replace(":userId", userIdStr);
        JSONObject groupJsonObject = new JSONObject(baseGroup.getGroupMapForRemote());
        // add logged in user(or caller) into group being created
        Set<Long> userIds =  userContainer.getUserMap().keySet();
        ArrayList<String> userIdsAsStr = new ArrayList<>();
        for(Long l: userIds){
            userIdsAsStr.add(String.valueOf(l));
        }
        JSONArray userIdsJsonArray = new JSONArray(userIdsAsStr);
        userIdsJsonArray.put(userIdStr);
        groupJsonObject.put("userIds", userIdsJsonArray);

        Set<Long> groupIds =  groupContainer.getGroupMap().keySet();
        ArrayList<String> groupIdsAsStr = new ArrayList<>();
        for(Long l: groupIds){
            groupIdsAsStr.add(String.valueOf(l));
        }
        groupJsonObject.put("groupIds", new JSONArray(groupIdsAsStr));
        StringEntity entity = new StringEntity(groupJsonObject.toString());
        this.dcidrPost(USER_GROUPS_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }


    public void setGroupMedia(String userIdStr, String groupIdStr, String base64Str, String mediaType, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException{
        String GROUPS_MEDIA_IMAGE_POST_URL = null;
        if(mediaType.equals("image")){
            GROUPS_MEDIA_IMAGE_POST_URL = DcidrConstant.GROUPS_MEDIA_IMAGE_POST_URL.replace(":userId", userIdStr);
            GROUPS_MEDIA_IMAGE_POST_URL = GROUPS_MEDIA_IMAGE_POST_URL.replace(":groupId", groupIdStr);
        } // TODO for other media types

        JSONObject mediaJson = new JSONObject();
        mediaJson.put("imageBase64Str", base64Str);
        StringEntity entity = new StringEntity(mediaJson.toString());
        this.dcidrPost(GROUPS_MEDIA_IMAGE_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void getGroupMedia(String userIdStr, String groupIdStr, String mediaType, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException{
        String GROUPS_MEDIA_IMAGE_GET_URL = null;
        if(mediaType.equals("image")){
            GROUPS_MEDIA_IMAGE_GET_URL = DcidrConstant.GROUPS_MEDIA_IMAGE_GET_URL.replace(":userId", userIdStr);
            GROUPS_MEDIA_IMAGE_GET_URL = GROUPS_MEDIA_IMAGE_GET_URL.replace(":groupId", groupIdStr);
        } // TODO for other media types
        this.dcidrGet(GROUPS_MEDIA_IMAGE_GET_URL, responseHandler);
    }

    public void getGroupMediaByUrl(String userIdStr, String url, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException{
        String GROUPS_MEDIA_GET_BY_URL = DcidrConstant.GROUPS_MEDIA_GET_BY_URL.replace(":userId", userIdStr);
        GROUPS_MEDIA_GET_BY_URL = GROUPS_MEDIA_GET_BY_URL + "?url=" + url;
        this.dcidrGet(GROUPS_MEDIA_GET_BY_URL, responseHandler);
    }
}
