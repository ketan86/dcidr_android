package com.example.turbo.dcidr.main.activity_helper;

import android.util.Log;

/**
 * Created by Turbo on 4/2/2016.
 */
public class FetchManager {
    private int mStartIndex;
    private int mEndIndex;
    private int mMinGap;
    private int mMaxGap;
    private int mPreviousVisibleEndIndex;
    private int mPreviousEndIndex;
    private FetchManager.FetchInterface mFetchInterface;

    public interface FetchInterface {
        public void onFetch(int offset, int limit);
    }

    public void setOnFetchListener(FetchManager.FetchInterface fetchInterface){
        this.mFetchInterface = fetchInterface;
    }

    public void incrMinGap(int increment) {
        mMinGap += increment;
    }
    public int getMinGap(){
        return mMinGap;
    }
    public FetchManager(int minGap, int maxGap){
        this.mMinGap = minGap;
        this.mMaxGap = maxGap;
        this.mStartIndex = 0;
        this.mEndIndex = 0;
        this.mPreviousVisibleEndIndex = -1;
        this.mPreviousEndIndex = -1;

    }
    public FetchManager(int minGap, int maxGap, int startIndex, int endIndex){
        this.mMinGap = minGap;
        this.mMaxGap = maxGap;
        this.mStartIndex = startIndex;
        this.mEndIndex = endIndex;
    }

    public int getEndIndex(){
        return mEndIndex;
    }
    public void setEndIndex(int end){
         this.mEndIndex = end;
    }
    public void incrementEndIndex(){
         this.mEndIndex += 1;
    }
    public void fetch(int visibleStartIndex, int visibleEndIndex, FetchInterface fetchInterface){

        Log.i("Ketan:","Mgr:" + String.valueOf(mStartIndex) + " VS : " + String.valueOf(visibleStartIndex)  + " VE: " + String.valueOf(visibleEndIndex) + " E: " + String.valueOf(mEndIndex));

        if(mEndIndex == 0){
                mEndIndex = visibleEndIndex + mMinGap;
                fetchInterface.onFetch(visibleStartIndex, mEndIndex);
        }else {
            if((mEndIndex - visibleEndIndex) < 0 ) {
                return;
            }
            if (visibleEndIndex - mPreviousVisibleEndIndex <= 0) {
                //we are scrolling backwards or not scrolling at all.
                if (mEndIndex == mPreviousEndIndex) {
                    //the EndIndex is same as previous End Index, so no new data has to be added
                    //Log.i("Ketan", "Mgr: Not fetching, as prevVisible = " + previousVisibleEnd + ", visibleEnd = " + visibleEndIndex);
                    return;
                }
            }
            if((mEndIndex - visibleEndIndex) <= mMinGap ) {
                //Log.i("Ketan", "Mgr: mMinGap = " + mMinGap + ", mEndIndex = " + mEndIndex + ", mPrevEndIndex = "+ mPreviousEndIndex + ", mPrevVisibleEndIndex = " + mPreviousVisibleEndIndex+"visibleEndIndex = " + visibleEndIndex + ", fetching...");
                fetchInterface.onFetch(mEndIndex, mMinGap);
                mPreviousEndIndex = mEndIndex;
            }
        }
        mPreviousVisibleEndIndex = visibleEndIndex;
        Log.i("Ketan:S2","Mgr:" + String.valueOf(mStartIndex) + " VS : " + String.valueOf(visibleStartIndex)  + " VE: " + String.valueOf(visibleEndIndex) + " E: " + String.valueOf(mEndIndex));

//        mFetchedSoFar = visibleEndIndex;
//        if(visibleStartIndex != 0) {
//            if ((visibleStartIndex - mStartIndex) < mMinGap) {
//                if(mStartIndex - mMinGap > 0) {
//                    requestHandlerInterface.fetch(mStartIndex - mMinGap, mMinGap);
//                    mStartIndex -= mMinGap;
//                }
//            }
//        }

//        if((visibleStartIndex - mStartIndex) > mMaxGap) {
//            int endIndex = (visibleStartIndex - mStartIndex) - mMinGap;
//            //requestHandlerInterface.destroy(mStartIndex, endIndex);
//            mStartIndex = endIndex;
//        }

//        if((mEndIndex - visibleEndIndex) > mMaxGap) {
//            int startIndex = mEndIndex - mMinGap;
//            //requestHandlerInterface.destroy(startIndex, mEndIndex);
//            mEndIndex = startIndex;
//        }


    }
}
