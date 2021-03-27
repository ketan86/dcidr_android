package com.example.turbo.dcidr.main.container;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.turbo.dcidr.main.user.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Turbo on 4/9/2016.
 */
public class ContactContainer implements Serializable {
    private ArrayList<Contact> mContactList;
    private HashMap<String,Contact> mContactMap;
    private Contact.UserSortKey mContactSortKey;
    private Context mContext;
    public ContactContainer(Context context) {
        this.mContext = context;
        this.mContactList = new ArrayList<Contact>();
        this.mContactMap = new HashMap<String,Contact>();
    }
    public void populateMe(JSONArray jsonContactArray, Contact.ContactType contactType) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        for (int i = 0; i < jsonContactArray.length(); i++) {
            JSONObject jsonContactObj =  jsonContactArray.getJSONObject(i);
            populateMe(jsonContactObj, contactType);
        }
    }


    public void populateMe(JSONObject jsonContactObject, Contact.ContactType contactType) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        Log.i("Ketan:CG3", jsonContactObject.getString("userId"));
        if (jsonContactObject.getString("emailId") != null && mContactMap.containsKey(jsonContactObject.getString("emailId"))) {
            Contact c =  mContactMap.get(jsonContactObject.getString("emailId"));
            c.populateMe(jsonContactObject);
            if (c.getStatusType() == Contact.StatusType.FRIEND ) {
                c.setContactType(Contact.ContactType.DCIDR);
            }
        } else {
            Contact contact = new Contact(mContext, contactType);
            contact.populateMe(jsonContactObject);
            //
            mContactMap.put(contact.getEmailId(), contact);
        }
    }

    public void populateMePhone(String emailId, String firstName, String lastName, Contact.StatusType contactStatusType, Contact.ContactType contactType) {
        //check if emailId already exists in the ContactsMap
        Contact c = mContactMap.get(emailId);
        boolean newEntry = false;
        if (c == null) {
            c = new Contact(mContext, contactType);
            newEntry = true;
        }
        c.setEmailId(emailId);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        //check if the new status being set on an existing entry is "Invited", in which case we overwrite
        Log.i("Kanishka", "emailId = " + emailId + ", Status = " + contactStatusType.toString());
        if (!newEntry && c.getStatusType() == Contact.StatusType.NOT_FRIEND  ) {
            c.setStatusType(contactStatusType);
        }
        //set the contacttype to contactType if the existing contact is not a friend or is invited.
        if (!newEntry && (c.getStatusType() == Contact.StatusType.NOT_FRIEND  || c.getStatusType() == Contact.StatusType.INVITED)) {
            c.setContactType(contactType);
        }
        if (newEntry) {
            c.setStatusType(contactStatusType);
            mContactMap.put(emailId, c);
        }
    }

    public void populateMe(String emailId, String firstName, String lastName,Contact.StatusType statusType,
                           Bitmap imageBitMap, Contact.ContactType contactType) {
        //check if emailId already exists in the ContactsMap
        Contact c = mContactMap.get(emailId);
        boolean newEntry = false;
        if (c == null) {
            c = new Contact(mContext, contactType);
            newEntry = true;
        }
        c.setEmailId(emailId);
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setStatusType(statusType);
        c.setUserProfilePicBitmap(imageBitMap);
        if (newEntry) {
            mContactMap.put(emailId, c);
        }
    }



    public void refreshContactList(){
        // TODO need to return list sorted by last modified time
        mContactList.clear();
        ArrayList<Contact> arrayList = new ArrayList<Contact>(mContactMap.values());
        Collections.sort(arrayList);
        mContactList.addAll(arrayList);
    }

    public void setContactSortKey(Contact.UserSortKey key) {mContactSortKey = key;}



    public HashMap<String, Contact> getContactMap(){
        return this.mContactMap;
    }

    public ArrayList<Contact> getContactList() {
        this.refreshContactList();
        return this.mContactList;
    }

    public int getContactCount(Contact.ContactType contactType){
        int count = 0;
        for(Contact contact: mContactMap.values()){
            if(contact.getContactType() == contactType){
                count++;
            }
        }
        return count;
    }

    public void clear(){
        for(Contact contact:mContactList){
            contact.releaseMemory();
        }
        mContactList.clear();
        mContactMap.clear();

    }
}
