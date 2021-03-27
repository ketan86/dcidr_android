package com.example.turbo.dcidr.main.activity_helper.history_fragment_helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.global.DcidrConstant;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.container.UserContainer;
import com.example.turbo.dcidr.main.event.BaseEvent;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/16/2016.
 */
public class HistoryCustomArrayAdapter extends ArrayAdapter<BaseEvent> {
    private String mEventTypeStr;
    private Object mEventAfterClick;
    enum ClickType {
        MEMBER_COUNT, EVENT_DETAIL
    };
    private ClickType mClickType;

    public HistoryCustomArrayAdapter(Context context, int resource, ArrayList<BaseEvent> eventArrayList) {
        super(context, resource, eventArrayList);
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        //based on which object baseArrayAdapter is pointing to, call that object's getHistoryView
        final BaseEvent event = getItem(position);
        try {
            // getMethod will also look at base class for getHistoryView where as getDeclaredMethod will only look at child class
            if (convertView == null) {
                String eventViewHelper = DcidrConstant.EVENT_VIEW_HELPER_PACKAGE_PATH +  Utils.capitalizeFirstLetter(event.getEventTypeStr());
                Class cls = Class.forName(eventViewHelper + "EventViewHelper");
                Constructor cons = cls.getConstructor(event.getClass());
                Object eventViewHelperObj = cons.newInstance(event);
                //Method method =eventViewHelperObj.getClass().getMethod("getHistoryView", new Class<?>[]{Context.class, ViewGroup.class});
                Method method = eventViewHelperObj.getClass().getMethod("getHistoryView", new Class<?>[]{int.class, Context.class, View.class, ViewGroup.class});
                convertView = (View) method.invoke(eventViewHelperObj, position, getContext(), convertView, parent);
            }
            //add clicklistener on the entire view
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEventAfterClick = event;
                    mClickType = ClickType.EVENT_DETAIL;
                    fetchGroupMembersOnClick(event);
                }
            });

            //add clicklisteners on the history_event_members
            TextView textView = (TextView) convertView.findViewById(R.id.history_event_members);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEventAfterClick = event;
                    mClickType = ClickType.MEMBER_COUNT;
                    fetchGroupMembersOnClick(event);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void fetchGroupMembersOnClick( Object event) {
        Method method = null;
        try {
            method = event.getClass().getMethod("getGroupId");
            Long groupId = (Long) method.invoke(event);
            method = event.getClass().getMethod("getEventTypeStr");
            mEventTypeStr = (String) method.invoke(event);
            method = event.getClass().getMethod("getEventId");
            Long eventId = (Long) method.invoke(event);
            String userIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
            //use the groupId to find all the members for that group and member information
            GroupAsyncHttpClient mGroupMembersAsyncHttpClient = new GroupAsyncHttpClient(getContext());
            MyAsyncHttpResponseHandler getGroupMembersAsyncHttpResponseHandler = new MyAsyncHttpResponseHandler(userIdStr, groupId, eventId);
            mGroupMembersAsyncHttpClient.getGroupMembers(userIdStr, String.valueOf(groupId), getGroupMembersAsyncHttpResponseHandler);
        } catch (NoSuchMethodException e) {
                e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public TextView getCustomDialogTitleTextView(String title, float textSize){
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        textView.setTextSize(textSize);
        textView.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
        textView.setPadding(10, 50, 10, 50);
        //textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        return textView;
    }

    /**
     * method to show popup with member names. it also launches selected event by passing member
     * names to it,
     * @param userContainer userContainer
     * @param eventTypeStr eventName
     *
     */
    public void onMemberClicked(final UserContainer userContainer, final String eventTypeStr, final long groupId) {
        // initialize AlertDialog.Builder for popup window with members names selection.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final String groupIdStr = String.valueOf(groupId);
        builder.setCustomTitle(getCustomDialogTitleTextView(getContext().getResources().getString(R.string.start_activity_with_popup_title_msg), 20));
        // setMultiChoiceItems needs initial state of each item being passes, setting to false
        String [] memberNames = new String[userContainer.getUserMap().size()];
        int i = 0;
        for (User user :userContainer.getUserList() ) {
            String firstName =user.getFirstName();
            String lastName = user.getLastName();
            memberNames[i++] = firstName + " " + lastName;
        }

        boolean[] isSelected = new boolean[userContainer.getUserMap().size()];
        Arrays.fill(isSelected, false);
        builder.setMultiChoiceItems(memberNames, isSelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // if member name checkbox is selected, add it in tempMemberNames list else remove.
                        if(isChecked){
                            userContainer.getUserList().get(which).setIsSelected(true);
                        }else{
                            userContainer.getUserList().get(which).setIsSelected(false);
                        }
                    }
                });
        builder.setCancelable(false)
                .setPositiveButton(R.string.start_activity_with_popup_positive_button_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //check if at least 1 user was selected
                        int selectedUserCount = 0;
                        for (User user: userContainer.getUserList()) {
                            if (user.getIsSelected()) {
                                selectedUserCount ++;
                            }
                        }
                        if (selectedUserCount == 0) {
                            return;
                        }
                        // construct eventActivityClassName from eventName and launch by passing tempMemberNames.
                        //String eventActivityClassName = DcidrResource.ANDROID_ACTIVITY_PACKAGE_PATH + "New" + Utils.capitalizeFirstLetter(eventName) + "EventActivity";
                        String eventActivityClassName = DcidrConstant.ANDROID_ACTIVITY_PACKAGE_PATH + "CreateGroupActivity";
                        Intent newEventIntent = null;
                        try {
                            newEventIntent = new Intent(getContext(), Class.forName(eventActivityClassName));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        newEventIntent.putExtra(getContext().getString(R.string.source_key), getContext().getString(R.string.history_custom_array_adapter_class_name));
                        newEventIntent.putExtra(getContext().getString(R.string.event_type_key), eventTypeStr);
                        //send only the selected users
                        Iterator<Long> userIt = userContainer.getUserMap().keySet().iterator();
                        while (userIt.hasNext()) {
                            if (!userContainer.getUserMap().get(userIt.next()).getIsSelected()) {
                                userIt.remove();
                            }
                        }
                        newEventIntent.putExtra(getContext().getString(R.string.group_id), groupIdStr);
                        newEventIntent.putExtra(getContext().getString(R.string.user_id_array_list_key), userContainer.getUserIdList());
                        getContext().startActivity(newEventIntent);
                    }
                })
                .setNegativeButton(R.string.start_activity_with_popup_negative_button_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    class  MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
        public Long mGroupId;
        public String mUserIdStr;
        public Long mEventId;
        public MyAsyncHttpResponseHandler(String userIdStr, Long groupId, Long eventId) {
            mUserIdStr = userIdStr;
            mGroupId = groupId;
            mEventId = eventId;
        }
        @Override
        public void onStart() {
            // called before request is started
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            //get the memberNames from the JSONresponse

            JSONObject jsonObject = null;
            //UserContainer userContainer = new UserContainer(HistoryCustomArrayAdapter.this.getContext());
            UserContainer userContainer = DcidrApplication.getInstance().getGlobalHistoryContainer().getGroupMap().get(mGroupId).getUserContainer();
            try {
                jsonObject = new JSONObject(new String(response));
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                try {
                    userContainer.populateUser(jsonArray);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String eventDetailActivityClassName = mEventAfterClick.getClass().getName() + "DetailActivity";
            Object eventDetailActivityObject = null;
            eventDetailActivityClassName = eventDetailActivityClassName.replace("main.event", "android_activity");
            try {
                eventDetailActivityObject = Class.forName(eventDetailActivityClassName).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (mClickType == ClickType.MEMBER_COUNT) {
                onMemberClicked(userContainer, mEventTypeStr, mGroupId);
            } else if (mClickType == ClickType.EVENT_DETAIL) {
                Intent eventDetailActivityIntent = new Intent(getContext(), eventDetailActivityObject.getClass());
                String eventClassName = mEventAfterClick.getClass().getName();
                BaseEvent baseEventObj = (BaseEvent) mEventAfterClick;
//                eventDetailActivityIntent.putExtra(getContext().getString(R.string.event_object_key), baseEventObj);
//                eventDetailActivityIntent.putExtra(getContext().getString(R.string.user_container_obj), userContainer);
                eventDetailActivityIntent.putExtra(getContext().getString(R.string.source_key),getContext().getString(R.string.history_custom_array_adapter_class_name));
                eventDetailActivityIntent.putExtra(getContext().getString(R.string.group_id), String.valueOf(mGroupId));
                eventDetailActivityIntent.putExtra(getContext().getString(R.string.event_id), String.valueOf(mEventId));
                getContext().startActivity(eventDetailActivityIntent);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            Log.i("Ketan", "Failure in getMembers");
            //this.onFailure(statusCode, headers, errorResponse, e);
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
            Log.e("Retry", "Retry");
        }
    }
}
