//package com.example.turbo.dcidr.main.event;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.example.turbo.dcidr.R;
//import com.example.turbo.dcidr.main.event_type.SportEventType;
//import com.example.turbo.dcidr.utils.common_utils.Utils;
//
//import java.util.TimeZone;
//
///**
// * Created by Turbo on 2/15/2016.
// */
//public class SportEvent extends BaseEvent {
//    private SportEventType mSportEventType;
//    public SportEvent() {
//        this.mSportEventType = new SportEventType();
//    }
//
//    public SportEventType getEventTypeObj(){
//        return mSportEventType;
//    }
//    public View getHistoryView(Context context, ViewGroup parent) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View convertView = inflater.inflate(getHistoryCustomLayout(), parent, false);
//        TextView historyEventName = (TextView) convertView.findViewById(R.id.history_event_name);
//        TextView historyEventTypeIcon = (TextView) convertView.findViewById(R.id.history_event_type_icon);
//        TextView historyEventPlace = (TextView) convertView.findViewById(R.id.history_event_place);
//        TextView historyEventStartDate = (TextView) convertView.findViewById(R.id.history_event_start_date);
//        TextView historyEventStartTime = (TextView) convertView.findViewById(R.id.history_event_start_time);
//        TextView historyEventEndDate = (TextView) convertView.findViewById(R.id.history_event_end_date);
//        TextView historyEventEndTime = (TextView) convertView.findViewById(R.id.history_event_end_time);
//        //TextView historyEventPlaceDistance = (TextView) convertView.findViewById(R.id.history_event_place_distance);
//        //Button historyEventMembers = (Button) convertView.findViewById(R.id.history_event_members);
//        Button historyEventMembers = (Button) convertView.findViewById(R.id.history_event_members);
//        TextView historyEventColor = (TextView) convertView.findViewById(R.id.event_color);
//        historyEventName.setText(Utils.capitalizeFirstLetter(this.getEventName().toString()));
//        historyEventPlace.setText(this.getLocationName());
//        //historyEventPlaceDistance.setText(String.valueOf(this.getLocationCoordinatesString()) + " Lat/Long");
//        historyEventColor.setBackgroundColor(Color.parseColor(this.mSportEventType.getEventColor()));
//        historyEventTypeIcon.setBackgroundResource(this.mSportEventType.getEventTypeDrawableIcon());
//        String[] startDateTimeArray = Utils.convertEpochToDateTime(this.getStartTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa");
//        historyEventStartDate.setText(startDateTimeArray[0]);
//        historyEventStartTime.setText(startDateTimeArray[1]);
//        historyEventEndDate.setText(startDateTimeArray[0]);
//        historyEventEndTime.setText(startDateTimeArray[1]);
//        historyEventMembers.setText(String.valueOf(mBaseGroup.getMemberCount()));
//        return convertView;
//    }
//
//    public View getSelectedGroupEventCustomView(Context context, ViewGroup parent) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View convertView = inflater.inflate(getSelectedGroupEventCustomLayout(), parent, false);
//        TextView selectedGroupEventType = (TextView) convertView.findViewById(R.id.selected_group_event_type);
//        TextView selectedGroupEventTypeIcon = (TextView) convertView.findViewById(R.id.selected_group_event_type_icon);
//        TextView selectedGroupEventPlace = (TextView) convertView.findViewById(R.id.selected_group_event_place);
//        TextView selectedGroupEventStartDate = (TextView) convertView.findViewById(R.id.selected_group_event_start_date);
//        TextView selectedGroupEventStartTime = (TextView) convertView.findViewById(R.id.selected_group_event_start_time);
//        TextView selectedGroupEventEndDate = (TextView) convertView.findViewById(R.id.selected_group_event_end_date);
//        TextView selectedGroupEventEndTime = (TextView) convertView.findViewById(R.id.selected_group_event_end_time);
//        //TextView selectedGroupEventPlaceDistance = (TextView) convertView.findViewById(R.id.selected_group_event_place_distance);
//        TextView selectedGroupEventColor = (TextView) convertView.findViewById(R.id.selected_group_event_color);
//        TextView selectedGroupDecisionTimer = (TextView) convertView.findViewById(R.id.selected_group_decision_timer);
//        selectedGroupEventType.setText(Utils.capitalizeFirstLetter(this.getEventName().toString()));
//        selectedGroupEventPlace.setText(this.getLocationName());
//        //selectedGroupEventPlaceDistance.setText(String.valueOf(this.getLocationCoordinatesString()) + " Lat/Long");
//        selectedGroupEventColor.setBackgroundColor(Color.parseColor(this.mSportEventType.getEventColor()));
//        selectedGroupEventTypeIcon.setBackgroundResource(this.mSportEventType.getEventTypeDrawableIcon());
//        String[] startDateTimeArray = Utils.convertEpochToDateTime(this.getStartTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa");
//        selectedGroupEventStartDate.setText(startDateTimeArray[0]);
//        selectedGroupEventStartTime.setText(startDateTimeArray[1]);
//        selectedGroupEventEndDate.setText(startDateTimeArray[0]);
//        selectedGroupEventEndTime.setText(startDateTimeArray[1]);
//        setViewWithTimeLeft(this.getDecideByTime(), selectedGroupDecisionTimer);
//        if(getHasExpired()){
//            convertView.setEnabled(false);
//            convertView.setBackgroundResource(R.color.colorGrayDark);
//        }
//        return convertView;
//    }
//}
