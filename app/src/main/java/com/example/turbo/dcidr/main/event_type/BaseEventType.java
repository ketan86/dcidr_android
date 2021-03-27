package com.example.turbo.dcidr.main.event_type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Turbo on 4/6/2016.
 */
public class BaseEventType implements Serializable, Comparable {

    private String mEventType;
    private long mEventTypeId;
    protected int mSelectNewEventCustomLayout;
    protected int mEventTypeDrawableIcon;
    protected int mEventColor;


    public BaseEventType(){
        this.mSelectNewEventCustomLayout = R.layout.activity_select_new_event_custom_view;
        this.mEventTypeDrawableIcon = R.drawable.lock_icon;
        this.mEventColor = R.color.colorPrimary;

    }
    public String getEventType(){
        return mEventType;
    }
    public int getEventColor(){
        return this.mEventColor;
    }

    public int getSelectNewEventCustomLayout(){
        return mSelectNewEventCustomLayout;
    }
    public int getEventTypeDrawableIcon(){
        return mEventTypeDrawableIcon;
    }

    public long getEventTypeId(){
        return mEventTypeId;
    }

    public void setEventType(String eventType){
        this.mEventType = eventType;
    }

    public void setEventTypeId(long eventTypeId){
        this.mEventTypeId = eventTypeId;
    }

    @Override
    public int compareTo(Object another) {
       // nothing now
       return 1;
    }

    public void populateMe(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("eventType")) {
            this.setEventType(jsonObject.getString("eventType"));
        }
        if (jsonObject.has("eventTypeId")) {
            this.setEventTypeId(jsonObject.getLong("eventTypeId"));
        }

    }

    public View getSelectNewEventView(Context context, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(getSelectNewEventCustomLayout(), parent, false);
        ImageView selectNewEventImage = (ImageView) convertView.findViewById(R.id.select_new_event_image);
        TextView selectNewEventNameText = (TextView) convertView.findViewById(R.id.select_new_event_name_text);
        selectNewEventImage.setBackgroundResource(this.getEventTypeDrawableIcon());
        selectNewEventNameText.setText(Utils.capitalizeFirstLetter(this.getEventType().toString()));
        return convertView;
    }
}
