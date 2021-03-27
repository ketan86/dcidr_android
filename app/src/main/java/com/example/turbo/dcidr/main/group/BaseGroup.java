package com.example.turbo.dcidr.main.group;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;

import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.container.EventContainer;
import com.example.turbo.dcidr.main.container.UserContainer;
import com.example.turbo.dcidr.utils.common_utils.SmartSearch;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/25/2016.
 */
public class BaseGroup implements Serializable, Comparable, SmartSearch.Searchable {

    public enum GroupSortKey {
        GROUP_LAST_MODIFIED_TIME,  GROUP_NAME
    };
    public long UNDEFINED = -1;
    private GroupSortKey mGroupSortKey;
    private String mGroupName;
    private String[] mMembers;
    private Long mMemberCount;
    private EventContainer mEventContainer;
    private UserContainer mUserContainer;
    private long mGroupId;
    private int mUnreadEventCount;
    private int mTotalEventCount;
    private long mGroupLastModifiedTime;
    private long mGroupCreationTime;
    private boolean mIsSelected;
    private String mGroupProfilePicBase64Str;
    private String mGroupProfilePicUrl;
    private Bitmap mGroupProfilePicBitmap;
    private Context mContext;

    private GroupProfPicFetchInterface mGroupProfPicFetchInterface;

    public BaseGroup(Context context){
        mContext  = context;
        mGroupId = UNDEFINED;
        mUnreadEventCount = 0;
        mEventContainer = new EventContainer(context);
        mUserContainer = new UserContainer(context);
        mGroupProfilePicBitmap = null;
    }

    public interface GroupProfPicFetchInterface {
        void onImageFetchDone(Bitmap bitmap);
        int getImageWidth();
        int getImageHeight();
    }

    public void onGroupProfPicFetchDoneListener(GroupProfPicFetchInterface groupProfPicFetchInterface){
        this.mGroupProfPicFetchInterface = groupProfPicFetchInterface;
    }

    public void setGroupProfilePicBase64Str(String base64Str){
        this.mGroupProfilePicBase64Str = base64Str;
    }

    public void setTotalEventCount(int count){
        this.mTotalEventCount = count;
    }

    public String getTotalEventCountStr(){
        return String.valueOf(this.mTotalEventCount);
    }
    public int getTotalEventCount(){
        return this.mTotalEventCount;
    }
    public void incrementTotalEventCount(){
        this.mTotalEventCount += 1;
    }
    public String getGroupProfilePicBase64Str(){
        return this.mGroupProfilePicBase64Str;
    }

    public Bitmap getGroupProfilePicBitmap(){
        return this.mGroupProfilePicBitmap;
    }

