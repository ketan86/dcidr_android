package com.example.turbo.dcidr.android_activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.login_activity_helper.DcidrLoginActivityHelper;
import com.example.turbo.dcidr.main.activity_helper.login_activity_helper.FacebookLoginActivityHelper;
import com.example.turbo.dcidr.main.activity_helper.login_activity_helper.GoogleLoginActivityHelper;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;


/**
 * Login activity to handle different kinds of login. It instantiate different kinds of login
 * objects and handles login based on selected login type using login button
 */
public class LoginActivity extends BaseActivity {
    private FacebookLoginActivityHelper mFacebookLoginActivityHelper;
    private DcidrLoginActivityHelper mDcidrLoginActivityHelper;
    private GoogleLoginActivityHelper mGoogleLoginActivityHelper;
    public static final String TAG = "LoginActivity";
    /**
     * calling super class for basic initialization
     */
    public LoginActivity(){
        super();
    }

    /**
     * OnCreate method where login activity helpers are instantiated
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // make sure user is populated before proceeding, refer to base activity onCreate
        setUserFetchListener(new UserFetchListener() {
            @Override
            public void onFetchDone() {
                initActivity();
            }
        });
        super.onCreate(savedInstanceState);
    }

    public void initActivity() {
        // setSharedPreferences by passing activity context.
        DcidrApplication.getInstance().getUserCache().setSharedPreferences(this);
        // get userId from cache
        String userId = DcidrApplication.getInstance().getUserCache().get("userId");
        if (BuildConfig.DEBUG){Log.i(TAG, "[onCreate] userId: " + userId);}

        // get deviceId and set it in cache
        String deviceId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DcidrApplication.getInstance().getUserCache().set("deviceId", deviceId);
        if (BuildConfig.DEBUG){Log.i(TAG, "[onCreate] deviceId: " + deviceId);}

        if (userId != null){
            if (BuildConfig.DEBUG){Log.i(TAG, "[onCreate] Allowing to proceed to MainActivity");}
            // finish login activity and go to main activity
            Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainActivityIntent.putExtra("SOURCE", getString(R.string.login_activity_class_name));
            startActivity(mainActivityIntent);
            finish();
        } else {
            // set login activity login
            setContentView(R.layout.activity_login);

            // init facebook, dcidr and google login
            mFacebookLoginActivityHelper = new FacebookLoginActivityHelper(this);
            mDcidrLoginActivityHelper = new DcidrLoginActivityHelper(this);
            mGoogleLoginActivityHelper = new GoogleLoginActivityHelper(this);


            // this is the layout which encloses all other views.
            // we are making this layout clickable from xml (prob not needed, works without that too)
            // and registering a onclicklistener on it to hide keyboard.
            RelativeLayout loginRelativeLayout = (RelativeLayout) findViewById(R.id.login_relative_layout);
            loginRelativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeyboard(LoginActivity.this);
                }
            });
        }
    }

    /**
     * onActivityResult method handles results returned by activities being called from login activity
     * @param requestCode request code used while starting an activity
     * @param resultCode request code returned by an activity
     * @param data data returned by an activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BaseActivity.CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                launchCropImageIntent(SD_IMAGE_FILE, CROPPED_IMAGE_FILE);
            }
        }
        if (requestCode == BaseActivity.CROP_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if(!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || !hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, getResources().getString(R.string.external_storage_permission_rationale) ,BaseActivity.STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE);
                }else {
                    mDcidrLoginActivityHelper.setSignupUserProfilePicBitmap(uri);
                }
            }
        }
        //get the facebook login activity result. In case this was not invoked, the login activity would be null?
        mFacebookLoginActivityHelper.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);

        //If the login was done via google login.
        if (requestCode == GoogleLoginActivityHelper.mGoogleSignupRequestCode && resultCode == RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //GoogleSignInAccount acct = result.getSignInAccount();
            mGoogleLoginActivityHelper.populateUserInformation(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDcidrLoginActivityHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        if (BuildConfig.DEBUG){Log.e(TAG, "[releaseMemory] Releasing memory");}
        super.onDestroy();
        if(mDcidrLoginActivityHelper != null) {
            mDcidrLoginActivityHelper.releaseMemory();
        }
        if(mFacebookLoginActivityHelper != null) {
            mFacebookLoginActivityHelper.releaseMemory();
        }
        if(mGoogleLoginActivityHelper != null) {
            mGoogleLoginActivityHelper.releaseMemory();
        }
    }
}
