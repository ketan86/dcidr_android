package com.example.turbo.dcidr.main.activity_helper.login_activity_helper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Turbo on 2/10/2016.
 */
@SuppressWarnings("ConstantConditions")
public class DcidrLoginActivityHelper extends BaseLoginActivityHelper {
    // login page init
    private ImageView mSignupUserProfilePicImageView;
    public static final String TAG = "DcidrLoginActivityHelper";

    /**
     * constructor for dcidrloginactivityhelper
     * @param activity : activity context
     */
    public DcidrLoginActivityHelper(BaseActivity activity){
        super(activity);
        login();
    }

    /**
     * method to handle login. if userId is already in cache then login activity is finished,
     * otherwise it is going to display login activity page.
     */
    private void login(){
        if (BuildConfig.DEBUG){Log.i(TAG, "[login] Dcidr login initialized");}
        final EditText mLoginEmailEditText = (EditText) mBaseActivity.findViewById(R.id.login_email_edit_text);
        final TextView mLoginEmailTextView = (TextView) mBaseActivity.findViewById(R.id.login_email_text_view);
        final EditText mLoginPasswordEditText = (EditText) mBaseActivity.findViewById(R.id.login_password_edit_text);
        final TextView mLoginPasswordTextView = (TextView) mBaseActivity.findViewById(R.id.login_password_text_view);
        final TextView mLoginButtonSelectedMarker = (TextView) mBaseActivity.findViewById(R.id.login_button_selected_marker);
        final TextView mSignupButtonSelectedMarker = (TextView) mBaseActivity.findViewById(R.id.signup_button_selected_marker);
        final RelativeLayout mLoginRelativeLayout = (RelativeLayout) mBaseActivity.findViewById(R.id.login_relative_layout);
        final Button mLoginButton = (Button) mBaseActivity.findViewById(R.id.login_button);
        Button mSignupPageSelector = (Button) mBaseActivity.findViewById(R.id.signup_page_selector);
        Button mLoginPageSelector = (Button) mBaseActivity.findViewById(R.id.login_page_selector);
        //signup page
        final EditText mSignupFirstNameEditText = (EditText) mBaseActivity.findViewById(R.id.signup_first_name_edit_text);
        final EditText mSignupLastNameEditText = (EditText) mBaseActivity.findViewById(R.id.signup_last_name_edit_text);
        final EditText mSignupEmailEditText = (EditText) mBaseActivity.findViewById(R.id.signup_email_edit_text);
        final EditText mSignupPasswordEditText = (EditText) mBaseActivity.findViewById(R.id.signup_password_edit_text);
        final TextView mSignupFirstNameTextView = (TextView) mBaseActivity.findViewById(R.id.signup_first_name_text_view);
        final TextView mSignupLastNameTextView = (TextView) mBaseActivity.findViewById(R.id.signup_last_name_text_view);
        final TextView mSignupEmailTextView = (TextView) mBaseActivity.findViewById(R.id.signup_email_text_view);
        final TextView mSignupPasswordTextView = (TextView) mBaseActivity.findViewById(R.id.signup_password_text_view);
        mSignupUserProfilePicImageView = (ImageView) mBaseActivity.findViewById(R.id.user_profile_pic);
        final Button mSignupButton = (Button) mBaseActivity.findViewById(R.id.signup_button);

        final RelativeLayout mSignupRelativeLayout = (RelativeLayout) mBaseActivity.findViewById(R.id.signup_relative_layout);

        // navigate to signup page if signup button is clicked
        mSignupPageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginButtonSelectedMarker.setVisibility(View.GONE);
                mLoginButton.setVisibility(View.GONE);
                mSignupButton.setVisibility(View.VISIBLE);
                mSignupButtonSelectedMarker.setVisibility(View.VISIBLE);
                mSignupRelativeLayout.setVisibility(View.VISIBLE);
                mLoginRelativeLayout.setVisibility(View.GONE);
            }
        });

        // login page click listener
        mLoginPageSelector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLoginButtonSelectedMarker.setVisibility(View.VISIBLE);
                mSignupButton.setVisibility(View.GONE);
                mLoginButton.setVisibility(View.VISIBLE);
                mSignupButtonSelectedMarker.setVisibility(View.GONE);
                mSignupRelativeLayout.setVisibility(View.GONE);
                mLoginRelativeLayout.setVisibility(View.VISIBLE);
            }
        });

        // show email text when clicked on edit email text
        mLoginEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mLoginEmailTextView.setVisibility(View.INVISIBLE);
                } else {
                    mLoginEmailTextView.setVisibility(View.VISIBLE);
                }
            }

        });
        // show password text when clicked on edit password text
        mLoginPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mLoginPasswordTextView.setVisibility(View.INVISIBLE);
                } else {
                    mLoginPasswordTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        mSignupFirstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    mSignupFirstNameTextView.setVisibility(View.INVISIBLE);
                }else {
                    mSignupFirstNameTextView.setVisibility(View.VISIBLE);
                }
            }

        });

        mSignupLastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    mSignupLastNameTextView.setVisibility(View.INVISIBLE);
                }else {
                    mSignupLastNameTextView.setVisibility(View.VISIBLE);
                }
            }

        });

        // show email text when clicked on edit email text
        mSignupEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mSignupEmailTextView.setVisibility(View.INVISIBLE);
                } else {
                    mSignupEmailTextView.setVisibility(View.VISIBLE);
                }
            }

        });


        // show password text when clicked on edit password text
        mSignupPasswordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mSignupPasswordTextView.setVisibility(View.INVISIBLE);
                } else {
                    mSignupPasswordTextView.setVisibility(View.VISIBLE);
                }
            }
        });


        // authenticate user and navigate to main activity page
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!mLoginEmailEditText.getText().toString().isEmpty()) &&
                        (!mLoginPasswordEditText.getText().toString().isEmpty())) {
                    if (BuildConfig.DEBUG){Log.i(TAG, "[login] Dcidr Login request made");}

                    User user = DcidrApplication.getInstance().getUser();
                    String loginEmailText = mLoginEmailEditText.getText().toString();
                    user.setLoginType(User.LoginType.DCIDR);
                    setUserCache("loginType", String.valueOf(user.getLoginTypeValue()));
                    try {
                        user.setPasswordDigest(Utils.hashString(mLoginPasswordEditText.getText().toString(), "SHA-256"));
                    } catch (NoSuchAlgorithmException e) {
                        mBaseActivity.showAlertDialog(mBaseActivity.getString(R.string.unexpected_error_during_login));
                    } catch (UnsupportedEncodingException e) {
                        mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.login_error_msg));
                    }
                    UserAsyncHttpClient httpClient = new UserAsyncHttpClient(mBaseActivity);
                    try {
                        httpClient.loginUser(loginEmailText, user.getPasswordDigest(), mUserAsyncHttpResponseHandler);
                    } catch (UnsupportedEncodingException e) {
                        mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.login_error_msg));
                    }
                } else {
                    mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.signup_unfilled_info_msg));
                }
            }
        });


        mSignupRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mBaseActivity);
            }
        });

        mSignupUserProfilePicImageView.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!mBaseActivity.hasPermission(Manifest.permission.CAMERA)){
                    if (BuildConfig.DEBUG){Log.i(TAG, "[login] Requesting camera permission");}
                    mBaseActivity.requestPermission(new String[]{Manifest.permission.CAMERA}, mBaseActivity.getResources().getString(R.string.camera_permission_rationale) ,BaseActivity.CAMERA_PERMISSION_REQUEST_CODE);
                }else {
                    // pre-marshmallow releases. No run-time permission so start camera intent.
                    mBaseActivity.launchImageCaptureIntent();
                }
            }
        });
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!mSignupFirstNameEditText.getText().toString().isEmpty()) &&
                        (!mSignupLastNameEditText.getText().toString().isEmpty()) &&
                        (!mSignupEmailEditText.getText().toString().isEmpty()) &&
                        (!mSignupPasswordEditText.getText().toString().isEmpty())) {
                    if (BuildConfig.DEBUG){Log.i(TAG, "[login] Dcidr Signup request made");}

                    User user = DcidrApplication.getInstance().getUser();
                    //user.setUserCreationTime(new Date().getTime());
                    user.setLoginType(User.LoginType.DCIDR);
                    DcidrApplication.getInstance().getUserCache().set("loginType", String.valueOf(user.getLoginTypeValue()));
                    if (mSignupLastNameEditText.getText().length() > 1) {
                        user.setFirstName(mSignupFirstNameEditText.getText().toString());
                        user.setLastName(mSignupLastNameEditText.getText().toString());
                    } else {
                        user.setFirstName(mSignupFirstNameEditText.getText().toString());
                        user.setLastName("");
                    }
                    user.setEmailId(mSignupEmailEditText.getText().toString());
                    try {
                        user.setPasswordDigest(Utils.hashString(mSignupPasswordEditText.getText().toString(), "SHA-256"));
                    } catch (NoSuchAlgorithmException e) {
                        mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.signup_error_msg));
                    } catch (UnsupportedEncodingException e) {
                        mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.signup_error_msg));
                    }
                    DcidrApplication.getInstance().getUserCache().set("emailId", user.getEmailId());
                    if (BuildConfig.DEBUG){Log.i(TAG, "[login] Requesting new user creation");}
                    createUser();
                } else {
                    mBaseActivity.showAlertDialog(mBaseActivity.getResources().getString(R.string.signup_unfilled_info_msg));
                }
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == BaseActivity.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                mBaseActivity.launchImageCaptureIntent();
            } else {
                // Permission Denied
                // nothing to do
            }
        }
        if(requestCode == BaseActivity.STORAGE_READ_WRITE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                setSignupUserProfilePicBitmap(android.net.Uri.parse(mBaseActivity.SD_IMAGE_FILE.toURI().toString()));
            } else {
                // Permission Denied
                // nothing to do
            }
        }
    }

    public void setSignupUserProfilePicBitmap(Uri uri) {
        Bitmap croppedBitmap = Utils.decodeUriAsBitmap(mBaseActivity, uri, mSignupUserProfilePicImageView.getWidth(), mSignupUserProfilePicImageView.getHeight());
        Bitmap bitmap;
        if (croppedBitmap == null) {
            bitmap = Utils.decodeUriAsBitmap(mBaseActivity, android.net.Uri.parse(mBaseActivity.SD_IMAGE_FILE.toURI().toString()),
                    mSignupUserProfilePicImageView.getWidth(), mSignupUserProfilePicImageView.getHeight());
        } else {
            bitmap = Utils.decodeUriAsBitmap(mBaseActivity, android.net.Uri.parse(mBaseActivity.CROPPED_IMAGE_FILE.toURI().toString()),
                    mSignupUserProfilePicImageView.getWidth(), mSignupUserProfilePicImageView.getHeight());
        }

        mSignupUserProfilePicImageView.setImageBitmap(bitmap);
        DcidrApplication.getInstance().getUser().setUserProfilePicBitmap(bitmap);
        DcidrApplication.getInstance().getUser().setUserProfilePicBase64Str(Utils.encodeToBase64(bitmap));
    }


    public void releaseMemory(){
        if (BuildConfig.DEBUG){Log.e(TAG, "[releaseMemory] Releasing memory");}
        super.releaseMemory();
        if(mSignupUserProfilePicImageView != null) {
            mSignupUserProfilePicImageView.setImageBitmap(null);
            mSignupUserProfilePicImageView = null;
        }
    }
}
