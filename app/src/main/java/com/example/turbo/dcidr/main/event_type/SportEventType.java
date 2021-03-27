package com.example.turbo.dcidr.main.event_type;

import com.example.turbo.dcidr.R;

/**
 * Created by Turbo on 4/6/2016.
 */
public class SportEventType extends BaseEventType {

    public SportEventType(){
        this.mEventColor = R.color.md_green_400;
        this.mEventTypeDrawableIcon = R.drawable.biking_icon;
    }

    public int getEventColor(){
        return this.mEventColor;
    }

    public int getEventTypeDrawableIcon(){
        return mEventTypeDrawableIcon;
    }

}
