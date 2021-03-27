package com.example.turbo.dcidr.main.activity_helper.selected_group_event_activity_helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by Turbo on 2/27/2016.
 */
public class SelectedGroupEventCustomArrayAdapter extends ArrayAdapter<BaseEvent> {

    public SelectedGroupEventCustomArrayAdapter(Context context, int resource, ArrayList<BaseEvent> eventList) {
        super(context, resource, eventList);
    }

    @Override
    public void clear() {
        super.clear();
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
//        Object event = getItem(position);
//        Object eventType;
//        int eventColor = R.color.peachColor;
//        try {
//            Method method = event.getClass().getMethod("getEventTypeObj", new Class<?>[]{});
//            eventType = (Object) method.invoke(event);
//            Method method1 = eventType.getClass().getMethod("getEventColor", new Class<?>[]{});
//            eventColor = (int) method1.invoke(eventType);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // get screen size
        final BaseActivity baseActivity = (BaseActivity) getContext();
        int screenWidth = baseActivity.getWindowScreenWidth();

        // parentEvent width is 1/2 and childEvent width is 1/4 of the screen size

        BaseEvent baseEvent = (BaseEvent) getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.activity_selected_group_event_custom_view, parent, false);
        }

        final LinearLayout parentLL = (LinearLayout) inflater.inflate(R.layout.selected_group_event_custom_view, null);
        parentLL.setMinimumWidth(screenWidth/2);
        TextView parentEventTypeColorStripe = (TextView) parentLL.findViewById(R.id.event_type_color_stripe);
        parentEventTypeColorStripe.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_red_400));
        LinearLayout eventsHolderLinearLayout = (LinearLayout) convertView.findViewById(R.id.selected_group_events_holder);

        // if click is performed on eventHolder, consider that as a click for listView index
        eventsHolderLinearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });

        eventsHolderLinearLayout.removeAllViews();

        TextView createdByUserName = (TextView) parentLL.findViewById(R.id.created_by_user_name);
        TextView eventName = (TextView) parentLL.findViewById(R.id.event_name);
        TextView eventTime = (TextView) parentLL.findViewById(R.id.event_time);
        createdByUserName.setText(baseEvent.getCreatedByName());
        eventName.setText(baseEvent.getEventName());
        eventTime.setText(String.valueOf(Utils.convertEpochToDateTime(baseEvent.getStartTime(),
                TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));

        eventsHolderLinearLayout.addView(parentLL);

        for(BaseEvent childEvent: baseEvent.getChildEventsContainer().getEventList()){
            LinearLayout childLL = (LinearLayout) inflater.inflate(R.layout.selected_group_event_custom_view, null);
            childLL.setMinimumWidth(screenWidth/4);
            TextView childEventTypeColorStripe = (TextView) parentLL.findViewById(R.id.event_type_color_stripe);
            childEventTypeColorStripe.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_deep_orange_300));

            TextView childEventName = (TextView) childLL.findViewById(R.id.event_name);

            childEventName.setText(childEvent.getEventName());

            TextView childEventTime = (TextView) childLL.findViewById(R.id.event_time);
            childEventTime.setText(String.valueOf(Utils.convertEpochToDateTime(childEvent.getStartTime(),
                    TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));

            TextView childEventCreatedByUserName = (TextView) childLL.findViewById(R.id.created_by_user_name);
            childEventCreatedByUserName.setText(childEvent.getCreatedByName());

            // add 5 spacing between events
            TextView marginTextView = new TextView(getContext());
            marginTextView.setPadding(10, 0, 0, 10);
            eventsHolderLinearLayout.addView(marginTextView);

            eventsHolderLinearLayout.addView(childLL);
        }


        return convertView;
    }


