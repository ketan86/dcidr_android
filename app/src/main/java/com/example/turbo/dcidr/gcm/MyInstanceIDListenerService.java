package com.example.turbo.dcidr.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Turbo on 2/22/2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * called when token is refreshed, it will launch RegistrationIntentService which interns gets
     * new token from GCM
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, RegistrationIntentService.class);
        intent.putExtra("SOURCE", "GCM");
        startService(intent);
    }
}
