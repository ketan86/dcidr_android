//package com.example.turbo.dcidr.android_activity;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//
//import com.example.turbo.dcidr.R;
//import com.example.turbo.dcidr.android_activity.BaseActivity;
//import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
//import com.example.turbo.dcidr.main.user.User;
//import com.example.turbo.dcidr.global.GlobalStateSingleton;
//import com.facebook.AccessToken;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.Profile;
//import com.facebook.ProfileTracker;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//
//import java.io.UnsupportedEncodingException;
//
//import cz.msebera.android.httpclient.Header;
//
//
//public class LoginActivity extends BaseActivity {
//    private LoginButton facebookLoginButton;
//    private CallbackManager callbackManager;
//    private Bitmap mProfileImage;
//    private ImageView mFacebookProfilePic;
//    private ProfileTracker mProfileTracker;
//    private User mUser = GlobalStateSingleton.getInstance().getUser();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext(), 0, new InitializerCallbackClass());
//        //handleDcidrLogin();
//    }
//
//    private void handleDcidrLogin(){
//        final EditText dcidrEmailEditText = (EditText) findViewById(R.id.dcidr_email_edit_text);
//        final EditText dcidrPasswordEditText = (EditText) findViewById(R.id.dcidr_password_edit_text);
//        Button dcidrLoginButton = (Button) findViewById(R.id.dcidr_login_button);
//        Button dcidrSignUpButton = (Button) findViewById(R.id.dcidr_signup_button);
//
//        dcidrSignUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent signUpActivityIntent = new Intent(LoginActivity.this, SignUpActivity.class);
//                startActivity(signUpActivityIntent);
//            }
//        });
//
//        dcidrLoginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((dcidrEmailEditText.getText().toString() != "") &&
//                        (dcidrPasswordEditText.getText().toString() != "")) {
//                    // rest api call to authenticate user
//                }
//            }
//        });
//
//    }
//    private class InitializerCallbackClass implements FacebookSdk.InitializeCallback {
//        @Override
//        public void onInitialized()
//        {
//            if (AccessToken.getCurrentAccessToken() != null)
//            {
//                finish();
//            }
//
//
//
//            mUser.setLoginType(User.LoginType.FACEBOOK);
//
//            callbackManager = CallbackManager.Factory.create();
//            // Initialize the SDK before executing any other operations,
//            // especially, if you're using Facebook UI elements.
//            setContentView(R.layout.activity_login);
//
//
//            final EditText dcidrEmailEditText = (EditText) findViewById(R.id.dcidr_email_edit_text);
//            final EditText dcidrPasswordEditText = (EditText) findViewById(R.id.dcidr_password_edit_text);
//            Button dcidrLoginButton = (Button) findViewById(R.id.dcidr_login_button);
//            Button dcidrSignUpButton = (Button) findViewById(R.id.dcidr_signup_button);
//
//            dcidrSignUpButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent signUpActivityIntent = new Intent(LoginActivity.this,SignUpActivity.class);
//                    startActivity(signUpActivityIntent);
//                }
//            });
//
//            dcidrLoginButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if ((dcidrEmailEditText.getText().toString() != "") &&
//                            (dcidrPasswordEditText.getText().toString() != "")) {
//                        // rest api call to authenticate user
//                    }
//                }
//            });
//
//
//
//            facebookLoginButton = (LoginButton) findViewById(R.id.login_button);
//            Log.i("Ketan", facebookLoginButton.getText().toString());
//
//            //mFacebookProfilePic = (ImageView) findViewById(R.id.facebook_profile_pic);
//            facebookLoginButton.setReadPermissions("user_friends");
//            // Other app specific specialization
//
//            // Callback registration
//            facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                @Override
//                public void onSuccess(LoginResult loginResult) {
//                    // App code
//                    Log.i("Ketan:Success", String.valueOf(loginResult.toString()));
//                    AccessToken a = loginResult.getAccessToken();
//                    Log.i("Ketan:Success", String.valueOf(a.getToken()));
//                    if(Profile.getCurrentProfile() == null) {
//                        mProfileTracker = new ProfileTracker() {
//                            @Override
//                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                                if (currentProfile != null) {
//                                    setUser(currentProfile);
//                                    mProfileTracker.stopTracking();
//                                }
//                            }
//                        };
//                        mProfileTracker.startTracking();
//                    } else {
//                        setUser(Profile.getCurrentProfile());
//                    }
//                    finish();
//
//                }
//
//                @Override
//                public void onCancel() {
//                    // App code
//                    Log.i("Ketan:Cancel", "Cancelled");
//                }
//
//                @Override
//                public void onError(FacebookException exception) {
//                    // App code
//                    Log.i("Ketan:Error", String.valueOf(exception.toString()));
//                }
//            });
//
//        }
//
//    }
//
//    private void setUser(Profile currentProfile){
//        mUser.setUserId(currentProfile.getId());
//        mUser.setFirstName(currentProfile.getFirstName());
//        mUser.setLastName(currentProfile.getLastName());
//        mUser.setLoginTime();
//        UserAsyncHttpClient userAsyncHttpClient = new UserAsyncHttpClient(getApplicationContext());
//        try {
//            userAsyncHttpClient.setUser(mUser, userAsyncHttpResponseHandler);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        //getFacebookProfilePicture(currentProfile.getId());
//        //mFacebookProfilePic.setImageBitmap(mProfileImage);
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    private AsyncHttpResponseHandler userAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {
//
//        @Override
//        public void onStart() {
//            // called before request is started
//        }
//
//        @Override
//        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//            // called when response HTTP status is "200 OK"
//        }
//
//        @Override
//        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//        }
//
//        @Override
//        public void onRetry(int retryNo) {
//            // called when request is retried
//        }
//    };
//
////    public void getFacebookProfilePicture(String userID){
////        new DownloadFilesTask().execute(userID);
////    }
////
////    private class DownloadFilesTask extends AsyncTask<String, Integer, Bitmap> {
////        protected Bitmap doInBackground(String... params) {
////            try {
////                URL imageURL = new URL("https://graph.facebook.com/" + params[0] + "/picture?type=large");
////                Log.i("Ketan",String.valueOf(imageURL));
////                Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
////                return bitmap;
////            } catch (IOException e) {
////                throw new RuntimeException(e);
////            }
////        }
////
////        protected void onProgressUpdate(Integer... progress) {
////            //
////        }
////
////        protected void onPostExecute(Bitmap result) {
////            mProfileImage = result;
////        }
////    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//}
