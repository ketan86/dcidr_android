package com.example.turbo.dcidr.utils.common_utils;

import com.example.turbo.dcidr.android_activity.BaseActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Turbo on 5/13/2016.
 */
public class SmartSearch {
    private HashMap<String, ArrayList<?>> filteredHashMap;
    private SmartSearch.GetDataForSearchInterface mDataForSearchInterface;
    private ArrayList<?> mSearchedObjList;
    private BaseActivity mBaseActivity;
    public SmartSearch(BaseActivity baseActivity){
        this.mBaseActivity = baseActivity;
        this.filteredHashMap = new HashMap<String, ArrayList<?>>();
        this.mSearchedObjList = new ArrayList<>();
    }

    public interface Searchable {
        String getSearchString();
    }

    public interface OnSearchFinishCallback {
        void call(ArrayList<?> arrayList);
    }
    public interface OnFinishCallback {
        void call(ArrayList<?> arrayList);
    }
    public interface GetDataForSearchInterface {
        void get(String searchStr, SmartSearch.OnFinishCallback onFinishCallback);
    }

    public void setDataForSearchCallback(SmartSearch.GetDataForSearchInterface mDataForSearchInterface){
        this.mDataForSearchInterface = mDataForSearchInterface;
    }

    public void search(final String searchStr, final SmartSearch.OnSearchFinishCallback onSearchFinishCallback){
        if (searchStr.equals("")) {
            onSearchFinishCallback.call(mSearchedObjList);
            return;
        }

        if(filteredHashMap.containsKey(searchStr)){
            onSearchFinishCallback.call(filteredHashMap.get(searchStr));
            return;
        }

        String searchStrPrefix = searchStr.substring(0, searchStr.length() -1);

        if(!filteredHashMap.containsKey(searchStrPrefix)){
            this.mDataForSearchInterface.get(searchStr, new OnFinishCallback() {
                @Override
                public void call(ArrayList<?> arrayList) {
                    filteredHashMap.put(searchStr, arrayList);
                    mSearchedObjList = filteredHashMap.get(searchStr);
                    onSearchFinishCallback.call(mSearchedObjList);
                    return;
                }
            });
        }else {
            mSearchedObjList = filteredHashMap.get(searchStrPrefix);
            ArrayList<Object> arrayList = new ArrayList<>();
            for(int i=0 ; i < mSearchedObjList.size(); i++){
                String str = null;
                try {
                    Method method = mSearchedObjList.get(i).getClass().getMethod("getSearchString", new Class<?>[]{});
                    str = (String) method.invoke(mSearchedObjList.get(i));
                } catch (NoSuchMethodException e) {
                    mBaseActivity.showAlertDialog("Error getting search string");
                } catch (InvocationTargetException e) {
                    mBaseActivity.showAlertDialog("Error getting search string");
                } catch (IllegalAccessException e) {
                    mBaseActivity.showAlertDialog("Error getting search string");
                }
                if(str == null) {
                    mBaseActivity.showAlertDialog("Error getting search string");
                    onSearchFinishCallback.call(arrayList);
                    return;
                }else {
                    if(str.toLowerCase().contains(searchStr)){
                        arrayList.add(mSearchedObjList.get(i));
                    }
                }
            }
            //if(arrayList.size() != 0){
            filteredHashMap.put(searchStr, arrayList);
            //}
            onSearchFinishCallback.call(arrayList);
        }
    }
}
