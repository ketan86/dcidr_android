//package com.example.turbo.dcidr.global;
//
//import android.util.Log;
//
//import com.example.turbo.dcidr.main.callback_helper.CallbackHelper;
//import com.example.turbo.dcidr.main.user.User;
//import com.example.turbo.dcidr.main.user.UserCache;
//import com.facebook.AccessToken;
//import com.google.android.gms.common.api.GoogleApiClient;
//
///**
// * Created by Turbo on 2/10/2016.
// */
//public class GlobalStateSingleton {
//    private User mUser = null;
//    //private GroupContainer mGroupContainer = null ;
//    private UserCache mUserCache = null;
//    private CallbackHelper mCallbackHelper = null;
//    private static GlobalStateSingleton mGlobalStateObject = null;
//    private GoogleApiClient mGoogleApiClient;
//    private AccessToken mFbAccessToken;
//
//
//
//    private GlobalStateSingleton() {
////        mUser = new User();
////        mUserCache = new UserCache();
////        mCallbackHelper = new CallbackHelper();
////        mGroupContainer = new GroupContainer();
//    }
//
//    public static GlobalStateSingleton getInstance() {
//
//        if (mGlobalStateObject == null) {
//            mGlobalStateObject = new GlobalStateSingleton();
//        }
//        Log.i("mUser:singleton", mGlobalStateObject.toString());
//        return mGlobalStateObject;
//    }
//
//    public AccessToken getFbAccessToken(){
//        return mFbAccessToken;
//    }
//    public void setFbAccessToken(AccessToken fbAccessToken) {
//        mFbAccessToken = fbAccessToken;
//    }
//    public User getUser() {
//        if (mUser == null) {
//            mUser = new User();
//        }
//        return mUser;
//    }
//    public UserCache getUserCache() {
//        if (mUserCache == null) {
//            mUserCache = new UserCache();
//        }
//        return mUserCache;
//    }
//    public CallbackHelper getCallbackHelper() {
//        if (mCallbackHelper == null) {
//            mCallbackHelper = new CallbackHelper();
//        }
//        return mCallbackHelper;
//    }
////    public GroupContainer getGroupContainer() {
////        if (mGroupContainer == null) {
////            mGroupContainer= new GroupContainer();
////        }
////        return mGroupContainer;
////    }
//    public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }
//    public void setGoogleApiClient(GoogleApiClient googleApiClient) {mGoogleApiClient = googleApiClient;}
//
//    public void clear(){
//        mUser = null;
//        //mGroupContainer = null;
//        mCallbackHelper = null;
//        mGlobalStateObject = null;
//        //we dont set cache to null because we use cache to determine if user is logged in
//    }
//}
