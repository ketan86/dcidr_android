package com.example.turbo.dcidr.utils.layout_utils;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Turbo on 9/7/2016.
 */
public class ListViewUpdater {

    private OnListItemFoundListener mOnListItemFoundListener;

    public interface OnListItemFoundListener {
        void onListItemFound(int position, View view);
    }
    public void setOnListItemFoundListener(OnListItemFoundListener onListItemFoundListener){
        this.mOnListItemFoundListener = onListItemFoundListener;
    }
    public void updateListViewVisibleItems(AdapterView listView, int currPosition, int resourceId, OnListItemFoundListener onListItemFoundListener){
        int firstVisibleElement = listView.getFirstVisiblePosition();
        int lastVisibleElement = listView.getLastVisiblePosition();
        for(int i=firstVisibleElement; i<=lastVisibleElement; i++) {
            if(i != currPosition) {
                View view = listView.getChildAt(i -firstVisibleElement);
                View childView = view.findViewById(resourceId);
                if(onListItemFoundListener != null){
                    onListItemFoundListener.onListItemFound(i, childView);
                }
            }
        }
    }
}