    public void setGroupProfilePicBitmap(Bitmap bitmap){
        this.mGroupProfilePicBitmap = bitmap;
    }
    public void setGroupProfilePicUrl(String profilePicUrl){
        this.mGroupProfilePicUrl = profilePicUrl;
    }
    public String getGroupProfilePicUrl(){
        return this.mGroupProfilePicUrl;
    }
    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }
    public void setEventContainerBaseGroup() {
        mEventContainer.setBaseGroup(this);
    }
    public void setMemberCount(Long memberCount) {this.mMemberCount = memberCount;}
    public void setGroupName(String name){
        this.mGroupName = name;
    }
    public void setGroupCreationTime(long creationTime){
        this.mGroupCreationTime = creationTime;
    }
    public void setUnreadEventCount(int count){
        this.mUnreadEventCount = count;
    }
    public void incrementUnreadEventCount(){
        this.mUnreadEventCount++;
    }
    public void setGroupId(long id){
        this.mGroupId = id;
    }
    public void setGroupLastModifiedTime(long groupLastModifiedTime){
        this.mGroupLastModifiedTime = groupLastModifiedTime;
    }

    public void setMemberNames(String[] members){
        this.mMembers = members;
    }
    public void setGroupSortKey(GroupSortKey key) {
        mGroupSortKey = key;
    }
    public EventContainer getEventContainer(){
        return this.mEventContainer;
    }
    public void setEventContainer(EventContainer eventContainer){
        this.mEventContainer = eventContainer;
    }
    public UserContainer getUserContainer(){
        return this.mUserContainer;
    }
    public String getGroupName(){
        return this.mGroupName;
    }
    public long getGroupCreationTime(){
        return this.mGroupCreationTime;
    }
    public boolean getIsSelected(){
        return this.mIsSelected;
    }
    public int getUnreadEventCount(){
        return this.mUnreadEventCount;
    }
    public String getUnreadEventCountStr(){
        return String.valueOf(this.mUnreadEventCount);
    }

    public long getGroupId(){
        return this.mGroupId;
    }
    public String getGroupIdStr(){
        return String.valueOf(this.mGroupId);
    }
    public long getGroupLastModifiedTime(){
        return this.mGroupLastModifiedTime;
    }
    public String[] getMembers(){return this.mMembers;}
    public long getMemberCount() { return this.mMemberCount;}

    public void populateEvent(JSONArray jsonGroupEventArray) throws IllegalAccessException,
            InstantiationException, JSONException,
            NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        mEventContainer.populateEvent(jsonGroupEventArray);
    }

    public void populateEvent(JSONObject jsonObject) throws IllegalAccessException,
            InstantiationException, JSONException,
            NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        mEventContainer.populateEvent(jsonObject);
    }

    public HashMap<String,String> getGroupMapForRemote(){
        HashMap<String,String> groupMap = new HashMap<String,String>();
        groupMap.put("groupName", this.getGroupName());
        groupMap.put("groupLastModifiedTime", String.valueOf(this.getGroupLastModifiedTime()));
        if(this.getGroupProfilePicBase64Str() != null) {
            groupMap.put("groupProfilePicBase64Str", String.valueOf(this.getGroupProfilePicBase64Str()));
        }
        return groupMap;
    }
    public HashMap<String,String> getGroupMapForLocal(){
        HashMap<String,String> groupMap = new HashMap<String,String>();
        groupMap.put("groupId", String.valueOf(this.getGroupId()));
        groupMap.put("groupName", this.getGroupName());
        groupMap.put("groupLastModifiedTime", String.valueOf(this.getGroupLastModifiedTime()));
        groupMap.put("groupProfilePicBase64Str", String.valueOf(this.getGroupProfilePicBase64Str()));
        return groupMap;
    }

    public void populateMe(JSONObject jsonObject) throws JSONException {

        if(jsonObject.has("groupId")) {
            this.setGroupId(jsonObject.getLong("groupId"));
        }
        if(jsonObject.has("unreadEvents")) {
            this.setUnreadEventCount(jsonObject.getInt("unreadEvents"));
        }
        if(jsonObject.has("totalEventCount")) {
            this.setTotalEventCount(jsonObject.getInt("totalEventCount"));
        }
        if(jsonObject.has("groupName")) {
            this.setGroupName(jsonObject.getString("groupName"));
        }
        if(jsonObject.has("members")) {
            this.setMemberNames(Utils.getStringArray(jsonObject.getJSONArray("members")));
        }
        if(jsonObject.has("groupLastModifiedTime")) {
            this.setGroupLastModifiedTime(jsonObject.getLong("groupLastModifiedTime"));
        }
        if(jsonObject.has("groupCreationTime")) {
            this.setGroupCreationTime(jsonObject.getLong("groupCreationTime"));
        }
        if (jsonObject.has("memberCount")) {
            this.setMemberCount(jsonObject.getLong("memberCount"));
        }
//        if (jsonObject.has("groupProfilePicBase64Str")){
//            String base64Str = jsonObject.getString("groupProfilePicBase64Str");
//            this.setGroupProfilePicBase64Str(base64Str);
//            Bitmap imageBitMap = Utils.decodeBase64(base64Str);
//            mGroupProfilePicBitmap = imageBitMap;
//        }
        if (jsonObject.has("groupProfilePicUrl")) {
            if(!jsonObject.isNull("groupProfilePicUrl")) {
                this.setGroupProfilePicUrl(jsonObject.getString("groupProfilePicUrl"));
            }
            //launch a async task to fetch the group image from the server and store it into the BaseGroup object
            if(this.getGroupProfilePicBase64Str() == null && this.getGroupProfilePicUrl() != null) {
                new GroupProfilePicAsyncLoad().execute("Fetching group profile pic");
            } // else {
//                if(mGroupProfilePicBitmap == null) {
//                    if(this.getGroupProfilePicBase64Str() != null) {
//                        Bitmap imageBitMap = Utils.decodeBase64(this.getGroupProfilePicBase64Str());
//                        mGroupProfilePicBitmap = imageBitMap;
//                    }
//                }

//            }
        }
    }

    private class GroupProfilePicAsyncLoad extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                GroupAsyncHttpClient groupAsyncHttpClient = new GroupAsyncHttpClient(mContext);
                String mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
                groupAsyncHttpClient.getGroupMediaByUrl(mUserIdStr, getGroupProfilePicUrl(), new AsyncHttpResponseHandler(Looper.getMainLooper()) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String result = new JSONObject(new String(responseBody)).getString("result");
                                setGroupProfilePicBase64Str(result);
                                if(mGroupProfPicFetchInterface != null) {
                                    Bitmap imageBitMap = Utils.decodeBase64(result,
                                            mGroupProfPicFetchInterface.getImageWidth(), mGroupProfPicFetchInterface.getImageHeight());
                                    setGroupProfilePicBitmap(imageBitMap);
                                    mGroupProfPicFetchInterface.onImageFetchDone(imageBitMap);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                        JSONObject jsonObject = null;
                        String errorString = null;
                        try {
                            jsonObject = new JSONObject(new String(errorResponse));
                            errorString = (String) jsonObject.get("error");
                        } catch (JSONException error) {
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "Executed";
        }
    }

    @Override
    public String getSearchString() {
        return this.mGroupName;
    }

    @Override
    public int compareTo(Object another) {
        BaseGroup anotherBaseGroup = (BaseGroup) another;
        if (mGroupSortKey == GroupSortKey.GROUP_NAME) {
            //TODO  implement if required. Setting up template for sorting based on different keys
            return -1;
        }
        else{
            return this.getGroupLastModifiedTime() > anotherBaseGroup.getGroupLastModifiedTime() ? -1 :
                    this.getGroupLastModifiedTime() < anotherBaseGroup.getGroupLastModifiedTime() ? 1 : 0;
        }
    }
}
