package com.example.turbo.dcidr.utils.image_utils.image_selector;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

/**
 * Created by borat on 8/28/2016.
 */
public class GoogleImageSelector {
    PlacePhotoMetadataResult mPlacePhotoMetadataResult;
    ImageSelectorCallbackInterface mCallbackInterfaceObj;
    GoogleApiClient mGoogleApiClient;
    Place mPlace;

    //Callback interface
    public interface ImageSelectorCallbackInterface {
        void onAsyncImageLoadDone(ArrayList<CustomBitmap> customBitmapArrayList);

    };

    public void onAsyncImageLoadDone(ImageSelectorCallbackInterface imageSelectorCallbackInterface){
        this.mCallbackInterfaceObj = imageSelectorCallbackInterface;
    }
    //Constructor
    public GoogleImageSelector(Place place, GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
        mPlace = place;
    }

    //Async image loading function . will call callbacks once done.
    public void loadImagesAsync() {
         new LocationPhotoTask().execute(mPlace); //launch the image
    }


    /* Class to load Photos Asynchronously */
    //AsyncTask <TypeOfVarArgParams , ProgressValue , ResultValue>.
    private class LocationPhotoTask extends AsyncTask<Place, Void, ArrayList<CustomBitmap>> {
        PlacePhotoMetadataResult mPlacePhotoMetadataResult;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<CustomBitmap> doInBackground(Place... params) {
            if (params.length != 1) {
                return null;
            }
            ArrayList<CustomBitmap> customBitmapArrayList = new ArrayList<>();
            mPlacePhotoMetadataResult =  Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, params[0].getId()).await();
            AttributedBitmap attributedBitmap = null;

            if (mPlacePhotoMetadataResult.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = mPlacePhotoMetadataResult.getPhotoMetadata();
                for(int index=0; index < photoMetadataBuffer.getCount(); index++) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(index);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, 500, 500).await().getBitmap();
                    attributedBitmap = new AttributedBitmap(attribution, image);
                    CustomBitmap cb = new CustomBitmap();
                    cb.mBitmap = attributedBitmap.bitmap;
                    if(index == 0){
                        // check first image as default
                        cb.mSelected = true;
                    }else {
                        cb.mSelected = false;
                    }
                    customBitmapArrayList.add(cb);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return customBitmapArrayList ;
        }

        @Override
        protected void onPostExecute(ArrayList<CustomBitmap> resultArray) {
            mCallbackInterfaceObj.onAsyncImageLoadDone(resultArray);
        }
    }
}
