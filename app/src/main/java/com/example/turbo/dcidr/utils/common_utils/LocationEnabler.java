package com.example.turbo.dcidr.utils.common_utils;

import android.content.IntentSender;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by Turbo on 4/20/2016.
 */
public class LocationEnabler {
    private boolean mIsLocationEnabled;
    private BaseActivity mBaseActivity;
    private LocationStatusInterface mLocationStatusInterface;


    public interface LocationStatusInterface {
        void getLocationStatus(int statusCode);
    }

    public void onLocationStatusListener(LocationStatusInterface locationStatusInterface){
        this.mLocationStatusInterface = locationStatusInterface;
    }

    public LocationEnabler(BaseActivity activity){
        this.mBaseActivity = activity;
    }
    public void enableLocation(final int requestCode)
    {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mBaseActivity)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // location service is enabled
                        if(mLocationStatusInterface != null){
                            mLocationStatusInterface.getLocationStatus(LocationSettingsStatusCodes.SUCCESS);
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        mLocationStatusInterface.getLocationStatus(LocationSettingsStatusCodes.RESOLUTION_REQUIRED);
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(mBaseActivity, requestCode);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        mLocationStatusInterface.getLocationStatus(LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE);
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
}