//            Method method = event.getClass().getMethod("getSelectedGroupEventCustomView", new Class<?>[]{Context.class, ViewGroup.class});
//            convertView = (View) method.invoke(event, getContext(), parent);
//            final BaseEvent baseEvent = (BaseEvent) event;
//
//            Log.i("Ketan", baseEvent.toString());
//
//            final LinearLayout eventsHolderLinearLayout = (LinearLayout) convertView.findViewById(R.id.selected_group_events_holder);
//
//            mEventAsyncHttpClient.getEvents(DcidrApplication.getInstance().getUser().getUserIdStr(), baseEvent.getGroupIdStr(), baseEvent.getEventIdStr(), 0, 1000, new AsyncHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                    if (statusCode == 200) {
//                        try {
//                            JSONArray jsonEventArray = new JSONObject(new String(responseBody)).getJSONArray("result");
//                            for (int i = 0; i < jsonEventArray.length(); i++) {
//                                LinearLayout linearLayout = new LinearLayout(getContext());
//                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                lp.addRule(RelativeLayout.RIGHT_OF, R.id.link_text_view);
//                                linearLayout.setLayoutParams(lp);
//                                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//                                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                llp.setMargins(0, 0, 5, 0); // llp.setMargins(left, top, right, bottom);
//
//                                TextView createdByUserName = new TextView(getContext());
//                                createdByUserName.setLayoutParams(llp);
//                                createdByUserName.setText("- " + jsonEventArray.getJSONObject(i).getString("createdByName"));
//
//                                TextView proposedText = new TextView(getContext());
//                                proposedText.setLayoutParams(llp);
//                                proposedText.setText("proposed");
//
//
//                                TextView eventName = new TextView(getContext());
//                                eventName.setLayoutParams(llp);
//                                eventName.setText(jsonEventArray.getJSONObject(i).getString("eventName"));
//
//
//                                TextView forText = new TextView(getContext());
//                                forText.setLayoutParams(llp);
//                                forText.setText("for");
//
//
//                                TextView eventTime = new TextView(getContext());
//                                eventTime.setLayoutParams(llp);
//                                eventTime.setText(String.valueOf(Utils.convertEpochToDateTime(jsonEventArray.getJSONObject(i).getLong("startTime"),
//                                        TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));
//
//                                linearLayout.addView(createdByUserName);
//                                linearLayout.addView(proposedText);
//                                linearLayout.addView(eventName);
//                                linearLayout.addView(forText);
//                                linearLayout.addView(eventTime);
//
//                                eventsHolderLinearLayout.addView(linearLayout);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                }
//            });



//            final RelativeLayout selectedGroupEventRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.selected_group_event_relative_layout);
//            selectedGroupEventRelativeLayout.setClickable(false);
//
////
//            TextView acceptedMembersCount = (TextView) convertView.findViewById(R.id.accepted_members_count);
//            TextView declinedMembersCount = (TextView) convertView.findViewById(R.id.declined_members_count);
//            TextView tentativeMembersCount = (TextView) convertView.findViewById(R.id.tentative_members_count);
//
////            final TextView currentStatusLabel = (TextView) convertView.findViewById(R.id.current_status_label);
//
//            BaseEvent baseEvent = (BaseEvent) event;
//            final UserEventContainer userEventContainer = baseEvent.getUserEventContainer();
//
//            HashMap<UserEvent.EventStatusType, Integer> statusMap = userEventContainer.getUserToStatusMap();
//            acceptedMembersCount.setText(String.valueOf(statusMap.get(UserEvent.EventStatusType.ACCEPTED)));
//            declinedMembersCount.setText(String.valueOf(statusMap.get(UserEvent.EventStatusType.DECLINED)));
//            tentativeMembersCount.setText(String.valueOf(statusMap.get(UserEvent.EventStatusType.TENTATIVE)));

//            // set label text and color
//            currentStatusLabel.setText(userEventContainer.getCurrentUserEventObj().getEventStatusType().toString());
//            int statusColor;
//            if(userEventContainer.getCurrentUserEventObj().getEventStatusType() == UserEvent.EventStatusType.ACCEPTED){
//                statusColor = ContextCompat.getColor(parent.getContext(), R.color.md_green_400);
//            }else if(userEventContainer.getCurrentUserEventObj().getEventStatusType() == UserEvent.EventStatusType.DECLINED){
//                statusColor = ContextCompat.getColor(parent.getContext(), R.color.md_red_400);
//            }else if(userEventContainer.getCurrentUserEventObj().getEventStatusType() == UserEvent.EventStatusType.TENTATIVE){
//                statusColor = ContextCompat.getColor(parent.getContext(), R.color.md_blue_400);
//            }else{
//                statusColor = ContextCompat.getColor(parent.getContext(), R.color.md_orange_400);
//            }
//            currentStatusLabel.setBackgroundColor(statusColor);
//
//            if(baseEvent.getHasExpired()){
//                dragLeftImageView.setVisibility(View.GONE);
//            }
//
//            selectedGroupEventRelativeLayout.setOnClickListener(new RelativeLayout.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (actionLinearLayout.getVisibility() == View.VISIBLE) {
//                        actionLinearLayout.setVisibility(View.GONE);
//                        dragLeftImageView.setVisibility(View.VISIBLE);
//                    }
//                    selectedGroupEventRelativeLayout.setClickable(false);
//                }
//            });
//
//
//
//            dragLeftImageView.setOnClickListener(new ImageView.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    selectedGroupEventRelativeLayout.setClickable(true);
//                    if(actionLinearLayout.getVisibility() == View.GONE) {
//                        actionLinearLayout.setVisibility(View.VISIBLE);
//                        dragLeftImageView.setVisibility(View.GONE);
//                    }else {
//                        actionLinearLayout.setVisibility(View.GONE);
//                    }
//                }
//            });

