package com.example.turbo.dcidr.utils.common_utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.main.event.BaseEvent;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by Turbo on 6/23/2016.
 */
public class CalendarHandler {
    private BaseActivity mBaseActivity;

    public CalendarHandler(BaseActivity baseActivity) {
        this.mBaseActivity = baseActivity;
    }

    public int getEventCount(long startTime, long endTime) {
        String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.ALL_DAY,
                CalendarContract.Events.EVENT_LOCATION};
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime + " ))";
//        if (ActivityCompat.checkSelfPermission(mBaseActivity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return 0;
//        }
        Cursor cursor = mBaseActivity.getContentResolver().query(Uri.parse(getCalendarUriBase(true) + "events"), projection, selection, null, null);

//        if (cursor.moveToFirst()) {
//            do {
//                Toast.makeText(mBaseActivity.getApplicationContext(), "Title: " + cursor.getString(1) + " Start-Time: " + (new Date(cursor.getLong(3))).toString(), Toast.LENGTH_LONG).show();
//            } while (cursor.moveToNext());
//        }
        return cursor.getCount();
    }

    public void pushEventToCalendar(String calId, BaseEvent baseEvent) {
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = mBaseActivity.getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        /** Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.TITLE, baseEvent.getEventName());
        values.put(CalendarContract.Events.DESCRIPTION, "A test Reminder.");
        values.put(CalendarContract.Events.ALL_DAY, 0);
        values.put(CalendarContract.Events.DTSTART, baseEvent.getStartTime());
        values.put(CalendarContract.Events.DTEND, baseEvent.getEndTime());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);

        // Display event id.
        Toast.makeText(mBaseActivity.getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. */
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);
        cr.insert(REMINDERS_URI, values);

    }

    public ArrayList<MyCalendar> getCalendars() {
        String projection[] = {"_id", "calendar_displayName"};
        ArrayList<MyCalendar> myCalendars = new ArrayList<MyCalendar>();
        //HashMap<String, String> myCalendersMap = new HashMap<String, String>();
        //CharSequence[] charSequences = new CharSequence[0];
        ContentResolver contentResolver = mBaseActivity.getContentResolver();
        if (ActivityCompat.checkSelfPermission(mBaseActivity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(mBaseActivity, new String[]{
                                    Manifest.permission.READ_CALENDAR,
                            }, 1);
        }
        Cursor managedCursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);

        if (managedCursor.moveToFirst()){
            String calName;
            String calID;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                MyCalendar myCalender  = new MyCalendar();
                calName = managedCursor.getString(nameCol);
                calID = managedCursor.getString(idCol);
                myCalender.setCalId(calID);
                myCalender.setCalName(calName);
                myCalendars.add(myCalender);
                //myCalendersMap.put(calName, calID);
            } while(managedCursor.moveToNext());
            managedCursor.close();
        }
        return myCalendars;

    }

    /** Returns Calendar Base URI, supports both new and old OS. */
    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
    



}
