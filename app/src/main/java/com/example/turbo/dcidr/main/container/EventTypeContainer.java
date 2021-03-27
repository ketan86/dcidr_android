package com.example.turbo.dcidr.main.container;

import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.event_type.BaseEventType;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Turbo on 4/6/2016.
 */
public class EventTypeContainer implements Serializable {

    private ArrayList<BaseEventType> mEventTypeList;
    private HashMap<Long,BaseEventType> mEventTypeMap;

    public EventTypeContainer() {
        this.mEventTypeList = new ArrayList<BaseEventType>();
        this.mEventTypeMap = new HashMap<Long,BaseEventType>();

    }
    public void populateEventType(JSONArray jsonEventArray) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        for (int i = 0; i < jsonEventArray.length(); i++) {
            JSONObject jsonEventType =  jsonEventArray.getJSONObject(i);
            String eventType = jsonEventType.getString("eventType");
            if (!eventType.equals("UNKNOWN")) {
                eventType = DcidrConstant.EVENT_TYPE_PACKAGE_PATH + Utils.capitalizeFirstLetter(eventType);
                Class cls = Class.forName(eventType + "EventType");
                Object event = cls.newInstance();
                // getMethod searches baseclass where getDeclaredMethod only looks at given class
                Method method = event.getClass().getMethod("populateMe", new Class<?>[]{JSONObject.class});
                method.invoke(event, jsonEventType);
                BaseEventType baseEventType = (BaseEventType) event;
                mEventTypeMap.put(baseEventType.getEventTypeId(), baseEventType);
            }
        }
    }

    public void refreshEventTypeList(){
        // TODO need to return list sorted by last modified time
        mEventTypeList.clear();
        ArrayList<BaseEventType> arrayList = new ArrayList<BaseEventType>(mEventTypeMap.values());
        Collections.sort(arrayList);
        mEventTypeList.addAll(arrayList);
    }

    public ArrayList<BaseEventType> getEventTypeList() {
        return this.mEventTypeList;
    }

}
