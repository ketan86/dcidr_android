package com.example.turbo.dcidr.main.user;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Turbo on 2/11/2016.
 */
public class UserCache {
    static final String CACHED_USER_KEY
            = "com.example.turbo.dcidr.userCache";

    private SharedPreferences mSharedPreferences;

    public void setSharedPreferences(Context context){
        mSharedPreferences = context.getSharedPreferences(
                CACHED_USER_KEY,
                Context.MODE_PRIVATE);
    }

    public String get(String key) {
        return  mSharedPreferences.getString(key,null);
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void clear() {
        mSharedPreferences.edit().clear().commit();
    }

}
