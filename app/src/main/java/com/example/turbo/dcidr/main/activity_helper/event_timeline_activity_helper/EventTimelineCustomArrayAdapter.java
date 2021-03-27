package com.example.turbo.dcidr.main.activity_helper.event_timeline_activity_helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Turbo on 8/27/2016.
 */
public class EventTimelineCustomArrayAdapter extends ArrayAdapter<BaseEvent> {

    public EventTimelineCustomArrayAdapter(Context context, int resource, ArrayList<BaseEvent> eventList) {
        super(context, resource, eventList);
    }

    public View getView(int position, View convertView, final ViewGroup parent) {
        BaseEvent event = getItem(position);
            try {
                String eventViewHelper = DcidrConstant.EVENT_VIEW_HELPER_PACKAGE_PATH +  Utils.capitalizeFirstLetter(event.getEventTypeStr());
                Class cls = Class.forName(eventViewHelper + "EventViewHelper");
                Constructor cons = cls.getConstructor(event.getClass());
                Object eventViewHelperObj = cons.newInstance(event);
                Method method = eventViewHelperObj.getClass().getMethod("getEventTimelineCustomView", new Class<?>[]{int.class, Context.class, View.class, ViewGroup.class});
                convertView = (View) method.invoke(eventViewHelperObj, position, getContext(), convertView, parent);
            } catch (Exception e) {
                e.printStackTrace();
        }
        return convertView;
    }
}
