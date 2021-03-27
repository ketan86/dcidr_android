package com.example.turbo.dcidr.main.container;

import android.content.Context;

import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.user.UserEventStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Turbo on 5/22/2016.
 */
public class UserEventStatusContainer implements Serializable {
    private ArrayList<UserEventStatus> mUserEventStatusArray;
    private long mGroupId;
    private Context mContext;
    public UserEventStatusContainer(Context context) {
        this.mContext = context;
        this.mUserEventStatusArray = new ArrayList<UserEventStatus>();
    }
    public void setGroupId(Long groupId){
        this.mGroupId = groupId;
    }
    public void populateMe(JSONObject jsonUserEventStatusObject){
        long userId;
        long eventId;
        int eventTypeId;
        try {
            userId = jsonUserEventStatusObject.getLong("userId");
            if(hasUserEventStatusObj(userId)){
                return;
            }
            eventId = jsonUserEventStatusObject.getLong("eventId");
            eventTypeId = jsonUserEventStatusObject.getInt("eventTypeId");
            UserEventStatus userEventStatus = new UserEventStatus(mContext, userId, this.mGroupId, eventId, BaseEvent.EventType.getType(eventTypeId));
            userEventStatus.setEventStatusTypeId(jsonUserEventStatusObject.getInt("eventStatusTypeId"));
            this.mUserEventStatusArray.add(userEventStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void populateMe(JSONArray jsonUserEventStatusArray) {
        for (int i = 0; i < jsonUserEventStatusArray.length(); i++) {
            try {
                this.populateMe(jsonUserEventStatusArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<UserEventStatus.EventStatusType, Integer> getUserToStatusMap(){
        HashMap<UserEventStatus.EventStatusType,Integer> statusMap = new HashMap<UserEventStatus.EventStatusType,Integer>();
        for(UserEventStatus userEventStatus: mUserEventStatusArray){
            if(statusMap.containsKey(userEventStatus.getEventStatusType())) {
                statusMap.put(userEventStatus.getEventStatusType(), statusMap.get(userEventStatus.getEventStatusType()) + 1);
            }else {
                statusMap.put(userEventStatus.getEventStatusType(), 1);
            }
        }

        if(!statusMap.containsKey(UserEventStatus.EventStatusType.ACCEPTED)){
            statusMap.put(UserEventStatus.EventStatusType.ACCEPTED, 0);
        }
        if(!statusMap.containsKey(UserEventStatus.EventStatusType.DECLINED)){
            statusMap.put(UserEventStatus.EventStatusType.DECLINED, 0);
        }

        return statusMap;
    }

    public UserEventStatus getCurrentUserEventStatusObj(){
        for(UserEventStatus userEventStatus : mUserEventStatusArray){
            if (userEventStatus.getUserId() == DcidrApplication.getInstance().getUser().getUserId()){
                return userEventStatus;
            }
        }
        return null;
    }

    public ArrayList<UserEventStatus> getUserEventStatusArray(){
        return mUserEventStatusArray;
    }

    public void clearUserEventStatusArray(){
        this.mUserEventStatusArray.clear();
    }

    public UserEventStatus getUserEventStatusObj(long userId){
        for(UserEventStatus userEventStatus : mUserEventStatusArray){
            if (userEventStatus.getUserId() == userId){
                return userEventStatus;
            }
        }
        return null;
    }

    public boolean hasUserEventStatusObj(long userId){
        for(UserEventStatus userEventStatus : mUserEventStatusArray){
            if (userEventStatus.getUserId() == userId){
                return true;
            }
        }
        return false;
    }


    public void releaseMemory(){
        clearUserEventStatusArray();
    }
}
