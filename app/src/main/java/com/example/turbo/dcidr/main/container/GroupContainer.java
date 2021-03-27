package com.example.turbo.dcidr.main.container;

import android.content.Context;

import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.group.BaseGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Turbo on 2/25/2016.
 */
public class GroupContainer implements Serializable{
    private ArrayList<BaseGroup> mGroupList;
    private ArrayList<BaseEvent> mGroupContainerEventList;
    private HashMap<Long,BaseGroup> mGroupMap;
    private BaseGroup.GroupSortKey mGroupSortKey;
    private BaseEvent.EventSortKey mEventSortKey;
    private Context mContext;
    public GroupContainer(Context context) {
        this.mContext = context;
        this.mGroupList = new ArrayList<BaseGroup>();
        this.mGroupContainerEventList = new ArrayList<BaseEvent>();
        this.mGroupMap = new HashMap<Long,BaseGroup>();
    }
    public void populateGroup(JSONObject jsonGroupObject) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
            // check if group already exist, if yes then add event to that group
            if(mGroupMap.containsKey(jsonGroupObject.getLong("groupId"))) {
                //mGroupMap.get(jsonGroupObject.getLong("groupId")).incrementUnreadEventCount();
                mGroupMap.get(jsonGroupObject.getLong("groupId")).populateMe(jsonGroupObject);
                //mGroupMap.get(jsonGroup.getInt("groupId")).populateGroup(jsonGroup.getJSONArray("events"));
                return;
            }

            BaseGroup baseGroup = new BaseGroup(mContext);
            baseGroup.setGroupSortKey(mGroupSortKey);
            baseGroup.getEventContainer().setSortKey(mEventSortKey);
            baseGroup.getEventContainer().setBaseGroup(baseGroup);
            baseGroup.populateMe(jsonGroupObject);

            mGroupMap.put(baseGroup.getGroupId(), baseGroup);
    }
    public void populateGroup(JSONArray jsonGroupArray) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        for (int i = 0; i < jsonGroupArray.length(); i++) {
            JSONObject jsonGroupObject =  jsonGroupArray.getJSONObject(i);
            populateGroup(jsonGroupObject);
        }
        refreshGroupList();

    }
    public HashMap<Long,BaseGroup> getGroupMap() {
        return mGroupMap;
    }
    public ArrayList<BaseEvent> getGroupContainerEventList() {
        //iterate through the mGroupList , each of which is a BaseGroup object, which
        //contains EventContainer which contains events
        mGroupContainerEventList.clear();
        for (BaseGroup baseGroup: mGroupMap.values()) {
            mGroupContainerEventList.addAll(baseGroup.getEventContainer().getEventList());
        }
        Collections.sort(mGroupContainerEventList);
        return mGroupContainerEventList;
    }

    public void refreshGroupContainerEventList(){
        //mGroupContainerEventList.clear();
        //Collections.sort(getGroupContainerEventList());
        getGroupContainerEventList();
    }

    public void setGroupSortKey(BaseGroup.GroupSortKey key) {
        mGroupSortKey = key;
    }
    public void setEventSortKey(BaseEvent.EventSortKey key) {
        mEventSortKey = key;
    }
    public void refreshGroupList(){
        // TODO need to return list sorted by last modified time
        mGroupList.clear();
        ArrayList<BaseGroup> arrayList = new ArrayList<BaseGroup>(mGroupMap.values());
        Collections.sort(arrayList);
        mGroupList.addAll(arrayList);
    }

    public ArrayList<BaseGroup> getGroupList() {
        refreshGroupList();
        return this.mGroupList;
    }

    public int getEventCount() {
        int count =0;
        for (BaseGroup baseGroup: mGroupMap.values()) {
            for (BaseEvent baseEvent: baseGroup.getEventContainer().getEventList()) {
                count++;
            }
        }
        return count;
    }

    public void clear(){
        mGroupList.clear();
        mGroupMap.clear();
    }
}
