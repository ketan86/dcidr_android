package com.example.turbo.dcidr.main.container;

import android.content.Context;

import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Turbo on 2/18/2016.
 */
public class EventContainer implements Serializable {

    private ArrayList<BaseEvent> mEventList;
    private HashMap<Long,BaseEvent> mEventMap;
    private BaseEvent.EventSortKey mEventSortKey;
    private BaseGroup mBaseGroup;
    private Context mContext;

    public EventContainer(Context context) {
        this.mContext = context;
        this.mEventList = new ArrayList<BaseEvent>();
        this.mEventMap = new HashMap<Long,BaseEvent>();

    }
    public void populateEvent(JSONObject jsonEventObject) {
        String eventType = null;
        try {
            if(mEventMap.containsKey(jsonEventObject.getLong("eventId"))) {
                //mGroupMap.get(jsonGroupObject.getLong("groupId")).incrementUnreadEventCount();
                mEventMap.get(jsonEventObject.getLong("eventId")).populateMe(jsonEventObject);
                //mGroupMap.get(jsonGroup.getInt("groupId")).populateGroup(jsonGroup.getJSONArray("events"));
                return;
            }
            eventType = BaseEvent.EventType.getType(jsonEventObject.getInt("eventTypeId")).toString();
            eventType = DcidrConstant.EVENT_PACKAGE_PATH +  Utils.capitalizeFirstLetter(eventType);
            Class cls = Class.forName(eventType + "Event");
            Constructor cons = cls.getConstructor(Context.class);
            Object event = cons.newInstance(mContext);
            BaseEvent baseEvent = (BaseEvent)event;
            baseEvent.setEventSortKey(mEventSortKey);
            baseEvent.setBaseGroup(mBaseGroup);
            baseEvent.populateMe(jsonEventObject);
            // getMethod searches baseclass where getDeclaredMethod only looks at given class
            //Method method = event.getClass().getMethod("populateMe", new Class<?>[]{JSONObject.class});
            //method.invoke(event, jsonEventObject);
            mEventMap.put(baseEvent.getEventId(), baseEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void populateEvent(JSONArray jsonEventArray) {
        for (int i = 0; i < jsonEventArray.length(); i++) {
            try {
                this.populateEvent(jsonEventArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void setSortKey(BaseEvent.EventSortKey key) {
        mEventSortKey = key;
    }
    public void setBaseGroup(BaseGroup baseGroup) {
        mBaseGroup = baseGroup;
    }
    public void refreshEventList(){
        // TODO need to return list sorted by last modified time
        mEventList.clear();
        ArrayList<BaseEvent> arrayList = new ArrayList<BaseEvent>(mEventMap.values());
        Collections.sort(arrayList);
        mEventList.addAll(arrayList);
    }

    public HashMap<Long,BaseEvent> getEventMap(){
        return mEventMap;
    }
    public ArrayList<BaseEvent> getEventList() {
        this.refreshEventList();
        return this.mEventList;
    }

    public void releaseMemory(){
        for(BaseEvent baseEvent: this.mEventList){
            baseEvent.getChildEventsContainer().getEventList().clear();
            baseEvent.releaseMemory();
        }
    }
}
