package com.example.turbo.dcidr.utils.image_utils.image_selector;

import android.graphics.Bitmap;

/**
 * Created by borat on 8/28/2016.
 */
public class AttributedBitmap {

    public final CharSequence attribution;

    public final Bitmap bitmap;

    public AttributedBitmap(CharSequence attribution, Bitmap bitmap) {

        this.attribution = attribution;
        this.bitmap = bitmap;
    }
    public  AttributedBitmap( Bitmap bitmap) {
        this.attribution = ""; /* empty attribution */
        this.bitmap = bitmap;
    }
}