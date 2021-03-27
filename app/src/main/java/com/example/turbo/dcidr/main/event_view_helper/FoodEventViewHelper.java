package com.example.turbo.dcidr.main.event_view_helper;

import com.example.turbo.dcidr.main.event.FoodEvent;

/**
 * Created by Turbo on 9/23/2016.
 */
public class FoodEventViewHelper extends BaseEventViewHelper {
    private FoodEvent mFoodEvent;
    public FoodEventViewHelper(FoodEvent foodEvent){
        super(foodEvent);
        this.mFoodEvent = foodEvent;
    }

}
