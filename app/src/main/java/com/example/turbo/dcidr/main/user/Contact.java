/**
 * Created by Turbo on 2/7/2016.
 */

package com.example.turbo.dcidr.main.user;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact extends User {

    public enum ContactType {
        FACEBOOK(1),
        GOOGLE(2),
        DCIDR(3),
        PHONE(4);

        private int value;

        private ContactType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    private StatusType mStatusType;
    private ContactType mContactType;
    private Context mContext;
    public Contact(Context context, ContactType contactType) {
        super(context);
        this.mContext = context;
        mContactType = contactType;
    }

    public enum StatusType {
        FRIEND(1), //Contact is a friend
        NOT_FRIEND(2), //Contact is not a friend
        INVITED(3);//Contact has been sent an Invite to become a friend

        private int value;

        private StatusType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    public StatusType getStatusType() {
        return mStatusType;
    }
    public void setStatusType(StatusType statusType) {
//        if (statusTypeStr.equals("NOT_FRIEND")) {
//            this.mStatusType = StatusType.NOT_FRIEND;
//        } else if (statusTypeStr.equals("FRIEND")){
//            this.mStatusType = StatusType.FRIEND;
//        } else if (statusTypeStr.equals("INVITED")) {
//            this.mStatusType = StatusType.INVITED;
//        }
        this.mStatusType = statusType;
    }
    public void setStatusType(int statusTypeInt) {
        if (statusTypeInt == 1) {
            this.mStatusType = StatusType.FRIEND;
        } else if (statusTypeInt == 2){
            this.mStatusType = StatusType.NOT_FRIEND;
        } else if (statusTypeInt == 3) {
            this.mStatusType = StatusType.INVITED;
        }
    }

    public ContactType getContactType(){
        return this.mContactType;
    }

    public void setContactType(ContactType contactType){
        this.mContactType = contactType;
    }


    public void populateMe(JSONObject jsonObject) throws JSONException {
        super.populateMe(jsonObject);
        if (jsonObject.has("statusTypeId")) {
            this.setStatusType(jsonObject.getInt("statusTypeId"));
        }
    }
}