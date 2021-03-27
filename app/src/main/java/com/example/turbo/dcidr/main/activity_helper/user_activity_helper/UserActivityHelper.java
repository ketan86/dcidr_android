package com.example.turbo.dcidr.main.activity_helper.user_activity_helper;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.LoginActivity;
import com.example.turbo.dcidr.android_activity.MainActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.user.User;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/13/2016.
 */
public class UserActivityHelper {
    private BaseActivity mBaseActivity;
    public static final String TAG = "UserActivityHelper";
    private UserFetchInterface mUserFetchInterface;
    public interface UserFetchInterface{
        void onFetchDone();
    }

    public void setUserFetchInterface(UserFetchInterface userFetchInterface){
        this.mUserFetchInterface = userFetchInterface;
    }


    public UserActivityHelper(BaseActivity activity){
        this.mBaseActivity = activity;
    }

    public void clearUserCache(){
        if (BuildConfig.DEBUG){Log.i(TAG, "[clearUserCache] Clearing user cache");}
        DcidrApplication.getInstance().getUserCache().clear();
    }

    public void clearNotifications(){
        if (BuildConfig.DEBUG){Log.i(TAG, "[clearNotifications] Clearing user notifications");}
        // clear all notification form notification bar since user is logging out
        NotificationManager notificationManager = (NotificationManager) mBaseActivity.getBaseContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void clearAppState(){
        if (BuildConfig.DEBUG){Log.i(TAG, "[clearAppState] Clearing the application state");}
        DcidrApplication.getInstance().clear();
    }

    public void logoutUser(){
        clearUserCache();
        clearNotifications();

        // based on loginType, perform logout
        if(DcidrApplication.getInstance().getUser().getLoginType() == User.LoginType.FACEBOOK) {
            LoginManager.getInstance().logOut();
        }else if (DcidrApplication.getInstance().getUser().getLoginType() == User.LoginType.GOOGLE) {
            if (DcidrApplication.getInstance().getGoogleApiClient().isConnected()) {
                DcidrApplication.getInstance().getGoogleApiClient().disconnect();
            }
        } else if (DcidrApplication.getInstance().getUser().getLoginType() == User.LoginType.DCIDR) {
            // no need to do anything for dcidr logout
        }
        // clear application state
        clearAppState();

        // finish caller activity and go back to login activity
        MainActivity mainActivity = (MainActivity) mBaseActivity;
        mainActivity.finish();
        launchLoginActivity();
    }

    public void launchLoginActivity(){
        if (BuildConfig.DEBUG){Log.i(TAG, "[launchLoginActivity] Launching login activity");}
        Intent loginActivityIntent = new Intent(mBaseActivity, LoginActivity.class);
        loginActivityIntent.putExtra("SOURCE", mBaseActivity.getResources().getString(R.string.main_activity_class_name));
        mBaseActivity.startActivity(loginActivityIntent);
    }

    public String getUserEmailFromCache(){
        String userEmail = DcidrApplication.getInstance().getUserCache().get("emailId");
        if(userEmail != null){
            return userEmail;
        }
        return null;
    }
    public String getUserIdFromCache(){
        String userId = DcidrApplication.getInstance().getUserCache().get("userId");
        if(userId != null){
            return userId;
        }
        return null;
    }

    public void initUser(){
        String userId = getUserIdFromCache();
        if (BuildConfig.DEBUG){Log.i(TAG, "[initUser] Initializing user");}
        if (userId != null) {
            UserAsyncHttpClient userAsyncHttpClient = new UserAsyncHttpClient(mBaseActivity.getApplicationContext());
            userAsyncHttpClient.getUser(userId, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    if (statusCode == 200){
                        if (BuildConfig.DEBUG){Log.i(TAG, "[initUser.getUser] Received statusCode 200");}
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(new String(response));
                            DcidrApplication.getInstance().getUser().populateMe(jsonObject);
                            if(mUserFetchInterface != null) {
                                mUserFetchInterface.onFetchDone();
                            }
                        } catch (JSONException e) {
                            if (BuildConfig.DEBUG){Log.i(TAG, "[initUser.getUser] Error parsing json response: " + e.toString());}
                        }
                    }else{
                        if (BuildConfig.DEBUG){Log.i(TAG, "[initUser.getUser] Received statusCode other than 200");}
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    if (BuildConfig.DEBUG){Log.i(TAG, "[initUser] Got failure ");}

                    JSONObject jsonObject = null;
                    String errorString = null;
                    try {
                        jsonObject = new JSONObject(new String(errorResponse));
                        errorString = (String) jsonObject.get("error");
                        if (BuildConfig.DEBUG){Log.i(TAG, "[initUser.getUser] Failure error: " + errorString);}
                    } catch (JSONException error) {
                        if (BuildConfig.DEBUG){Log.i(TAG, "[initUser.getUser] Error parsing json response: " + error.toString());}
                    }
                }
            });
        }else {
            if (BuildConfig.DEBUG){Log.i(TAG, "[initUser] userId is null. Initialization can not be done");}
        }
    }
}
