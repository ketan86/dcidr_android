package com.example.turbo.dcidr.global;

import android.app.Application;

import com.example.turbo.dcidr.main.activity_helper.login_activity_helper.BaseLoginActivityHelper;
import com.example.turbo.dcidr.main.container.ContactContainer;
import com.example.turbo.dcidr.main.container.GroupContainer;
import com.example.turbo.dcidr.main.container.HistoryContainer;
import com.example.turbo.dcidr.main.user.Contact;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.main.user.UserCache;
import com.facebook.AccessToken;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.Serializable;

/**
 * Created by Turbo on 4/19/2016.
 */
public class DcidrApplication extends Application implements Serializable {
    private static DcidrApplication sAppInstance;
    private User mUser;
    private UserCache mUserCache;
    private GoogleApiClient mGoogleApiClient;
    private GroupContainer mGlobalGroupContainer;
    private HistoryContainer mGlobalHistoryContainer;
    private ContactContainer mGlobalContactContainer;
    private AccessToken mFbAccessToken;
    private BaseLoginActivityHelper.SetDeviceCallbackRunnable mSetDeviceCallbackRunnable;
    public static DcidrApplication getInstance() {
        return sAppInstance;
    }

    public User getUser() {
        // re create object if logout has destroyed it
        if (mUser == null) {
            mUser = new User(getApplicationContext());
        }
        return mUser;
    }

    public void setDeviceCallbackRunnable(BaseLoginActivityHelper.SetDeviceCallbackRunnable setDeviceCallbackRunnable){
        this.mSetDeviceCallbackRunnable = setDeviceCallbackRunnable;
    }
    public BaseLoginActivityHelper.SetDeviceCallbackRunnable getDeviceCallbackRunnable(){
        return this.mSetDeviceCallbackRunnable;
    }
    public UserCache getUserCache() {
        return mUserCache;
    }
    public GroupContainer getGlobalGroupContainer() {
        // re create object if logout has destroyed it
        if(mGlobalGroupContainer == null) {
            mGlobalGroupContainer = new GroupContainer(getApplicationContext());
        }
        return mGlobalGroupContainer;
    }
    public HistoryContainer getGlobalHistoryContainer() {
        // re create object if logout has destroyed it
        if(mGlobalHistoryContainer == null) {
            mGlobalHistoryContainer = new HistoryContainer(getApplicationContext());
        }
        return mGlobalHistoryContainer;
    }

    public ContactContainer getGlobalContactContainer() {
        // re create object if logout has destroyed it
        if(mGlobalContactContainer == null) {
            mGlobalContactContainer = new ContactContainer(getApplicationContext());
            mGlobalContactContainer.setContactSortKey(Contact.UserSortKey.USER_FIRST_NAME);

        }
        return mGlobalContactContainer;
    }
    public AccessToken getFbAccessToken(){
        return mFbAccessToken;
    }
    public void setFbAccessToken(AccessToken fbAccessToken) {
        mFbAccessToken = fbAccessToken;
    }


    public void clear(){
        mUser = null;
        mGlobalGroupContainer = null;
        mGlobalHistoryContainer = null;
        mGlobalContactContainer = null;
        //we dont set cache to null because we use cache to determine if user is logged in
        mGoogleApiClient = null;
        mFbAccessToken = null;
        mSetDeviceCallbackRunnable = null;
    }
    public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
        sAppInstance.initializeInstances();
    }



    protected void initializeInstances() {
        // do all your initialization here
        if (mUser == null) {
            mUser = new User(getApplicationContext());
        }
        if (mUserCache == null) {
            mUserCache = new UserCache();
        }
        if(mGlobalGroupContainer == null) {
            mGlobalGroupContainer = new GroupContainer(getApplicationContext());
        }
        if(mGlobalHistoryContainer == null) {
            mGlobalHistoryContainer = new HistoryContainer(getApplicationContext());
        }

        if(mGlobalContactContainer == null) {
            mGlobalContactContainer = new ContactContainer(getApplicationContext());
            mGlobalContactContainer.setContactSortKey(Contact.UserSortKey.USER_FIRST_NAME);
        }
    }
}
