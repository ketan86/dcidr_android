package com.example.turbo.dcidr.httpclient;

import android.content.Context;
import android.util.Log;

import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.user.User;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Turbo on 2/7/2016.
 */
public class UserAsyncHttpClient extends BaseAsyncHttpClient {
    public UserAsyncHttpClient(Context context){
        super(context);
    }

    public void createUser(User user, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        //if (mContentType == ContentType.JSON) {
        JSONObject userJsonObject = new JSONObject(user.getUserMap());
        StringEntity entity = new StringEntity(userJsonObject.toString());
        this.dcidrPost(DcidrConstant.USERS_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
        //}
    }

//    public void setGcmRegToken(String userId, String gcmRegToken, AsyncHttpResponseHandler responseHandler)  throws UnsupportedEncodingException{
//        String USER_DEVICES_PUT_URL = DcidrResource.USER_DEVICES_PUT_URL.replace(":userId", userId);
//        HashMap<String,String> gcmHashMap = new HashMap<>();
//        gcmHashMap.put("gcmRegToken", gcmRegToken);
//        Log.i("Ketan", gcmRegToken);
//        JSONObject loginjsonObject = new JSONObject(gcmHashMap);
//        StringEntity entity = new StringEntity(loginjsonObject.toString());
//        this.dcidrPut(USER_DEVICES_PUT_URL, entity, getContentTypeAsString(mContentType), responseHandler);
//    }

    public void createUserDevice(String userId, HashMap<String, String> attHashMap, AsyncHttpResponseHandler responseHandler)  throws UnsupportedEncodingException{
        String USER_DEVICES_POST_URL = DcidrConstant.USER_DEVICES_POST_URL.replace(":userId", userId);
        JSONObject loginjsonObject = new JSONObject(attHashMap);
        StringEntity entity = new StringEntity(loginjsonObject.toString());
        this.dcidrPost(USER_DEVICES_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void updateUserDevice(String userId, HashMap<String, String> attHashMap, AsyncHttpResponseHandler responseHandler)  throws UnsupportedEncodingException{
        String USER_DEVICES_PUT_URL = DcidrConstant.USER_DEVICES_PUT_URL.replace(":userId", userId);
        JSONObject loginJsonObject = new JSONObject(attHashMap);
        StringEntity entity = new StringEntity(loginJsonObject.toString());
        this.dcidrPut(USER_DEVICES_PUT_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void getUserId(String queryParamKey, String queryParamValue, AsyncHttpResponseHandler responseHandler){
        Log.i("Ketan:query", queryParamKey);
        this.dcidrGet(RELATIVE_USER_URL + "?" + queryParamKey + "=" + queryParamValue, responseHandler);
    }

    public void getUser(String userId, AsyncHttpResponseHandler responseHandler) {
        String USER_GET_URL = DcidrConstant.USER_GET_URL.replace(":userId", userId);
        this.dcidrGet(USER_GET_URL, responseHandler);
    }

    public void loginUser(String email, String passwordDigest, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException{
        if (mContentType == ContentType.JSON) {
            HashMap<String,String> loginHashMap = new HashMap<>();
            loginHashMap.put("emailId", email);
            loginHashMap.put("passwordDigest", passwordDigest);
            JSONObject loginJsonObject = new JSONObject(loginHashMap);
            StringEntity entity = new StringEntity(loginJsonObject.toString());
            this.dcidrPost(DcidrConstant.USERS_LOGIN_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
        }
    }

    public void getFriends(String userId, String status, String inviteBy, int offset, int limit, AsyncHttpResponseHandler responseHandler){
        String USER_FRIENDS_GET_URL = DcidrConstant.USER_FRIENDS_GET_URL.replace(":userId", userId);
        if(inviteBy.equals("true")) {
            USER_FRIENDS_GET_URL = USER_FRIENDS_GET_URL + "?direct=true" + "&offset=" + offset + "&limit=" + limit + "&status=" + status + "&invitedBy=" + inviteBy;
        }else {
            USER_FRIENDS_GET_URL = USER_FRIENDS_GET_URL + "?direct=true" + "&offset=" + offset + "&limit=" + limit + "&status=" + status;
        }
        this.dcidrGet(USER_FRIENDS_GET_URL, responseHandler);
    }

    public void getActiveFriendsByQueryText(String userId, String queryText, int offset, int limit, AsyncHttpResponseHandler responseHandler){
        String USER_FRIENDS_GET_URL = DcidrConstant.USER_FRIENDS_GET_URL.replace(":userId", userId);
        USER_FRIENDS_GET_URL = USER_FRIENDS_GET_URL + "?direct=true" + "&offset=" + offset + "&limit=" + limit + "&has=" + queryText + "&status=FRIEND";
        this.dcidrGet(USER_FRIENDS_GET_URL, responseHandler);
    }

    public void setFriend(String userId, String email, AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException{
        String USER_FRIENDS_POST_URL = DcidrConstant.USER_FRIENDS_POST_URL.replace(":userId", userId);
        HashMap<String,String> addFriendHashMap = new HashMap<>();
        addFriendHashMap.put("friendEmailId", email);
        JSONObject addFriendJsonObject = new JSONObject(addFriendHashMap);
        StringEntity entity = new StringEntity(addFriendJsonObject.toString());
        this.dcidrPost(USER_FRIENDS_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void getDirectAndIndirectContacts(String userId, int offset, int limit, AsyncHttpResponseHandler responseHandler){
        String USER_FRIENDS_GET_URL = DcidrConstant.USER_FRIENDS_GET_URL.replace(":userId", userId);
        USER_FRIENDS_GET_URL = USER_FRIENDS_GET_URL + "?directAndIndirect=true" + "&offset=" + offset + "&limit=" + limit;
        this.dcidrGet(USER_FRIENDS_GET_URL, responseHandler);
    }

    public void inviteContact(String userIdStr, String friendEmailId ,AsyncHttpResponseHandler responseHandler) {
        String USER_FRIEND_POST_URL = DcidrConstant.USER_FRIEND_POST_URL.replace(":userId", userIdStr);
        USER_FRIEND_POST_URL = USER_FRIEND_POST_URL.replace(":emailId", friendEmailId);
        USER_FRIEND_POST_URL = USER_FRIEND_POST_URL + "?action=invite";
        this.dcidrPost(USER_FRIEND_POST_URL, responseHandler);
    }

    public void acceptInvitation(String userIdStr, String userEmailId,  String friendIdStr, String friendEmailId ,AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        String USER_FRIEND_POST_URL = DcidrConstant.USER_FRIEND_POST_URL.replace(":userId", userIdStr);
        USER_FRIEND_POST_URL = USER_FRIEND_POST_URL.replace(":emailId", friendEmailId);
        USER_FRIEND_POST_URL = USER_FRIEND_POST_URL + "?action=accept";
        HashMap<String,String> addFriendHashMap = new HashMap<>();
        addFriendHashMap.put("friendId", friendIdStr);
        addFriendHashMap.put("userEmailId", userEmailId);
        JSONObject jsonObject = new JSONObject(addFriendHashMap);
        StringEntity entity = new StringEntity(jsonObject.toString());
        this.dcidrPost(USER_FRIEND_POST_URL,  entity, getContentTypeAsString(mContentType),responseHandler);
    }

    public void declineInvitation(String userIdStr, String userEmailId,  String friendIdStr, String friendEmailId,AsyncHttpResponseHandler responseHandler) throws UnsupportedEncodingException {
        String USER_FRIEND_POST_URL = DcidrConstant.USER_FRIEND_POST_URL.replace(":userId", userIdStr);
        USER_FRIEND_POST_URL = USER_FRIEND_POST_URL.replace(":emailId", friendEmailId);
        USER_FRIEND_POST_URL = USER_FRIEND_POST_URL + "?action=decline";
        HashMap<String,String> addFriendHashMap = new HashMap<>();
        addFriendHashMap.put("friendId", friendIdStr);
        addFriendHashMap.put("userEmailId", userEmailId);
        JSONObject jsonObject = new JSONObject(addFriendHashMap);
        StringEntity entity = new StringEntity(jsonObject.toString());
        this.dcidrPost(USER_FRIEND_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void setUserMedia(String userIdStr, String base64Str, String mediaType, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException{
        String USER_MEDIA_IMAGE_POST_URL = null;
        if(mediaType.equals("image")){
            USER_MEDIA_IMAGE_POST_URL = DcidrConstant.USER_MEDIA_IMAGE_POST_URL.replace(":userId", userIdStr);
        } // TODO for other media types
        JSONObject mediaJson = new JSONObject();
        mediaJson.put("imageBase64Str", base64Str);
        StringEntity entity = new StringEntity(mediaJson.toString());
        this.dcidrPost(USER_MEDIA_IMAGE_POST_URL, entity, getContentTypeAsString(mContentType), responseHandler);
    }

    public void getUserMediaByUrl(String userIdStr, String url, AsyncHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException{
        String USER_MEDIA_GET_BY_URL = DcidrConstant.USER_MEDIA_GET_BY_URL.replace(":userId", userIdStr);
        USER_MEDIA_GET_BY_URL = USER_MEDIA_GET_BY_URL + "?url=" + url;
        this.dcidrGet(USER_MEDIA_GET_BY_URL, responseHandler);
    }
}

