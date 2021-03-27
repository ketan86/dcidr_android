package com.example.turbo.dcidr.utils.common_utils;

import java.util.ArrayList;

/**
 * Created by Turbo on 6/24/2016.
 */

public class MyCalendar {
    private String mCalId;
    private String mCalName;
    private ArrayList<CalendarEvent> calendarEventArrayList;
    public MyCalendar(){
    }

    public void setCalId(String calId){
        this.mCalId = calId;
    }
    public void setCalName(String calName){
        this.mCalName = calName;
    }

    public String getCalId(){
        return this.mCalId;
    }

    public String getCalName(){
        return this.mCalName;
    }

    public void addCalEvent(CalendarEvent calendarEvent){
        this.calendarEventArrayList.add(calendarEvent);
    }
}
