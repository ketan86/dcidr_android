package com.example.turbo.dcidr.main.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.BaseAdapter;

import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.utils.common_utils.SmartSearch;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 2/7/2016.
 */



public class User implements Serializable, Comparable, SmartSearch.Searchable {

    @Override
    public String getSearchString() {
        return this.mFirstName + " " + this.mLastName;
    }

    public enum UserSortKey {
        USER_FIRST_NAME, USER_LAST_NAME
    }

    public enum LoginType {
        FACEBOOK(1),
        GOOGLE(2),
        DCIDR(3);

        private int value;

        private LoginType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    public static long UNDEFINED = -1;
    private boolean mIsSelected;
    private long mUserId;
    private LoginType mLoginType;
    private String mFirstName;
    private String mLastName;
    private long mUserCreationTime;
    private long mLoginTime;
    private long mLogoutTime;
    private String mEmailId;
    private boolean mIsPopulated;
    private String mPasswordDigest;
    private String mAuthToken;
    private UserSortKey mUserSortKey;
    private Bitmap mUserProfilePicBitmap;
    private String mUserProfilePicBase64Str;
    private String mUserProfilePicUrl;
    private BaseAdapter mBaseAdapter;
    private Context mContext;
    private UserProfPicFetchInterface mUserProfPicFetchInterface;

    public interface UserProfPicFetchInterface {
        void onImageFetchDone(Bitmap bitmap);
        int getImageWidth();
        int getImageHeight();
    }

    public void onUserProfPicFetchDoneListener(UserProfPicFetchInterface userProfPicFetchInterface){
        this.mUserProfPicFetchInterface = userProfPicFetchInterface;
    }