//            dragLeftImageView.setOnTouchListener(new OnSwipeTouchListener(parent.getContext()){
//                public void onSwipeLeft() {
//                    selectedGroupEventRelativeLayout.setClickable(true);
//                    if(actionLinearLayout.getVisibility() == View.GONE) {
//                        actionLinearLayout.setVisibility(View.VISIBLE);
//                        dragLeftImageView.setVisibility(View.GONE);
//                    }else {
//                        actionLinearLayout.setVisibility(View.GONE);
//                    }
//                }
//            });
//            dragRightImageView.setOnTouchListener(new OnSwipeTouchListener(parent.getContext()){
//                public void onSwipeRight() {
//                    selectedGroupEventRelativeLayout.setClickable(false);
//                    dragLeftImageView.setVisibility(View.VISIBLE);
//                    actionLinearLayout.setVisibility(View.GONE);
//                }
//            });
//
//            final View finalConvertView = convertView;
//            acceptButton.setOnClickListener(new Button.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    selectedGroupEventRelativeLayout.setClickable(false);
//                    dragLeftImageView.setVisibility(View.VISIBLE);
//                    actionLinearLayout.setVisibility(View.GONE);
//                    showEventStatusConfirmationDialog(parent.getContext(), userEventContainer, finalConvertView, parent.getContext().getResources().getString(R.string.accept_activity_dialog_msg), UserEvent.EventStatusType.ACCEPTED);
//
//                }
//            });
//            declineButton.setOnClickListener(new Button.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    selectedGroupEventRelativeLayout.setClickable(false);
//                    dragLeftImageView.setVisibility(View.VISIBLE);
//                    actionLinearLayout.setVisibility(View.GONE);
//                    showEventStatusConfirmationDialog(parent.getContext(), userEventContainer, finalConvertView, parent.getContext().getString(R.string.decline_activity_dialog_msg), UserEvent.EventStatusType.DECLINED);
//                }
//            });
//            tentativeButton.setOnClickListener(new Button.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    selectedGroupEventRelativeLayout.setClickable(false);
//                    dragLeftImageView.setVisibility(View.VISIBLE);
//                    actionLinearLayout.setVisibility(View.GONE);
//                    showEventStatusConfirmationDialog(parent.getContext(), userEventContainer, finalConvertView, parent.getContext().getString(R.string.tentative_activity_dialog_msg), UserEvent.EventStatusType.TENTATIVE);
//                }
//            });

//    public void showEventStatusConfirmationDialog(final Context context, final UserEventContainer userEventContainer, final View view, String msg, final UserEvent.EventStatusType eventStatusType){
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage(msg)
//                .setPositiveButton(R.string.dialog_yes_button_msg, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        final UserEvent currentUserEventObj = userEventContainer.getCurrentUserEventObj();
//                        final UserEvent.EventStatusType currentUserStatus = currentUserEventObj.getEventStatusType();
//                        if (currentUserStatus.equals(eventStatusType)){
//                            return;
//                        }
//                        currentUserEventObj.setEventStatusType(eventStatusType);
//                        try {
//                            mEventAsyncHttpClient.updateUserEvent(currentUserEventObj, new AsyncHttpResponseHandler() {
//                                @Override
//                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                                    if (statusCode == 200) {
//                                        HashMap<UserEvent.EventStatusType, Integer> statusMap = userEventContainer.getUserToStatusMap();
//                                        TextView acceptedMembersCount = (TextView) view.findViewById(R.id.accepted_members_count);
//                                        TextView declinedMembersCount = (TextView) view.findViewById(R.id.declined_members_count);
//                                        TextView tentativeMembersCount = (TextView) view.findViewById(R.id.tentative_members_count);
//                                        acceptedMembersCount.setText(String.valueOf(statusMap.get(UserEvent.EventStatusType.ACCEPTED)));
//                                        declinedMembersCount.setText(String.valueOf(statusMap.get(UserEvent.EventStatusType.DECLINED)));
//                                        tentativeMembersCount.setText(String.valueOf(statusMap.get(UserEvent.EventStatusType.TENTATIVE)));
//
//                                        final TextView currentStatusLabel = (TextView) view.findViewById(R.id.current_status_label);
//
//                                        if(eventStatusType == UserEvent.EventStatusType.ACCEPTED){
//                                            currentStatusLabel.setText("ACCEPTED");
//                                            currentStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_green_400));
//
//                                        }else if(eventStatusType == UserEvent.EventStatusType.DECLINED) {
//                                            currentStatusLabel.setText("DECLINED");
//                                            currentStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_red_400));
//                                        }else if(eventStatusType == UserEvent.EventStatusType.TENTATIVE){
//                                            currentStatusLabel.setText("TENTATIVE");
//                                            currentStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_blue_400));
//                                        }else {
//                                            currentStatusLabel.setText("PENDING");
//                                            currentStatusLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.md_orange_400));
//                                        }
//
//                                        return;
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                                    JSONObject jsonObject = null;
//                                    String errorString = null;
//                                    try {
//                                        jsonObject = new JSONObject(new String(errorResponse));
//                                        errorString = (String) jsonObject.get("error");
//                                    } catch (JSONException error) {
//                                    }
//                                }
//                            });
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                })
//                .setNegativeButton(R.string.dialog_no_button_msg, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                })
//                .create().show();
//    }
}
