package com.example.turbo.dcidr.main.activity_helper.login_activity_helper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.LoginActivity;
import com.example.turbo.dcidr.android_activity.MainActivity;
import com.example.turbo.dcidr.gcm.RegistrationIntentService;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.user_activity_helper.UserActivityHelper;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/10/2016.
 */
public class BaseLoginActivityHelper {
    private ProgressDialog mProgressDialog;
    protected BaseActivity mBaseActivity;
    public static final String TAG = "LoginActivity";
    private UserAsyncHttpClient mUserAsyncHttpClient;
    boolean mCreateUserFlag;

    /**
     * constructor for base login activity helper. other login helpers extend this class
     * @param activity : activity context
     */
    public BaseLoginActivityHelper(BaseActivity activity){
        this.mBaseActivity = activity;
        this.mCreateUserFlag =  false;
    }

    /**
     * method for setting cache for user 
     * @param key : name of cache
     * @param value : value of cache
     */
    public void setUserCache(String key, String value) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[setUserCache] Setting user cache");}
        DcidrApplication.getInstance().getUserCache().set(key, value);
    }

    /**
     * method for setting user on DB. it will get userId in response.
     */
    public void createUser(){
        if (BuildConfig.DEBUG){Log.i(TAG, "[createUser] Sending new singin request to server");}
        mCreateUserFlag =  true;
        mUserAsyncHttpClient = new UserAsyncHttpClient(mBaseActivity.getApplicationContext());
        try {
            mUserAsyncHttpClient.createUser(DcidrApplication.getInstance().getUser(), mUserAsyncHttpResponseHandler);
        } catch (UnsupportedEncodingException e) {
            if (BuildConfig.DEBUG){Log.i(TAG, "[createUser] UnsupportedEncodingException in create user");}
            mBaseActivity.showAlertDialog(mBaseActivity.getString(R.string.createuser_error_msg));
        }
    }

    public class SetDeviceCallbackRunnable implements Runnable {
        @Override
        public void run() {
            if (BuildConfig.DEBUG){Log.i(TAG, "[DeviceCallbackRunner.run] Got callback to proceed to Main Activity");}
            mBaseActivity.dismissProgressDialog(mProgressDialog);
            mProgressDialog = null;
            // finish login activity and go to main activity
            Intent mainActivityIntent = new Intent(mBaseActivity, MainActivity.class);
            mainActivityIntent.putExtra("SOURCE", mBaseActivity.getResources().getString(R.string.login_activity_class_name));
            mBaseActivity.startActivity(mainActivityIntent);

            // finish login activity
            LoginActivity loginActivity = (LoginActivity)mBaseActivity;
            loginActivity.finish();
        }
    }
    /**
     * response handler for user set request
     */
    protected AsyncHttpResponseHandler mUserAsyncHttpResponseHandler;

    {
        mUserAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "[CreateUser] createUser request started");
                }
                mProgressDialog = mBaseActivity.getAndShowProgressDialog(mBaseActivity, mBaseActivity.getString(R.string.login_inprogress_msg));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "[CreateUser] Request success");
                }
                if (statusCode == 201 || statusCode == 200) {
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "[CreateUser] Received status code " + statusCode);
                    }
                    // set user populated to true
                    DcidrApplication.getInstance().getUser().setIsPopulated(true);
                    JSONObject jsonObject = null;
                    long userId = -1;
                    String authToken = null;
                    String emailId = null;
                    try {
                        jsonObject = new JSONObject(new String(response));
                        userId = jsonObject.getLong("userId");
                        authToken = jsonObject.getString("authToken");
                        emailId = jsonObject.getString("emailId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // set id on User class and set in cache
                    DcidrApplication.getInstance().getUser().setUserId(userId);
                    setUserCache("userId", String.valueOf(userId));
                    setUserCache("emailId", emailId);
                    setUserCache("authToken", authToken);

                    // init user class by making request to db
                    UserActivityHelper userActivityHelper = new UserActivityHelper(mBaseActivity);
                    userActivityHelper.initUser();

                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "[CreateUser] Registering setDevice callback ");
                    }
                    SetDeviceCallbackRunnable setDeviceRunnableCallback = new SetDeviceCallbackRunnable();
                    DcidrApplication.getInstance().setDeviceCallbackRunnable(setDeviceRunnableCallback);
                    Intent intent = new Intent(mBaseActivity, RegistrationIntentService.class);
                    intent.putExtra("SOURCE", "LOCAL");
                    mBaseActivity.startService(intent);
                    Utils.hideSoftKeyboard(mBaseActivity);

                    //set the user image either from DCIDR, Facebook or Google
                    if (mCreateUserFlag == true) { //this is not null only for the signup workflow
                        try {
                            mUserAsyncHttpClient.setUserMedia(String.valueOf(userId), DcidrApplication.getInstance().getUser().getUserProfilePicBase64Str(),
                                "image", new AsyncHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        Log.i("DCIDR", "Image stored successfully");
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.e("DCIDR", "Error storing user Profile pic ");
                                    }
                                });
                        }catch(JSONException e){
                            e.printStackTrace();
                        }catch(UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // if login is not successful, send failure code and handle it
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "[CreateUser] Failure");
                }
                JSONObject jsonObject = null;
                String errorString = null;
                try {
                    jsonObject = new JSONObject(new String(errorResponse));
                    errorString = (String) jsonObject.get("error");
                } catch (JSONException error) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "[CreateUser] JSONException : error" + error.toString());
                    }
                }
                mBaseActivity.showAlertDialog(errorString);
                mProgressDialog.dismiss();
                mProgressDialog = null;
                Utils.hideSoftKeyboard(mBaseActivity);
            }
        };
    }

    public void releaseMemory(){
        mProgressDialog = null;
    }
}