    public User(Context context) {
        this.mContext = context;
        this.mIsPopulated = false;
        this.mUserId = UNDEFINED;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public void setIsSelected(boolean isSelected) {
        this.mIsSelected = isSelected;
    }

    public void setAuthToken(String authToken) {
        this.mAuthToken = authToken;
    }



    public void setUserProfilePicBase64Str(String base64Str){
        this.mUserProfilePicBase64Str = base64Str;
    }

    public String getUserProfilePicBase64Str(){
        return this.mUserProfilePicBase64Str;
    }

    public Bitmap getUserProfilePicBitmap(){
        return this.mUserProfilePicBitmap;
    }

    public void setUserProfilePicBitmap(Bitmap bitmap){
        this.mUserProfilePicBitmap = bitmap;
    }

    public void setUserProfilePicUrl(String url){
        this.mUserProfilePicUrl = url;
    }

    public String getUserProfilePicUrl(){
        return this.mUserProfilePicUrl;
    }
    public void setLoginType(LoginType loginType) {
        this.mLoginType = loginType;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getUserName() {
        if (this.mLastName == null) {
            return this.mFirstName;
        }
        if(this.mFirstName == null && this.mLastName == null){
            return null;
        }
        return this.mFirstName + " " + this.mLastName;
    }
    public void setEmailId(String emailId) {
        this.mEmailId = emailId;
    }

    public void setPasswordDigest(String passwordDigest) {
        this.mPasswordDigest = passwordDigest;
    }

    public void setLoginTime(long epochTime) {
        this.mLoginTime = epochTime;
    }

    public void setUserCreationTime(long epochTime) {
        this.mUserCreationTime = epochTime;
    }

    public void setLogoutTime(long epochTime) {
        this.mLogoutTime = epochTime;
    }

    public long getUserId() {
        return this.mUserId;
    }

    public String getUserIdStr() {
        return String.valueOf(this.mUserId);
    }

    public boolean getIsSelected() {
        return this.mIsSelected;
    }

    public String getAuthToken() {
        return this.mAuthToken;
    }

    public LoginType getLoginType() {
        return this.mLoginType;
    }

    public int getLoginTypeValue() {
        return this.mLoginType.getValue();
    }

    public String getFirstName() {
        return this.mFirstName;
    }

    public String getLastName() {
        return this.mLastName;
    }

    public String getPasswordDigest() {
        return this.mPasswordDigest;
    }

    public long getLoginTime() {
        return this.mLoginTime;
    }

    public long getUserCreationTime() {
        return this.mUserCreationTime;

    }

    public String getEmailId() {
        return this.mEmailId;
    }

    public long getLogoutTime() {
        return this.mLogoutTime;
    }

    public HashMap<String, Object> getUserMap() {
        HashMap<String, Object> userMap = new HashMap<String, Object>();
        //userMap.put("id",this.getId());
        userMap.put("emailId", this.getEmailId());
        userMap.put("firstName", this.getFirstName());
        userMap.put("lastName", this.getLastName());
        userMap.put("loginType", this.getLoginType().toString());
        userMap.put("logoutTime", this.getLogoutTime());
        userMap.put("passwordDigest", this.getPasswordDigest());
        // TODO need to send base64 only when create user request is sent, not during login (need to split getUserMap into two diff method calls)
        userMap.put("userProfilePicBase64Str", this.getUserProfilePicBase64Str());
        return userMap;
    }

    /**
     * If the Integer is equal to the argument then 0 is returned.
     * If the Integer is less than the argument then -1 is returned.
     * If the Integer is greater than the argument then 1 is returned.
     */
    @Override
    public int compareTo(Object another) {

        User anotherUser = (User) another;
        if (mUserSortKey == mUserSortKey.USER_LAST_NAME) {
            //TODO  implement if required. Setting up template for sorting based on different keys
            return -1;
        } else {
            if (this.getFirstName() == null && anotherUser.getFirstName() == null ) {
                return this.getEmailId().compareTo(anotherUser.getEmailId()) < 0 ? -1 :
                        this.getEmailId().compareTo(anotherUser.getEmailId()) > 0 ? 1 : 0;
            } else if (this.getFirstName() == null && anotherUser.getFirstName() != null ) {
                return this.getEmailId().compareTo(anotherUser.getFirstName()) < 0 ? -1 :
                        this.getEmailId().compareTo(anotherUser.getFirstName()) > 0 ? 1 : 0;
            } else if (this.getFirstName() != null && anotherUser.getFirstName() == null) {
                return this.getFirstName().compareTo(anotherUser.getEmailId()) < 0 ? -1 :
                        this.getFirstName().compareTo(anotherUser.getEmailId()) > 0 ? 1 : 0;
            }else {
                return this.getFirstName().compareTo(anotherUser.getFirstName()) < 0 ? -1 :
                        this.getFirstName().compareTo(anotherUser.getFirstName()) > 0 ? 1 : 0;
            }
        }
    }

    public void setIsPopulated(boolean isPopulated) {
        this.mIsPopulated = isPopulated;
    }

    public boolean getIsPopulated() {
        return this.mIsPopulated;
    }

    public void populateMe(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("userId") && !jsonObject.isNull("userId")) {
            this.setUserId(jsonObject.getLong("userId"));
        }
        if (jsonObject.has("emailId") && !jsonObject.isNull("emailId")) {
            this.setEmailId(jsonObject.getString("emailId"));
        }
        if (jsonObject.has("firstName") && !jsonObject.isNull("firstName")) {
            this.setFirstName(jsonObject.getString("firstName"));
        }
        if (jsonObject.has("lastName") && !jsonObject.isNull("lastName")) {
            this.setLastName(jsonObject.getString("lastName"));
        }
        if (jsonObject.has("loginType") && !jsonObject.isNull("loginType")) {
            this.setLoginType(User.LoginType.values()[(jsonObject.getInt("loginType")) - 1]);
        }
        if (jsonObject.has("loginTime") && !jsonObject.isNull("loginTime")) {
            this.setLoginTime(jsonObject.getLong("loginTime"));
        }
        if (jsonObject.has("logoutTime") && !jsonObject.isNull("logoutTime")) {
            this.setLogoutTime(jsonObject.getLong("logoutTime"));
        }
        if (jsonObject.has("userCreationTime") && !jsonObject.isNull("userCreationTime")) {
            this.setUserCreationTime(jsonObject.getLong("userCreationTime"));
        }
        if (jsonObject.has("authToken") && !jsonObject.isNull("authToken")) {
            this.setAuthToken(jsonObject.getString("authToken"));
        }

        if (jsonObject.has("userProfilePicUrl")) {
            if(!jsonObject.isNull("userProfilePicUrl")) {
                this.setUserProfilePicUrl(jsonObject.getString("userProfilePicUrl"));
            }else{
                if (mUserProfPicFetchInterface != null) {
                    mUserProfPicFetchInterface.onImageFetchDone(null);
                }
            }
            //launch a async task to fetch the group image from the server and store it into the BaseGroup object
            if(this.getUserProfilePicBase64Str() == null && this.getUserProfilePicUrl() != null) {
               new UserProfilePicAsyncLoad().execute("Fetching user profile pic");
            } //else {
//                if(mUserProfilePicBitmap == null) {
//                    if(this.getUserProfilePicBase64Str() != null) {
//                        Bitmap imageBitMap = Utils.decodeBase64(this.getUserProfilePicBase64Str());
//                        mUserProfilePicBitmap = imageBitMap;
//                    }
//                }
//            }
        }
        setIsPopulated(true);
    }

    private class UserProfilePicAsyncLoad extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                final String emailId = getEmailId();
                Log.i("Ketan", "Fetching user Profile pic for emailid = " + emailId );

                UserAsyncHttpClient userAsyncHttpClient = new UserAsyncHttpClient(mContext);
                // make sure first arg is current user instead of user of the User object
                userAsyncHttpClient.getUserMediaByUrl(DcidrApplication.getInstance().getUser().getUserIdStr(), getUserProfilePicUrl(), new AsyncHttpResponseHandler(Looper.getMainLooper()) {
                    @Override
                    public void onStart() {
                        //starting off the fetching. no log present
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (statusCode == 200) {
                            try {
                                Log.i("Ketan", "Fetched user Profile pic for emailid = " + emailId);
                                String result = new JSONObject(new String(responseBody)).getString("result");
                                setUserProfilePicBase64Str(result);
                                if (mUserProfPicFetchInterface != null) {
                                    Bitmap imageBitMap = Utils.decodeBase64(result, mUserProfPicFetchInterface.getImageWidth(), mUserProfPicFetchInterface.getImageHeight());
                                    setUserProfilePicBitmap(imageBitMap);
                                    mUserProfPicFetchInterface.onImageFetchDone(imageBitMap);
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

    public void releaseMemory(){
        if(mUserProfPicFetchInterface != null){
            mUserProfPicFetchInterface.onImageFetchDone(null);
            mUserProfPicFetchInterface = null;
        }

        setUserProfilePicBitmap(null);
        setUserProfilePicBase64Str(null);

    }

}