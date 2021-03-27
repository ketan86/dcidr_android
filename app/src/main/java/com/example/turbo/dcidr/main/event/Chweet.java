package com.example.turbo.dcidr.main.event;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by borat on 10/2/2016.
 */
public class Chweet implements Comparable{
    private long mChweetId;
    private long mUserId;
    private String mChweetText;
    private long mChweetTime;
    private String mUserFirstName;
    private String mUserLastName;
    private long mParentEventId;
    private long mParentEventTypeId;
    private int mChweetColor;
    public static int UNKNOWN = -1;
    public Chweet(){
        mChweetColor = UNKNOWN;
    }
    public long getChweetId() {
        return mChweetId;
    }
    public int getChweetColor() {
        return mChweetColor;
    }
    public long getUserId() {
        return mUserId;
    }
    public String getChweetText() {
        return mChweetText;
    }
    public long getChweetTime() {
        return  mChweetTime;
    }
    public String getChweetUserFirstName() {
        return  mUserFirstName;
    }
    public String getChweetUserLastName() {
        return  mUserLastName;
    }
    public long getChweetParentEventId() {
        return  mParentEventId;
    }
    public long getChweetParentEventTypeId() {
        return  mParentEventTypeId;
    }

    public void setChweetId(long chweetId) {
        this.mChweetId = chweetId;
    }
    public void setChweetColor(int chweetColor) {
        this.mChweetColor = chweetColor;
    }
    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public void setChweetTime(long chweetTime) {
        this.mChweetTime = chweetTime;
    }
    public void setChweetText(String chweetText) {
        this.mChweetText = chweetText;
    }
    public void setChweetUserFirstName(String userFirstName) {
        this.mUserFirstName = userFirstName;
    }
    public void setChweetUserLastName(String userLastName) {
        this.mUserLastName = userLastName;
    }
    public void setChweetParentEventId(long parentEventId) {
        this.mParentEventId = parentEventId;
    }
    public void setChweetParentEventTypeId(long parentEventTypeId) {
        this.mParentEventTypeId = parentEventTypeId;
    }

    @Override
    public int compareTo(Object another) {
        Chweet anotherChweet = (Chweet) another;
        if (this.mChweetTime > anotherChweet.mChweetTime) {
            return -1;
        } else if (this.mChweetTime == anotherChweet.mChweetTime ) {
            return 0;
        }
        return 1;
    }

    public void populateMe(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("chweetId")) {
            this.setChweetId(jsonObject.getLong("chweetId"));
        }
        if (jsonObject.has("userId")) {
            this.setUserId(jsonObject.getLong("userId"));
        }
        if (jsonObject.has("chweetText")) {
            this.setChweetText(jsonObject.getString("chweetText"));
        }
        if (jsonObject.has("chweetTime")) {
            this.setChweetTime(jsonObject.getLong("chweetTime"));
        }
        if (jsonObject.has("parentEventId")) {
            this.setChweetParentEventId(jsonObject.getLong("parentEventId"));
        }
        if (jsonObject.has("parentEventTypeId")) {
            this.setChweetParentEventId(jsonObject.getLong("parentEventTypeId"));
        }
        if (jsonObject.has("firstName")) {
            this.setChweetUserFirstName(jsonObject.getString("firstName"));
        }
        if (jsonObject.has("lastName")) {
            this.setChweetUserLastName(jsonObject.getString("lastName"));
        }
    }
    public void releaseMemory() {
    }
}
