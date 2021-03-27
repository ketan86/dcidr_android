package com.example.turbo.dcidr.main.event;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.container.ChweetContainer;
import com.example.turbo.dcidr.main.container.EventContainer;
import com.example.turbo.dcidr.main.container.UserEventStatusContainer;
import com.example.turbo.dcidr.main.event_type.BaseEventType;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Points;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;


/**
 * Created by Turbo on 2/13/2016.
 */
public class BaseEvent implements Serializable, Comparable {

//    public enum EventType {
//        HIKE, FOOD, SPORT, UNKNOWN
//    }

    public enum EventType {
        UNKNOWN(-1),
        HIKE(15),
        FOOD(16),
        SPORT(17);

        private int value;

        EventType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static EventType getType(int value){
            for(EventType eventType: values()){
                if(eventType.value == value){
                    return eventType;
                }
            }
            return null;
        }
    }

    public enum EventSortKey {
        EVENT_LAST_MODIFIED_TIME, EVENT_DECIDED_TIME, EVENT_CREATION_TIME
    }

    public long UNDEFINED = -1;
    private BaseEventType mBaseEventType;
    private long mEventId;
    private long mStartTime;
    private long mEndTime;
    protected Points mLocationCoordinates;
    protected int mHistoryCustomLayout;
    protected int mSelectedGroupEventCustomLayout;
    private EventType mEventType;
    private String mEventName;
    private long mEventCreationTime;
    private long mEventLastModifiedTime;
    private long mDecidedTime;
    private boolean mDecided;
    private boolean mFinished;
    private long mDecideByTime;
    private String mLocationName;
    private boolean mHasExpired;
    private EventSortKey mEventSortKey;
    private User[] mUsers;
    protected BaseGroup mBaseGroup;
    private UserEventStatusContainer mUserEventStatusContainer;
    private Context mContext;
    private long mParentEventId;
    private EventType mParentEventType;
    protected String mCreatedByName;
    private EventContainer mChildEventsContainer;
    private ChweetContainer mChweetContainer;
    private String mEventPicUrl;
    private String mEventPicBase64;
    private EventPicFetchInterface mEventPicFetchInterface;
    private Bitmap mEventPicBitmap;
    private Bitmap mLocationMapBitmap;
    private String mEventNotes;
    private EventAttributeMask mEventAttributeMask;
    private double mDistanceFromCurrentLoc;

    public interface EventPicFetchInterface {
        void onImageFetchDone(Bitmap bitmap);
        int getImageWidth();
        int getImageHeight();
    }

    public void onEventPicFetchDoneListener(EventPicFetchInterface eventPicFetchInterface){
        this.mEventPicFetchInterface = eventPicFetchInterface;
    }
    
    public BaseEvent(Context context) {
        this.mContext = context;
        this.mEventId = UNDEFINED;
        this.mBaseEventType = new BaseEventType();
        this.mUserEventStatusContainer = new UserEventStatusContainer(mContext);
        this.mHasExpired = false;
        this.mEventCreationTime = 0;
        this.mEventLastModifiedTime = 0;
        this.mDecidedTime = 0;
        this.mDecided = false;
        this.mFinished = false;
        this.mDistanceFromCurrentLoc = -1;
        this.mHistoryCustomLayout = R.layout.fragment_history_custom_view;
        this.mSelectedGroupEventCustomLayout = R.layout.activity_selected_group_event_custom_view;

        this.mParentEventId = -1;
        this.mParentEventType = EventType.UNKNOWN;
        this.mChildEventsContainer = new EventContainer(context);
        this.mChweetContainer = new ChweetContainer(context);
        this.mEventAttributeMask = new EventAttributeMask();
    }

    public static enum EventAttribute {
        ALLOW_EDITABLE_DIFFERENT_EVENT_TYPES(0),
        ALLOW_EVENT_PROPOSAL(1),
        ALLOW_EDITABLE_EVENT_LOCATION(2),
        ALLOW_EDITABLE_EVENT_TIME(3);
        private int value;

