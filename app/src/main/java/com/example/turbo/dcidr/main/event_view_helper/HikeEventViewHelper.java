package com.example.turbo.dcidr.main.event_view_helper;

import com.example.turbo.dcidr.main.event.HikeEvent;

/**
 * Created by Turbo on 9/23/2016.
 */
public class HikeEventViewHelper extends BaseEventViewHelper {
    private HikeEvent mHikeEvent;
    public HikeEventViewHelper(HikeEvent hikeEvent){
        super(hikeEvent);
        this.mHikeEvent = hikeEvent;
    }

}
