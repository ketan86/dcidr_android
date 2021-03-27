package com.example.turbo.dcidr.main.activity_helper.login_activity_helper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.user.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Turbo on 2/10/2016.
 */
public class FacebookLoginActivityHelper extends BaseLoginActivityHelper implements FacebookSdk.InitializeCallback {
    private CallbackManager mFacebookCallbackManager;
    public static final String TAG = "FacebookLoginActivityHelper";

    /**
     * constructor of facebook login activity helper class. It is used by login activity
     * @param activity : activity context
     */
    public FacebookLoginActivityHelper(BaseActivity activity){
        super(activity);
        FacebookSdk.sdkInitialize(activity.getApplicationContext(), 0, this);
        }

    /**
     * facebook callback manager 
     * @return CallbackManager for facebook
     */
    public CallbackManager getFacebookCallbackManager(){
        return mFacebookCallbackManager;
    }

    /**
     * method called with facebook sdk is initialized
     */
    @Override
    public void onInitialized() {

        if (BuildConfig.DEBUG){Log.i(TAG, "[onInitialized] Initializing facebook login");}

        // create a callback for facebook
        mFacebookCallbackManager = CallbackManager.Factory.create();
        // once facebook sdk is initialized, assign login action to facebook signin button
        Button facebookLoginButton = (Button) mBaseActivity.findViewById(R.id.facebook_sign_in_button);
        // set permission to access public_profile and email. there are other permissions that
        // can be set too.
        if (facebookLoginButton != null) {
            facebookLoginButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    LoginManager.getInstance().logInWithReadPermissions(mBaseActivity, Arrays.asList("public_profile, email, user_friends"));
                }
            });
        }

        // Callback registration
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (BuildConfig.DEBUG){Log.i(TAG, "[onInitialized.onSuccess] Success in facebook login");}
                // onSuccess, get access token
                AccessToken a = loginResult.getAccessToken();
                DcidrApplication.getInstance().setFbAccessToken(a);
                // use graph api and get user info
                GraphRequest graphRequest =  GraphRequest.newMeRequest(a, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            User user = DcidrApplication.getInstance().getUser();
                            user.setLoginType(User.LoginType.FACEBOOK);
                            user.setEmailId(object.getString("email"));
                            user.setFirstName(object.getString("first_name"));
                            user.setLastName(object.getString("last_name"));
                            //user.setLoginTime(new Date().getTime());
                            setUserCache("emailId", user.getEmailId());
                            setUserCache("loginType", String.valueOf(user.getLoginTypeValue()));
                            createUser();
                        } catch (JSONException e) {
                            if (BuildConfig.DEBUG){Log.e(TAG, "[onInitialized.onSuccess] JSONException in facebook login");}
                            mBaseActivity.showAlertDialog(mBaseActivity.getString(R.string.facebook_login_oncompleted_error_msg));
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name, last_name, email,gender, birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                if (BuildConfig.DEBUG){Log.e(TAG, "[onInitialized.onCancel] Facebook login canceled");}
                mBaseActivity.showAlertDialog(mBaseActivity.getString(R.string.facebook_login_oncompleted_error_msg));
            }

            @Override
            public void onError(FacebookException exception) {
                if (BuildConfig.DEBUG){Log.e(TAG, "[onInitialized.onError] Error in facebook login");}
                mBaseActivity.showAlertDialog(mBaseActivity.getString(R.string.facebook_login_error_msg));
            }
        });

    }

    public void releaseMemory(){
        if (BuildConfig.DEBUG){Log.e(TAG, "[releaseMemory] Releasing memory");}
        super.releaseMemory();
        mFacebookCallbackManager = null;
    }

}