        private EventAttribute(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public class EventAttributeMask {
        private long mEventMask;
        public void setEventAttribute(EventAttribute eventAttribute) {
            long maskVal = 1L;
            maskVal = maskVal << eventAttribute.getValue();
            mEventMask |= maskVal;
        }
        public boolean isEventAttributeEditable(EventAttribute eventAttribute) {
            long maskVal = 1L;
            maskVal = maskVal << eventAttribute.getValue();
            if ((mEventMask & maskVal) > 0 ) {
                return true;
            }
            return false;
        }
        public long getMask() {
            return mEventMask;
        }
        public void setMask(long mask){
            this.mEventMask = mask;
        }
    }

    public double getDistanceFromCurrentLoc(){
        return this.mDistanceFromCurrentLoc;
    }

    public String getDistanceFromCurrentLocInMilesStr(){
        DecimalFormat f = new DecimalFormat("##.00");
        return String.valueOf(f.format(this.mDistanceFromCurrentLoc * 0.000621371)) + " miles away";
    }
    public void setDistanceFromCurrentLoc(double distanceFromCurrentLoc){
        this.mDistanceFromCurrentLoc = distanceFromCurrentLoc;
    }
    public EventAttributeMask getEventAttributeMask(){
        return mEventAttributeMask;
    }
    

    public void setLocationMapBitmap(Bitmap bitmap){
        this.mLocationMapBitmap = bitmap;
    }
    public Bitmap getLocationMapBitmap(){
        return this.mLocationMapBitmap;
    }

    public EventContainer getChildEventsContainer() {
        return this.mChildEventsContainer;
    }

    public void setChildEventsContainer(EventContainer childEventsContainer){
        this.mChildEventsContainer = childEventsContainer;
    }
    public ChweetContainer getChweetContainer() {
        return this.mChweetContainer;
    }

    public void setChweetContainer(ChweetContainer chweetContainer){
        this.mChweetContainer = chweetContainer;
    }
    public void setBaseGroup(BaseGroup baseGroup) {
        mBaseGroup = baseGroup; //if the baseEvent has a link with a baseGroup call this function
    }

    public void setParentEventId(long parentEventId) {
        this.mParentEventId = parentEventId;
    }

    public void setParentEventType(EventType parentEventTypeId) {
        this.mParentEventType = parentEventTypeId;
    }


    public void setEventNotes(String notes){
        this.mEventNotes = notes;
    }

    public String getEventNotes(){
        return this.mEventNotes;
    }
    public long getParentEventId() {
        return this.mParentEventId;
    }

    public EventType getParentEventType() {
        return this.mParentEventType;
    }

    public String getParentEventIdStr() {
        return String.valueOf(this.mParentEventId);
    }

    public String getParentEventTypeStr() {
        return String.valueOf(this.mParentEventType);
    }

    public void setCreatedByName(String userName) {
        this.mCreatedByName = userName;
    }

    public String getCreatedByName() {
        return this.mCreatedByName;
    }

    public BaseGroup getBaseGroup() {
        return mBaseGroup;
    }

    public UserEventStatusContainer getUserEventStatusContainer() {
        return mUserEventStatusContainer;
    }

    public long getGroupId() {
        return mBaseGroup.getGroupId();
    }

    public String getGroupIdStr() {
        return String.valueOf(mBaseGroup.getGroupId());
    }

    public BaseEventType getEventTypeObj() {
        return mBaseEventType;
    }

    public String getLocationCoordinatesString() {
        return this.mLocationCoordinates.toString();
    }

    public Points getLocationCoordinatesPoints() {
        return this.mLocationCoordinates;
    }

    public void setLocationName(String locationName) {
        this.mLocationName = locationName;
    }

    public void setLocationCoordinates(double latitude, double longitude) {
        this.mLocationCoordinates = new Points(latitude, longitude);
    }

    public void setHasExpired(boolean expired) {
        this.mHasExpired = expired;
    }

    public void setEventId(long id) {
        this.mEventId = id;
    }

    public int getHistoryCustomLayout() {
        return mHistoryCustomLayout;
    }

    public int getSelectedGroupEventCustomLayout() {
        return mSelectedGroupEventCustomLayout;
    }

    public boolean getHasExpired() {
        return mHasExpired;
    }

    public String getEventName() {
        return this.mEventName;
    }

    public String getEventTypeStr() {
        return this.mEventType.toString();
    }
    public EventType getEventType(){
        return this.mEventType;
    }

    public void setStartTime(long time) {
        this.mStartTime = time;
    }

    public void setEventName(String eventName) {
        this.mEventName = eventName;
    }

    public void setEndTime(long time) {
        this.mEndTime = time;
    }

    public void setEventType(EventType type) {
        this.mEventType = type;
    }

    public void setDecideByTime(long time) {
        this.mDecideByTime = time;
    }

    public void setEventCreationTime(long time) {
        this.mEventCreationTime = time;
    }

    public void setEventLastModifiedTime(long time) {
        this.mEventLastModifiedTime = time;
    }

    public void setDecidedTime(long time) {
        this.mDecidedTime = time;
    }

    public void setDecided(boolean decidedFlag) {
        this.mDecided = decidedFlag;
    }

    public String[] getMemberNames() {
        return mBaseGroup.getMembers();
    }

    public String[] getDateTime(long epoch) {
        // epoch is in seconds, Date requires milliseconds
        Date date = new Date(epoch * 1000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        format.setTimeZone(TimeZone.getTimeZone("PST"));
        String[] dateTimeArray = new String[2];
        dateTimeArray[0] = format.format(date).split(" ")[0];
        dateTimeArray[1] = format.format(date).split(" ")[1];
        return dateTimeArray;
    }

    public long getEventId() {
        return mEventId;
    }

    public String getEventIdStr() {
        return String.valueOf(mEventId);
    }

    public long getStartTime() {
        return this.mStartTime;
    }

    public boolean getDecided() {
        return this.mDecided;
    }

    public long getEndTime() {
        return this.mEndTime;
    }

    public EventType getType() {
        return this.mEventType;
    }

    public long getDecideByTime() {
        return this.mDecideByTime;
    }

    public String getLocationName() {
        return this.mLocationName;
    }

    public long getEventCreationTime() {
        return this.mEventCreationTime;
    }

    public long getDecidedTime() {
        return this.mDecidedTime;
    }

    public long getEventLastModifiedTime() {
        return this.mEventLastModifiedTime;
    }

    public long setDecidedTime() {
        return this.mDecidedTime;
    }

    public void setFinished(boolean finished) {
        this.mFinished = finished;
    }

    public boolean getFinished() {
        return this.mFinished;
    }

    public boolean checkIfExpired(){
        if(getDecideByTime() - System.currentTimeMillis() <= 0) {
            return true;
        }
        return false;
    }

    public void setViewWithTimeLeft(Context context, long futureEpochTimeInMilliSecs, TextView textView) {
        String resultDate = Utils.convertDateToMeaningfulText(futureEpochTimeInMilliSecs, true);
        if (resultDate.equals("EXPIRED")) {
            textView.setText("EXPIRED");
            this.setHasExpired(true);
        }
        if (!resultDate.contains("Days left")) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.md_red_400));
        }
        textView.setText(resultDate);
    }


    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putLong("eventId", this.getEventId());
        return bundle;
    }


    public HashMap<String, String> getEventDataAsMap() {
        HashMap<String, String> eventMap = new HashMap<String, String>();
        if (this.getEventId() != UNDEFINED) {
            eventMap.put("eventId", this.getEventIdStr());
        }
        eventMap.put("parentEventId", this.getParentEventIdStr());
        eventMap.put("parentEventType", this.getParentEventTypeStr());
        eventMap.put("createdByUserId", DcidrApplication.getInstance().getUser().getUserIdStr());
        eventMap.put("eventType", this.getEventTypeStr());
        eventMap.put("eventLastModifiedTime", String.valueOf(this.getEventLastModifiedTime()));
        eventMap.put("eventName", this.getEventName());
        eventMap.put("eventNotes", this.getEventNotes());
        eventMap.put("locationName", this.getLocationName());
        eventMap.put("locationCoordinates", this.getLocationCoordinatesString());
        eventMap.put("startTime", String.valueOf(this.getStartTime()));
        eventMap.put("endTime", String.valueOf(this.getEndTime()));
        eventMap.put("decideByTime", String.valueOf(this.getDecideByTime()));
        eventMap.put("eventPicBase64Str", String.valueOf(this.getEventPicBase64Str()));
        eventMap.put("eventCreationTime", String.valueOf(this.getEventCreationTime()));
        eventMap.put("eventAttributeMask", String.valueOf(mEventAttributeMask.getMask()));

        return eventMap;
    }

    public void populateMe(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("eventType")) {
            this.setEventType(BaseEvent.EventType.valueOf(jsonObject.getString("eventType")));
        }
        if (jsonObject.has("eventTypeId")) {
            this.setEventType(BaseEvent.EventType.getType(jsonObject.getInt("eventTypeId")));
        }
        if (jsonObject.has("eventName")) {
            this.setEventName(jsonObject.getString("eventName"));
        }
        if (jsonObject.has("eventNotes")) {
            this.setEventNotes(jsonObject.getString("eventNotes"));
        }
        if (jsonObject.has("eventId")) {
            this.setEventId(jsonObject.getInt("eventId"));
        }
        if (jsonObject.has("locationName")) {
            this.setLocationName(jsonObject.getString("locationName"));
        }
        if (jsonObject.has("locationCoordinates")) {
            try {
                JSONObject ob = jsonObject.getJSONObject("locationCoordinates");
                this.setLocationCoordinates(ob.getDouble("x"), ob.getDouble("y"));
            } catch (JSONException e) {
                double[] points = Points.stringToDoubleArray(jsonObject.getString("locationCoordinates"));
                this.setLocationCoordinates(points[0], points[1]);
            }
        }
        if (jsonObject.has("startTime")) {
            this.setStartTime(jsonObject.getLong("startTime"));
        }
        if (jsonObject.has("endTime")) {
            this.setEndTime(jsonObject.getLong(("endTime")));
        }
        if (jsonObject.has("decideByTime")) {
            this.setDecideByTime(jsonObject.getLong("decideByTime"));
        }
        if (jsonObject.has("eventCreationTime")) {
            this.setEventCreationTime(jsonObject.getLong("eventCreationTime"));
        }
        if (jsonObject.has("eventLastModifiedTime")) {
            this.setEventLastModifiedTime(jsonObject.getLong("eventLastModifiedTime"));
        }
        if (jsonObject.has("decided")) {
            this.setDecided(jsonObject.getBoolean("decided"));
            if (this.getDecided()) {
                if (jsonObject.has("decidedTime")) {
                    this.setDecidedTime(jsonObject.getLong("decidedTime"));
                }
            }
        }
        if (jsonObject.has("finished")) {
            this.setFinished(jsonObject.getBoolean("finished"));
        }
        if(jsonObject.has("eventAttributeMask")){
            HikeEvent.EventAttributeMask eventAttributeMask = getEventAttributeMask();
            eventAttributeMask.setMask(jsonObject.getLong("eventAttributeMask"));
        }
