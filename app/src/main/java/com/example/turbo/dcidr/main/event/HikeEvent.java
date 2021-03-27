package com.example.turbo.dcidr.main.event;

import android.content.Context;

import com.example.turbo.dcidr.main.container.UserEventStatusContainer;
import com.example.turbo.dcidr.main.event_type.HikeEventType;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Turbo on 2/13/2016.
 */
public class HikeEvent extends BaseEvent implements Serializable{
    private HikeEventType mHikeEventType;
    private UserEventStatusContainer mUserEventStatusContainer;

    public HikeEvent(Context context) {
        super(context);
        this.mHikeEventType = new HikeEventType();
    }




    public HikeEventType getEventTypeObj(){
        return mHikeEventType;
    }
}
