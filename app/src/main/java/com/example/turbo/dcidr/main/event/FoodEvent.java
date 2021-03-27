package com.example.turbo.dcidr.main.event;

import android.content.Context;

import com.example.turbo.dcidr.main.container.UserEventStatusContainer;
import com.example.turbo.dcidr.main.event_type.FoodEventType;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Turbo on 2/13/2016.
 */
public class FoodEvent extends BaseEvent implements Serializable{
    private FoodEventType mFoodEventType;
    private UserEventStatusContainer mUserEventStatusContainer;

    public FoodEvent(Context context) {
        super(context);
        this.mFoodEventType = new FoodEventType();
    }




    public FoodEventType getEventTypeObj(){
        return mFoodEventType;
    }
}
