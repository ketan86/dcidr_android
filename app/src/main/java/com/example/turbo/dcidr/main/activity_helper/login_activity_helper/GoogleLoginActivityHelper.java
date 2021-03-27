package com.example.turbo.dcidr.main.activity_helper.login_activity_helper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.user.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleLoginActivityHelper extends BaseLoginActivityHelper
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    public static final int mGoogleSignupRequestCode = 200;
    private BaseActivity mBaseActivity;
    public static final String TAG = "GoogleLoginActivityHelper";
    public GoogleLoginActivityHelper(BaseActivity activity) {
        super(activity);
        mBaseActivity = activity;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
         final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mBaseActivity)
                .enableAutoManage(mBaseActivity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        DcidrApplication.getInstance().setGoogleApiClient(googleApiClient);

        Button googleSingInButton = (Button) mBaseActivity.findViewById(R.id.google_sign_in_button);

        googleSingInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleApiClient.connect();
                if (BuildConfig.DEBUG){Log.i(TAG, "[googleSingInButton.Onclik] Creating signin intent for google login");}
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                mBaseActivity.startActivityForResult(signInIntent, mGoogleSignupRequestCode);
            }
        });

    }

    public void populateUserInformation(GoogleSignInResult result) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[populateUserInformation] Populating user info using google login");}
        User user = DcidrApplication.getInstance().getUser();
        GoogleSignInAccount acct = result.getSignInAccount();
        String personName = acct.getDisplayName();
        String personEmail = acct.getEmail();
        user.setLoginType(User.LoginType.GOOGLE);
        setUserCache("loginType", String.valueOf(user.getLoginTypeValue()));
        user.setEmailId(personEmail);
        user.setFirstName(personName);
        user.setLastName(personName);
        setUserCache("emailId", user.getEmailId());
        createUser();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (BuildConfig.DEBUG){Log.e(TAG, "[onConnectionFailed] Google login connect failed");}

        mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.login_error_msg));
    }

    @Override
    public void onConnected(Bundle bundle) {
        //get user credentials
        if (BuildConfig.DEBUG){Log.i(TAG, "[onConnectionFailed] Google login connected");}
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (BuildConfig.DEBUG){Log.e(TAG, "[onConnectionFailed] Google login connection suspended");}
    }

    public void releaseMemory(){
        if (BuildConfig.DEBUG){Log.e(TAG, "[releaseMemory] Releasing memory");}
        super.releaseMemory();
    }
}
