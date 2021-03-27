package com.example.turbo.dcidr.main.activity_helper.select_new_event_helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.turbo.dcidr.main.event_type.BaseEventType;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Turbo on 2/18/2016.
 */
public class SelectNewEventCustomArrayAdapter  extends ArrayAdapter<BaseEventType> {
    public SelectNewEventCustomArrayAdapter(Context context, int resource, ArrayList<BaseEventType> eventArrayList) {
        super(context, resource, eventArrayList);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        //based on which object baseArrayAdapter is pointing to, call that object's getHistoryView
        Object event = getItem(position);
        try {
            Method method = event.getClass().getMethod("getSelectNewEventView", new Class<?>[]{Context.class, ViewGroup.class});
            convertView = (View) method.invoke(event, getContext(),parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
