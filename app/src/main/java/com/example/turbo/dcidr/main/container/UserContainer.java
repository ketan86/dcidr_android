package com.example.turbo.dcidr.main.container;

import android.content.Context;
import android.util.Log;

import com.example.turbo.dcidr.main.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Turbo on 4/9/2016.
 */
public class UserContainer implements Serializable {
    private ArrayList<User> mUserList;
    private HashMap<Long,User> mUserMap;
    private User.UserSortKey mUserSortKey;
    private Context mContext;
    public UserContainer(Context context) {
        this.mContext = context;
        this.mUserList = new ArrayList<User>();
        this.mUserMap = new HashMap<Long,User>();
    }
    public void populateUser(JSONArray jsonUserArray) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        for (int i = 0; i < jsonUserArray.length(); i++) {
            JSONObject jsonUser =  jsonUserArray.getJSONObject(i);
            Log.i("Ketan:CG3", jsonUser.getString("userId"));
            if(mUserMap.containsKey(jsonUser.getLong("userId"))) {
                mUserMap.get(jsonUser.getLong("userId")).populateMe(jsonUser);
                continue;
            }
            User user = new User(mContext);
            user.populateMe(jsonUser);
            mUserMap.put(user.getUserId(), user);
        }
    }

    public void refreshUserList(){
        // TODO need to return list sorted by last modified time
        mUserList.clear();
        ArrayList<User> arrayList = new ArrayList<User>(mUserMap.values());
        Collections.sort(arrayList);
        mUserList.addAll(arrayList);
    }

    public void setUserSortKey(User.UserSortKey key) {mUserSortKey = key;}


    public void addToUserMap(User user){
        mUserMap.put(user.getUserId(), user);
    }
    public HashMap<Long, User> getUserMap(){
        return this.mUserMap;
    }
    public void clearUserMap(){
        mUserMap.clear();
    }
    public ArrayList<User> getUserList() {
        this.refreshUserList();
        return this.mUserList;
    }

    public ArrayList<Long> getUserIdList() {
        this.refreshUserList();
        ArrayList<Long> userIdList = new ArrayList<>();
        for (int i=0;i <this.mUserList.size();i++) {
            userIdList.add(this.mUserList.get(i).getUserId());
        }
        return userIdList;
    }

    public void clear(){
        for(User user:mUserList){
            user.releaseMemory();
        }
        mUserList.clear();
        mUserMap.clear();
    }
}
