package com.example.turbo.dcidr.main.user;

import android.content.Context;

import com.example.turbo.dcidr.main.group.BaseGroup;

/**
 * Created by Turbo on 5/22/2016.
 */
public class UserGroup {

    private User mUser;
    private BaseGroup mBaseGroup;
    private Context mContext;
    public UserGroup(Context context){
        this.mContext = context;
        mUser = new User(mContext);
        mBaseGroup = new BaseGroup(context);
    }
}
