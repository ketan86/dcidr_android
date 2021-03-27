package com.example.turbo.dcidr.main.user;

import android.content.Context;

import com.example.turbo.dcidr.main.event.BaseEvent;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Turbo on 5/22/2016.
 */
public class UserEventStatus implements Serializable{

    private User mUser;
    private long mGroupId;
    private long mEventId;
    private EventStatusType mEventStatusType;
    private BaseEvent.EventType mEventType;
    private Context mContext;
    public enum EventStatusType {
        PENDING, ACCEPTED, DECLINED
    };
    public UserEventStatus(Context context, long userId, long groupId , long eventId, BaseEvent.EventType eventType){
        this.mContext = context;
        this.mUser = new User(mContext);
        this.mUser.setUserId(userId);
        this.mGroupId = groupId;
        this.mEventId = eventId;
        this.mEventType = eventType;
        // initialize with null status type
        mEventStatusType = EventStatusType.PENDING;
    }
    public long getUserId(){
        return mUser.getUserId();
    }
    public User getUserObj(){
        return this.mUser;
    }
    public long getGroupId(){
        return mGroupId;
    }
    public long getEventId(){
        return mEventId;
    }
    public BaseEvent.EventType getEventType(){
        return mEventType;
    }

    public String getUserIdStr(){
        return mUser.getUserIdStr();
    }
    public String getGroupIdStr(){
        return String.valueOf(mGroupId);
    }
    public String getEventIdStr(){
        return String.valueOf(mEventId);
    }
    public String getEventTypeStr(){
        return mEventType.toString();
    }

    public EventStatusType getEventStatusType(){
        return mEventStatusType;
    }
    public void setEventStatusTypeId(int id){
        if(id == 0){
            this.mEventStatusType = EventStatusType.PENDING;
        }else if(id == 1){
            this.mEventStatusType = EventStatusType.ACCEPTED;
        }else if(id == 2){
            this.mEventStatusType = EventStatusType.DECLINED;
        }else {
            this.mEventStatusType = EventStatusType.PENDING;
        }
    }

    public void setEventStatusType(UserEventStatus.EventStatusType eventStatusType){
        this.mEventStatusType = eventStatusType;
    }

    public HashMap<String, String> getUserEventMapRemote(){
        HashMap<String,String> userEventMap = new HashMap<String,String>();
        userEventMap.put("eventStatusType", mEventStatusType.toString());
        return userEventMap;
    }

}
