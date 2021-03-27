package com.example.turbo.dcidr.main.common;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Turbo on 10/4/2016.
 */
public class ImageViewer {
    private Bitmap mImageBitmap;
    private String mAddedByUserName;
    private Context mContext;

    public ImageViewer(Context context){
        this.mContext = context;
    }

    public void setImageBitmap(Bitmap bitmap){
        this.mImageBitmap = bitmap;
    }
    public Bitmap getImageBitmap(){
        return this.mImageBitmap;
    }

    public void setAddedByUserName(String userName){
        this.mAddedByUserName = userName;
    }

    public String getAddedByUserName(){
        return this.mAddedByUserName;
    }

}
