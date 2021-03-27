package com.example.turbo.dcidr.main.event_view_helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.MapViewActivity;
import com.example.turbo.dcidr.android_activity.SelectedEventActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.EventAsyncHttpClient;
import com.example.turbo.dcidr.main.container.UserEventStatusContainer;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.user.UserEventStatus;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mypopsy.maps.StaticMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 9/23/2016.
 */
public class BaseEventViewHelper {

    private BaseEvent mBaseEvent;
    public BaseEventViewHelper(BaseEvent baseEvent){
        this.mBaseEvent = baseEvent;
    }
    public static class HistoryCustomViewHolder {

        TextView historyEventName ;
        TextView historyEventTypeIcon;
        TextView historyEventPlace;
        TextView historyEventStartDate;
        TextView historyEventStartTime;
        TextView historyEventEndDate;
        TextView historyEventEndTime;
        //TextView historyEventPlaceDistance = (TextView) convertView.findViewById(R.id.history_event_place_distance);
        Button historyEventMembers;
        TextView historyEventColor;
    }


    public View getHistoryView(final int position, final Context context, View convertView, final ViewGroup parent) {

        final HistoryCustomViewHolder holder;
        // list view creates visible + 1 unique converView objects are reuses during scrolling
        // if convertView is null, inflate the layout and create a new holderView object
        // tag viewHolder to convertView so can be retrieve when convertView is not null
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            holder = new HistoryCustomViewHolder();
            convertView = inflater.inflate(mBaseEvent.getHistoryCustomLayout(), parent, false);
            holder.historyEventName = (TextView) convertView.findViewById(R.id.history_event_name);
            holder.historyEventTypeIcon = (TextView) convertView.findViewById(R.id.history_event_type_icon);
            holder.historyEventPlace = (TextView) convertView.findViewById(R.id.history_event_place);
            holder.historyEventStartDate = (TextView) convertView.findViewById(R.id.history_event_start_date);
            holder.historyEventStartTime = (TextView) convertView.findViewById(R.id.history_event_start_time);
            holder.historyEventEndDate = (TextView) convertView.findViewById(R.id.history_event_end_date);
            holder.historyEventEndTime = (TextView) convertView.findViewById(R.id.history_event_end_time);
            //TextView historyEventPlaceDistance = (TextView) convertView.findViewById(R.id.history_event_place_distance);
            holder.historyEventMembers = (Button) convertView.findViewById(R.id.history_event_members);
            holder.historyEventColor = (TextView) convertView.findViewById(R.id.event_color);
        } else {
            holder = (HistoryCustomViewHolder) convertView.getTag();
        }
        //Set Values
        holder.historyEventName.setText(Utils.capitalizeFirstLetter(mBaseEvent.getEventName().toString()));
        holder.historyEventPlace.setText(mBaseEvent.getLocationName());
        //historyEventPlaceDistance.setText(String.valueOf(this.getLocationCoordinatesString()) + " Lat/Long");
        holder.historyEventColor.setBackgroundColor(mBaseEvent.getEventTypeObj().getEventColor());
        holder.historyEventTypeIcon.setBackgroundResource(mBaseEvent.getEventTypeObj().getEventTypeDrawableIcon());
        String startDateTime = Utils.convertEpochToDateTime(mBaseEvent.getStartTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa");
        String tokens[] =  startDateTime.split(" ");
        String startDate = tokens[0];
        String startTime = tokens[1] + " " +  tokens[2] ;
        holder.historyEventStartDate.setText(startDate);
        holder.historyEventStartTime.setText(startTime);

        String endDateTime = Utils.convertEpochToDateTime(mBaseEvent.getEndTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa");
        tokens =  endDateTime.split(" ");
        String endDate = tokens[0];
        String endTime = tokens[1] + " " +  tokens[2] ;
        holder.historyEventEndDate.setText(endDate);
        holder.historyEventEndTime.setText(endTime);
        //get the memberCount from

        Log.i("Ketan", "setting for" + String.valueOf(mBaseEvent.getBaseGroup().getGroupId()));
        holder.historyEventMembers.setText(String.valueOf(mBaseEvent.getBaseGroup().getMemberCount()));
        return convertView;
    }


    public static class CustomViewHolder
    {
        ImageView eventMapPreview;
        ImageView eventPic;
        TextView createdByUserName;
        TextView eventPlace;
        TextView eventName;
        //TextView eventCreationTime;
        TextView eventStartDate;
        TextView eventStartTime;
        TextView eventEndDate;
        TextView eventEndTime;
        TextView selectedGroupDecisionTimer;
        ImageButton eventSelectionOptions;
        Button acceptEventButton;
        Button declineEventButton;
        Button acceptedMembersCount;
        //TextView declinedMembersCount;
        TextView eventStatusLabel;
        TextView eventColorTypeStripe;
        TextView eventNotes;
        RelativeLayout eventStatusInfoRelativeLayout;
        RelativeLayout eventInfoRelativeLayout;
        TextView distanceToPlace;
        int position;
        int eventPicWidth;
        int eventPicHeight;
    }

    public View getEventTimelineCustomView(final int position, final Context context, View convertView, final ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        final BaseActivity baseActivity = (BaseActivity) context;
        final CustomViewHolder holder;
        // list view creates visible + 1 unique converView objects are reuses during scrolling
        // if convertView is null, inflate the layout and create a new holderView object
        // tag viewHolder to convertView so can be retrieve when convertView is not null
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.event_timeline_custom_view, parent, false);
            holder = new CustomViewHolder();
            holder.eventMapPreview = (ImageView) convertView.findViewById(R.id.event_map_preview);
            holder.eventPic = (ImageView) convertView.findViewById(R.id.event_pic);
            holder.eventPicWidth = holder.eventPic.getDrawable().getIntrinsicWidth();
            holder.eventPicHeight = holder.eventPic.getDrawable().getIntrinsicHeight();
            holder.createdByUserName = (TextView) convertView.findViewById(R.id.created_by_user_name);
            holder.eventPlace = (TextView) convertView.findViewById(R.id.event_place);
            //holder.eventCreationTime = (TextView) convertView.findViewById(R.id.event_creation_time);
            holder.eventName = (TextView) convertView.findViewById(R.id.event_name);
            holder.eventStartDate = (TextView) convertView.findViewById(R.id.event_start_date);
            holder.eventStartTime = (TextView) convertView.findViewById(R.id.event_start_time);
            holder.eventEndDate = (TextView) convertView.findViewById(R.id.event_end_date);
            holder.eventEndTime = (TextView) convertView.findViewById(R.id.event_end_time);
            holder.selectedGroupDecisionTimer = (TextView) convertView.findViewById(R.id.event_expire_time);
            //holder.expireEventOverlay = (View) convertView.findViewById(R.id.expire_event_overlay);
            //holder.eventSelectionOptions = (ImageButton) convertView.findViewById(R.id.event_selection_options);
            holder.acceptEventButton = (Button) convertView.findViewById(R.id.accept_button);
            holder.declineEventButton = (Button) convertView.findViewById(R.id.decline_button);
            holder.acceptedMembersCount = (Button) convertView.findViewById(R.id.accepted_members_count);
            //holder.declinedMembersCount = (TextView) convertView.findViewById(R.id.declined_members_count);
            holder.eventStatusLabel = (TextView) convertView.findViewById(R.id.event_status_label);
            holder.eventColorTypeStripe = (TextView) convertView.findViewById(R.id.event_type_color_stripe);
            holder.eventNotes = (TextView) convertView.findViewById(R.id.event_notes);
            holder.eventStatusInfoRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.event_status_info_relative_layout);
            holder.eventInfoRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.event_info_relative_layout);
            holder.distanceToPlace = (TextView) convertView.findViewById(R.id.distance_to_place);
            convertView.setTag(holder);
        }else {
            holder = (CustomViewHolder) convertView.getTag();
        }

        // lets assign a current position to holder position
        // it helps to decide if data to load to view or not
        // for ex, during fast scrolling, holder.imageView reference may get overridden by the time,
        // image is loaded into expected view
        holder.position = position;

        // set image bitmap to null before actual image is loaded into imageView
        holder.eventMapPreview.setImageBitmap(null);

        // set event pic image bitmap to null
        holder.eventPic.setImageBitmap(null);

        // set overlay to Gone as default
        //holder.expireEventOverlay.setVisibility(View.GONE);

        // set userEventStatus count to 0 as default
        holder.acceptedMembersCount.setText("0");
        //holder.declinedMembersCount.setText("0");

        // set default event status selector button to menu
        //holder.eventSelectionOptions.setImageResource(R.drawable.menu_icon);

        // set event color stripe
        holder.eventColorTypeStripe.setBackgroundColor(ContextCompat.getColor(context, mBaseEvent.getEventTypeObj().getEventColor()));

        if(mBaseEvent.getDistanceFromCurrentLoc() == -1) {
            baseActivity.onLocationUpdateListener(new BaseActivity.LocationUpdateInterface() {
                @Override
                public void onLocationChange(Location location) {
                    // set distance
                    if (holder.position == position) {
                        float[] resultArray = new float[1];
                        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                mBaseEvent.getLocationCoordinatesPoints().getLatitude(),
                                mBaseEvent.getLocationCoordinatesPoints().getLongitude(), resultArray);
                        mBaseEvent.setDistanceFromCurrentLoc(resultArray[0]);
                        holder.distanceToPlace.setText(mBaseEvent.getDistanceFromCurrentLocInMilesStr());
                    }
                }
            });
        }else {
            holder.distanceToPlace.setText(mBaseEvent.getDistanceFromCurrentLocInMilesStr());
        }

        //if(holder.position == position) {
        //launch a async task to fetch the group image from the server and store it into the BaseGroup object
        if(mBaseEvent.getEventPicBitmap() != null) {
            holder.eventPic.setImageBitmap(mBaseEvent.getEventPicBitmap());
        }else if(mBaseEvent.getEventPicBase64Str() != null && mBaseEvent.getEventPicBitmap() == null) {
            mBaseEvent.setEventPicBitmap(Utils.decodeBase64(mBaseEvent.getEventPicBase64Str(),
                    holder.eventPicWidth, holder.eventPicHeight));
            holder.eventPic.setImageBitmap(mBaseEvent.getEventPicBitmap());
        }else {
            mBaseEvent.new EventPicAsyncLoad().execute("Fetching event pic");
            mBaseEvent.onEventPicFetchDoneListener(new BaseEvent.EventPicFetchInterface() {
                @Override
                public void onImageFetchDone(Bitmap bitmap) {
                    if (holder.position == position) {
                        holder.eventPic.setImageBitmap(bitmap);
                    }
                }

                @Override
                public int getImageWidth() {
                    return holder.eventPicWidth;
                }

                @Override
                public int getImageHeight() {
                    return holder.eventPicHeight;
                }
            });
        }
        //}


        // set image on click
        holder.eventPic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mBaseEvent.getEventPicBitmap() != null) {
                    baseActivity.showImageViewDialog(mBaseEvent.getEventPicBase64Str(), null);
                }
            }
        });

        // hide accept/decline button when clicked on event status info relative layout
        holder.eventStatusInfoRelativeLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                holder.acceptEventButton.setVisibility(View.GONE);
                holder.declineEventButton.setVisibility(View.GONE);
                Intent selectedEventActivityIntent = new Intent(context, SelectedEventActivity.class);
                selectedEventActivityIntent.putExtra(context.getString(R.string.selected_group_id), mBaseEvent.getGroupIdStr());
                selectedEventActivityIntent.putExtra(context.getString(R.string.selected_event_id), mBaseEvent.getEventIdStr());
                selectedEventActivityIntent.putExtra(context.getString(R.string.parent_event_id), mBaseEvent.getParentEventIdStr());
                selectedEventActivityIntent.putExtra(context.getString(R.string.parent_event_type), mBaseEvent.getParentEventTypeStr());
                selectedEventActivityIntent.putExtra(context.getString(R.string.source_key), "LOCAL");
                context.startActivity(selectedEventActivityIntent);
            }
        });


        // show selected event info
        holder.eventInfoRelativeLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                holder.eventStatusInfoRelativeLayout.performClick();
            }
        });


        holder.eventNotes.setText(mBaseEvent.getEventNotes());

        // if(holder.position == position) {
        holder.eventMapPreview.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapViewIntent = new Intent(context, MapViewActivity.class);
                mapViewIntent.putExtra("LATITUDE", mBaseEvent.getLocationCoordinatesPoints().getLatitude());
                mapViewIntent.putExtra("LONGITUDE", mBaseEvent.getLocationCoordinatesPoints().getLongitude());
                mapViewIntent.putExtra("MARKER_TITLE", mBaseEvent.getLocationName());
                mapViewIntent.putExtra("MARKER_SNIPPET", "Created by, " + mBaseEvent.getCreatedByName());
                context.startActivity(mapViewIntent);
            }
        });
        //}

        //holder.eventSelectionOptions.setVisibility(View.VISIBLE);
        holder.acceptEventButton.setVisibility(View.GONE);
        holder.declineEventButton.setVisibility(View.GONE);

        holder.acceptedMembersCount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //holder.eventSelectionOptions.setVisibility(View.GONE);
                if(holder.acceptEventButton.getVisibility() == View.VISIBLE){
                    holder.acceptEventButton.setVisibility(View.GONE);
                    holder.declineEventButton.setVisibility(View.GONE);
                }else {
                    holder.acceptEventButton.setVisibility(View.VISIBLE);
                    holder.declineEventButton.setVisibility(View.VISIBLE);
                }
            }
        });

        final View finalConvertView = convertView;
        holder.acceptEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEventStatusConfirmationDialog(context, parent, position, mBaseEvent.getUserEventStatusContainer(), finalConvertView, parent.getContext().getResources().getString(R.string.accept_activity_dialog_msg), UserEventStatus.EventStatusType.ACCEPTED);
                holder.acceptEventButton.setVisibility(View.GONE);
                holder.declineEventButton.setVisibility(View.GONE);
            }
        });

        holder.declineEventButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEventStatusConfirmationDialog(context, parent, position, mBaseEvent.getUserEventStatusContainer(), finalConvertView, parent.getContext().getResources().getString(R.string.decline_activity_dialog_msg), UserEventStatus.EventStatusType.DECLINED);
                holder.acceptEventButton.setVisibility(View.GONE);
                holder.declineEventButton.setVisibility(View.GONE);
            }
        });

        holder.createdByUserName.setText(mBaseEvent.getCreatedByName());
        holder.eventPlace.setText(mBaseEvent.getLocationName());
        //holder.eventCreationTime.setText(Utils.convertEpochToDateTime(this.getEventCreationTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa"));
        holder.eventName.setText(mBaseEvent.getEventName());
        String startDateTime = Utils.convertEpochToDateTime(mBaseEvent.getStartTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa");
        String tokens[] =  startDateTime.split(" ");
        String startDate = tokens[0];
        String startTime = tokens[1] + " " +  tokens[2] ;
        holder.eventStartDate.setText(startDate);
        holder.eventStartTime.setText(startTime);

        String endDateTime = Utils.convertEpochToDateTime(mBaseEvent.getEndTime(), TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa");
        tokens =  endDateTime.split(" ");
        String endDate = tokens[0];
        String endTime = tokens[1] + " " +  tokens[2] ;
        holder.eventEndDate.setText(endDate);
        holder.eventEndTime.setText(endTime);
        mBaseEvent.setViewWithTimeLeft(context, mBaseEvent.getDecideByTime(), holder.selectedGroupDecisionTimer);

        if(mBaseEvent.getHasExpired()){
            //holder.eventSelectionOptions.setVisibility(View.GONE);
            holder.acceptedMembersCount.setEnabled(false);
        }

        // if holder.position matches the current position which will be the case unless list view
        // is scrolled really fast, attempt to download the static map
        int screenWidth = baseActivity.getWindowScreenWidth();
        int screenHeight = baseActivity.getWindowScreenHeight();
        //if(holder.position == position) {
        if(mBaseEvent.getLocationMapBitmap() == null) {
            StaticMap staticMap = new StaticMap()
                    // use screenWidth for both width and height since we want same square image
                    .size(screenWidth /4 , screenWidth/4 )
                    //.scale(2)
                    .marker(StaticMap.Marker.Style.builder().build(),
                            new StaticMap.GeoPoint(mBaseEvent.getLocationCoordinatesPoints().getLatitude(),
                                    mBaseEvent.getLocationCoordinatesPoints().getLongitude()));

            staticMap.key(context.getString(R.string.google_android_geo_api_key));
            BaseEvent.DownloadImageFromUrlTask downloadImageTask = new BaseEvent.DownloadImageFromUrlTask();
            downloadImageTask.onImageFetchDone(new BaseEvent.DownloadImageFromUrlTask.ImageFetchInterface() {
                @Override
                public void onImageFetchDone(Bitmap bitmap) {
                    // if holder position is still matches the current position of list then set
                    // imageBitmap otherwise do not set (to avoid setting multiple images on same
                    // view one after another)
                    if (holder.position == position) {
                        holder.eventMapPreview.setImageBitmap(bitmap);
                        mBaseEvent.setLocationMapBitmap(bitmap);
                    }
                }
            });
            downloadImageTask.execute(staticMap.toString());
        }else{
            if (holder.position == position) {
                holder.eventMapPreview.setImageBitmap(mBaseEvent.getLocationMapBitmap());
            }
        }
        //}

        // get userEventStatus and update the view
        //if(holder.position == position) {
        if (mBaseEvent.getUserEventStatusContainer().getUserEventStatusArray().size() == 0) {
            try {
                EventAsyncHttpClient eventAsyncHttpClient = new EventAsyncHttpClient(context);
                eventAsyncHttpClient.getUserEventStatus(DcidrApplication.getInstance().getUser().getUserIdStr(),
                        mBaseEvent.getGroupIdStr(), mBaseEvent.getEventIdStr(), mBaseEvent.getEventTypeStr(),
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200) {
                                    try {
                                        mBaseEvent.getUserEventStatusContainer().setGroupId(mBaseEvent.getGroupId());
                                        mBaseEvent.getUserEventStatusContainer().populateMe(new JSONObject(new String(responseBody)).getJSONArray("result"));
                                        UserEventStatusContainer userEventStatusContainer = mBaseEvent.getUserEventStatusContainer();
                                        HashMap<UserEventStatus.EventStatusType, Integer> statusMap = userEventStatusContainer.getUserToStatusMap();
                                        if(holder.position == position) {
                                            holder.acceptedMembersCount.setText(String.valueOf(statusMap.get(UserEventStatus.EventStatusType.ACCEPTED)));
                                            //holder.declinedMembersCount.setText(String.valueOf(statusMap.get(UserEventStatus.EventStatusType.DECLINED)));
                                            UserEventStatus userEventStatus = userEventStatusContainer.getCurrentUserEventStatusObj();
                                            if(userEventStatus != null) {
                                                if(userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.ACCEPTED){
                                                    holder.eventStatusLabel.setText(userEventStatus.getEventStatusType().toString());
                                                    holder.eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_green_400));
                                                }else if(userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.DECLINED) {
                                                    holder.eventStatusLabel.setText(userEventStatus.getEventStatusType().toString());
                                                    holder.eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_red_400));
                                                }else {
                                                    holder.eventStatusLabel.setText(userEventStatus.getEventStatusType().toString());
                                                    holder.eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_orange_400));
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        Log.e("UserEventStatus", "Error populating userEventStatus data: " + e.toString());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                JSONObject jsonObject = null;
                                String errorString = null;
                                try {
                                    jsonObject = new JSONObject(new String(responseBody));
                                    errorString = (String) jsonObject.get("error");
                                } catch (JSONException e) {
                                    Log.e("UserEventStatus", "Failure getting userEventStatus data: " + e.toString());
                                }
                            }
                        });
            } catch (UnsupportedEncodingException e) {
                Log.e("UserEventStatus", "Error fetching userEventStatus data: " + e.toString());
            }
        }else{
            UserEventStatusContainer userEventStatusContainer = mBaseEvent.getUserEventStatusContainer();
            HashMap<UserEventStatus.EventStatusType, Integer> statusMap = userEventStatusContainer.getUserToStatusMap();
            if(holder.position == position) {
                holder.acceptedMembersCount.setText(String.valueOf(statusMap.get(UserEventStatus.EventStatusType.ACCEPTED)));
                //holder.declinedMembersCount.setText(String.valueOf(statusMap.get(UserEventStatus.EventStatusType.DECLINED)));
                UserEventStatus userEventStatus = userEventStatusContainer.getCurrentUserEventStatusObj();
                if (userEventStatus != null) {
                    if (userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.ACCEPTED) {
                        holder.eventStatusLabel.setText(userEventStatus.getEventStatusType().toString());
                        holder.eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_green_400));
                    } else if (userEventStatus.getEventStatusType() == UserEventStatus.EventStatusType.DECLINED) {
                        holder.eventStatusLabel.setText(userEventStatus.getEventStatusType().toString());
                        holder.eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_red_400));
                    } else {
                        holder.eventStatusLabel.setText(userEventStatus.getEventStatusType().toString());
                        holder.eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_orange_400));
                    }
                }
            }
        }
        //}

        return convertView;

    }

    public void showEventStatusConfirmationDialog(final Context context, final ViewGroup parent, final int position,  final UserEventStatusContainer userEventStatusContainer, final View convertView, String msg, final UserEventStatus.EventStatusType eventStatusType){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(R.string.dialog_yes_button_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final UserEventStatus currentUserEventStatusObj = userEventStatusContainer.getCurrentUserEventStatusObj();
                        final UserEventStatus.EventStatusType currentUserStatus = currentUserEventStatusObj.getEventStatusType();
                        if (currentUserStatus.equals(eventStatusType)){
                            return;
                        }
                        currentUserEventStatusObj.setEventStatusType(eventStatusType);
                        try {
                            EventAsyncHttpClient eventAsyncHttpClient = new EventAsyncHttpClient(context);
                            eventAsyncHttpClient.updateUserEventStatus(mBaseEvent.getParentEventIdStr(), mBaseEvent.getParentEventTypeStr(), currentUserEventStatusObj, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        HashMap<UserEventStatus.EventStatusType, Integer> statusMap = userEventStatusContainer.getUserToStatusMap();
                                        Button acceptedMembersCount = (Button) convertView.findViewById(R.id.accepted_members_count);
                                        //TextView declinedMembersCount = (TextView) convertView.findViewById(R.id.declined_members_count);
                                        acceptedMembersCount.setText(String.valueOf(statusMap.get(UserEventStatus.EventStatusType.ACCEPTED)));
                                        //declinedMembersCount.setText(String.valueOf(statusMap.get(UserEventStatus.EventStatusType.DECLINED)));

                                        TextView eventStatusLabel = (TextView) convertView.findViewById(R.id.event_status_label);

                                        if (eventStatusType == UserEventStatus.EventStatusType.ACCEPTED) {
                                            eventStatusLabel.setText(eventStatusType.toString());
                                            eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_green_400));
                                        } else if (eventStatusType == UserEventStatus.EventStatusType.DECLINED) {
                                            eventStatusLabel.setText(eventStatusType.toString());
                                            eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_red_400));
                                        } else {
                                            eventStatusLabel.setText(eventStatusType.toString());
                                            eventStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_orange_400));
                                        }
                                        ListView listView = (ListView) parent;

                                        // this should happen before changing the accepted and declined count data on ui
                                        for(int i=0; i<listView.getAdapter().getCount(); i++) {
                                            if(i != position) {
                                                BaseEvent baseEvent = (BaseEvent) listView.getAdapter().getItem(i);
                                                for (UserEventStatus userEventStatus : baseEvent.getUserEventStatusContainer().getUserEventStatusArray()) {
                                                    if (userEventStatus == baseEvent.getUserEventStatusContainer().getCurrentUserEventStatusObj()) {
                                                        userEventStatus.setEventStatusType(UserEventStatus.EventStatusType.DECLINED);
                                                    }
                                                }
                                            }
                                        }

                                        for(int i=0; i<listView.getChildCount(); i++){
                                            if(i != position) {
                                                TextView eventStatusForOtherEvents = (TextView) listView.getChildAt(i).findViewById(R.id.event_status_label);
                                                TextView acceptedMembersCountForOtherEvents = (TextView) listView.getChildAt(i).findViewById(R.id.accepted_members_count);
                                                //TextView declinedMembersCountForOtherEvents = (TextView) listView.getChildAt(i).findViewById(R.id.declined_members_count);
                                                if(eventStatusForOtherEvents.getText() != UserEventStatus.EventStatusType.DECLINED.toString()) {
                                                    eventStatusForOtherEvents.setText(UserEventStatus.EventStatusType.DECLINED.toString());
                                                    eventStatusForOtherEvents.setBackgroundColor(ContextCompat.getColor(context, R.color.md_red_400));
                                                }
                                                // make sure userEventStatus is updated before this code is executed for correct results
                                                BaseEvent baseEvent = (BaseEvent) listView.getAdapter().getItem(i);
                                                HashMap<UserEventStatus.EventStatusType, Integer> statusMapForOtherEvents = baseEvent.getUserEventStatusContainer().getUserToStatusMap();
                                                acceptedMembersCountForOtherEvents.setText(String.valueOf(statusMapForOtherEvents.get(UserEventStatus.EventStatusType.ACCEPTED)));
                                                //declinedMembersCountForOtherEvents.setText(String.valueOf(statusMapForOtherEvents.get(UserEventStatus.EventStatusType.DECLINED)));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                    JSONObject jsonObject = null;
                                    String errorString = null;
                                    try {
                                        jsonObject = new JSONObject(new String(errorResponse));
                                        errorString = (String) jsonObject.get("error");
                                    } catch (JSONException error) {
                                    }
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_no_button_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create().show();
    }
}
