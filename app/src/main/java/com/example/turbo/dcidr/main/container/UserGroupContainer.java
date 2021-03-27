package com.example.turbo.dcidr.main.container;

import com.example.turbo.dcidr.main.user.UserGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Turbo on 5/22/2016.
 */
public class UserGroupContainer implements Serializable {
    private HashMap<Long,ArrayList<UserGroup>> mUserGroupMap;
    public UserGroupContainer() {
    }
}