//        if (jsonObject.has("userEventStatus")) {
//            this.mUserEventStatusContainer.setGroupId(mBaseGroup.getGroupId());
//            this.mUserEventStatusContainer.populateMe(jsonObject.getJSONArray("userEventStatus"));
//        }

        if (jsonObject.has("childEventsData")) {
            this.mChildEventsContainer.setBaseGroup(mBaseGroup);
            this.mChildEventsContainer.populateEvent(jsonObject.getJSONArray("childEventsData"));
        }

        if (jsonObject.has("createdByName")) {
            this.mCreatedByName = jsonObject.getString("createdByName");
        }

        if (jsonObject.has("eventPicUrl")) {
            //this.setEventPicUrl(jsonObject.getString("eventPicUrl"));
            if(!jsonObject.isNull("eventPicUrl")) {
                this.setEventPicUrl(jsonObject.getString("eventPicUrl"));
            }
        }
    }

    public class EventPicAsyncLoad extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                GroupAsyncHttpClient groupAsyncHttpClient = new GroupAsyncHttpClient(mContext);
                String mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
                groupAsyncHttpClient.getGroupMediaByUrl(mUserIdStr, getEventPicUrl(), new AsyncHttpResponseHandler(Looper.getMainLooper()) {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                String result = new JSONObject(new String(responseBody)).getString("result");
                                setEventBase64PicStr(result);
                                if(mEventPicFetchInterface != null) {
                                    Bitmap imageBitMap = Utils.decodeBase64(result, mEventPicFetchInterface.getImageWidth(), mEventPicFetchInterface.getImageHeight());
                                    setEventPicBitmap(imageBitMap);
                                    mEventPicFetchInterface.onImageFetchDone(imageBitMap);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "Executed";
        }
    }

    public void setEventPicBitmap(Bitmap bitmap){
        this.mEventPicBitmap = bitmap;
    }

    public Bitmap getEventPicBitmap(){
        return this.mEventPicBitmap;
    }
    public void setEventBase64PicStr(String eventBase64Pic) {
        this.mEventPicBase64 = eventBase64Pic;
    }

    public String getEventPicBase64Str() {
        return mEventPicBase64;
    }

    public void setEventPicUrl(String eventPicUrl) {
        this.mEventPicUrl = eventPicUrl;
    }

    public String getEventPicUrl() {
        return this.mEventPicUrl;
    }

    public void setEventSortKey(EventSortKey key) {
        mEventSortKey = key;
    }

    @Override
    public int compareTo(Object another) {
        BaseEvent anotherBaseEvent = (BaseEvent) another;
        int retVal = -1;
        if (mEventSortKey == EventSortKey.EVENT_DECIDED_TIME) {
            if (this.getDecidedTime() == anotherBaseEvent.getDecidedTime()) {
                //check based on alphabetical ordering
                retVal = this.getEventName().compareTo(anotherBaseEvent.getEventName()) == 0 ? 0 :
                        this.getEventName().compareTo(anotherBaseEvent.getEventName()) > 0 ? 1 : -1;
            } else {
                retVal = this.getDecidedTime() > anotherBaseEvent.getDecidedTime() ? -1 : 1;
            }
        } else {// (mEventSortKey == SortKey.LAST_MODIFIED_TIME) {
            retVal = this.getEventLastModifiedTime() > anotherBaseEvent.getEventLastModifiedTime() ? -1 :
                    this.getEventLastModifiedTime() < anotherBaseEvent.getEventLastModifiedTime() ? 1 : 0;
        }
        return retVal;
    }

    public View getHistoryView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(getHistoryCustomLayout(), parent, false);
        TextView historyEventName = (TextView) convertView.findViewById(R.id.history_event_name);
        TextView historyEventTypeIcon = (TextView) convertView.findViewById(R.id.history_event_type_icon);
        TextView historyEventPlace = (TextView) convertView.findViewById(R.id.history_event_place);
        TextView historyEventStartDate = (TextView) convertView.findViewById(R.id.history_event_start_date);
        TextView historyEventStartTime = (TextView) convertView.findViewById(R.id.history_event_start_time);
        TextView historyEventEndDate = (TextView) convertView.findViewById(R.id.history_event_end_date);
        TextView historyEventEndTime = (TextView) convertView.findViewById(R.id.history_event_end_time);
        //
        //TextView historyEventPlaceDistance = (TextView) convertView.findViewById(R.id.history_event_place_distance);
        //Button historyEventMembers = (Button) convertView.findViewById(R.id.history_event_members);
        TextView historyEventColor = (TextView) convertView.findViewById(R.id.event_color);
        historyEventName.setText(Utils.capitalizeFirstLetter(this.getEventName().toString()));
        historyEventPlace.setText(this.getLocationName());
        //historyEventPlaceDistance.setText(String.valueOf(this.getLocationCoordinatesString()) + " Lat/Long");
        historyEventColor.setBackgroundColor(this.mBaseEventType.getEventColor());
        historyEventTypeIcon.setBackgroundResource(this.mBaseEventType.getEventTypeDrawableIcon());
        String[] startDateTimeArray = this.getDateTime(this.getStartTime());
        historyEventStartDate.setText(startDateTimeArray[0]);
        historyEventStartTime.setText(startDateTimeArray[1]);
        historyEventEndDate.setText(startDateTimeArray[0]);
        historyEventEndTime.setText(startDateTimeArray[1]);
        //historyEventMembers.setText(String.valueOf(this.getMemberCount()));
        return convertView;
    }

    public View getSelectedGroupEventCustomView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(getSelectedGroupEventCustomLayout(), parent, false);
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
//        selectedGroupEventColor.setBackgroundColor(Color.parseColor(this.mBaseEventType.getEventColor()));
//        selectedGroupEventTypeIcon.setBackgroundResource(this.mBaseEventType.getEventTypeDrawableIcon());
//        String[] startDateTimeArray = this.getDateTime(this.getStartTime());
//        selectedGroupEventStartDate.setText(startDateTimeArray[0]);
//        selectedGroupEventStartTime.setText(startDateTimeArray[1]);
//        selectedGroupEventEndDate.setText(startDateTimeArray[0]);
//        selectedGroupEventEndTime.setText(startDateTimeArray[1]);
//        setViewWithTimeLeft(context, this.getDecideByTime() * 1000, selectedGroupDecisionTimer);
//        if(getHasExpired()){
//            convertView.setEnabled(false);
//            convertView.setBackgroundResource(R.color.md_gray_400);
//        }
        return convertView;
    }

    public static class EventMiniMapFragment extends MapFragment {
        private LatLng mPosition;
        private GoogleMap mGoogleMap;

        public EventMiniMapFragment() {
            super();
        }

        public static EventMiniMapFragment newInstance(LatLng position) {
            EventMiniMapFragment frag = new EventMiniMapFragment();
            frag.mPosition = position;
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.google_map_fragment, container, false);
            initMap();
            return rootView;
        }

        private void initMap() {
            getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(mPosition));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPosition, 14.0f));
                    mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            Log.i("Ketan:Sanp", "Got it..");
                        }
                    });
                }
            });
        }

        public void takeSnapShot(final ImageView imageView) {
            if (mGoogleMap == null) {
                getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mGoogleMap = googleMap;
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(mPosition));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPosition, 14.0f));
                        mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
            } else {
                mGoogleMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    protected void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    public static class DownloadImageFromUrlTask extends AsyncTask<String, Void, Bitmap> {
        private ImageFetchInterface mImageFetchInterface;
        public interface ImageFetchInterface {
            void onImageFetchDone(Bitmap bitmap);
        }

        public void onImageFetchDone(ImageFetchInterface imageFetchInterface){
            this.mImageFetchInterface = imageFetchInterface;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            mImageFetchInterface.onImageFetchDone(result);
        }

        private Bitmap downloadImage(String url) {
            //---------------------------------------------------
            Bitmap bm = null;
            try {
                //Log.i("Ketan:Url", "Making static map api call");
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
            //---------------------------------------------------
        }
    }

    public void releaseMemory(){
        if(mEventPicFetchInterface != null) {
            mEventPicFetchInterface.onImageFetchDone(null);
            mEventPicFetchInterface = null;
        }
        setEventPicBitmap(null);
        setLocationMapBitmap(null);
        for(BaseEvent childEvent: getChildEventsContainer().getEventList()){
            childEvent.releaseMemory();
        }

    }
}

