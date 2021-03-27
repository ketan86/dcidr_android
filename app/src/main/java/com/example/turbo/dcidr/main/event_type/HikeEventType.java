package com.example.turbo.dcidr.main.event_type;

import com.example.turbo.dcidr.R;

/**
 * Created by Turbo on 4/6/2016.
 */
public class HikeEventType extends BaseEventType {

    public HikeEventType(){
        this.mEventColor = R.color.md_teal_200;
        this.mEventTypeDrawableIcon = R.drawable.mountain_icon;
    }
    
    public int getEventColor(){
        return this.mEventColor;
    }

    public int getEventTypeDrawableIcon(){
        return mEventTypeDrawableIcon;
    }

}
