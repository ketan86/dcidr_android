package com.example.turbo.dcidr.main.event_type;

import com.example.turbo.dcidr.R;

/**
 * Created by Turbo on 4/6/2016.
 */
public class FoodEventType extends BaseEventType {


    public FoodEventType(){
        this.mEventColor = R.color.peachColor;
        this.mEventTypeDrawableIcon = R.drawable.restaurant_icon;
    }

    public int getEventColor(){
        return this.mEventColor;
    }

    public int getEventTypeDrawableIcon(){
        return mEventTypeDrawableIcon;
    }

}
