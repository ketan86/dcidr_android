package com.example.turbo.dcidr.main.container;

import android.content.Context;

import com.example.turbo.dcidr.main.event.Chweet;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by borat on 10/2/2016.
 */
public class ChweetContainer {
    private ArrayList<Chweet> mChweetList;
    private HashMap<Long,Chweet> mChweetMap;
    private HashMap<Long, Integer> mUserChweetColorMap;
    private Context mContext;
    public ChweetContainer(Context context) {
        this.mContext = context;
        this.mChweetList = new ArrayList<Chweet>();
        this.mChweetMap = new HashMap<Long,Chweet>();
        this.mUserChweetColorMap = new HashMap<Long,Integer>();
    }
    public void populateMe(JSONArray jsonChweetArray) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        for (int i = 0; i < jsonChweetArray.length(); i++) {
            JSONObject jsonChweetObj =  jsonChweetArray.getJSONObject(i);
            populateMe(jsonChweetObj);
        }
    }


    public void populateMe(JSONObject jsonChweetObject) throws
            JSONException,ClassNotFoundException,IllegalAccessException,
            InstantiationException, InvocationTargetException, NoSuchMethodException{
        if (jsonChweetObject.getString("chweetId") != null && mChweetMap.containsKey(jsonChweetObject.getLong("chweetId"))) {
            Chweet c =  mChweetMap.get(jsonChweetObject.getLong("chweetId"));
            if(c.getChweetColor() == Chweet.UNKNOWN) {
                int chweetColor = Utils.generateRandomColor();
                c.setChweetColor(chweetColor);
                mUserChweetColorMap.put(c.getUserId(),chweetColor);
            }
            c.populateMe(jsonChweetObject);
        } else {
            Chweet c = new Chweet();
            c.populateMe(jsonChweetObject);
            if(!mUserChweetColorMap.containsKey(c.getUserId())) {
                int chweetColor = Utils.generateRandomColor();
                mUserChweetColorMap.put(c.getUserId(), chweetColor);
                c.setChweetColor(chweetColor);
            }else {
                int chweetColor = mUserChweetColorMap.get(c.getUserId());
                c.setChweetColor(chweetColor);
            }
            mChweetMap.put(c.getChweetId(), c);

        }

    }

    public void refreshChweetList(){
        mChweetList.clear();
        ArrayList<Chweet> arrayList = new ArrayList<Chweet>(mChweetMap.values());
        Collections.sort(arrayList);
        mChweetList.addAll(arrayList);
    }

    public HashMap<Long, Chweet> getChweetMap(){
        return this.mChweetMap;
    }

    public ArrayList<Chweet> getChweetList() {
        this.refreshChweetList();
        return this.mChweetList;
    }

    public void clear(){
        for(Chweet chweet:mChweetList){
            chweet.releaseMemory();
        }
        mChweetList.clear();
        mChweetMap.clear();
    }
}
