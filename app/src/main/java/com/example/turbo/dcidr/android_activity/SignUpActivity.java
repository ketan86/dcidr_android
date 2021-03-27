package com.example.turbo.dcidr.android_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends BaseActivity {

    private EditText mSignupUserNameEditText;
    private EditText mSignupEmailEditText;
    private EditText mSignupPasswordEditText;
    private RelativeLayout mSignupRelativeLayout;
    private Button mSignUpButton;

    /**
     * calling super class for basic initialization
     */
    public SignUpActivity(){
        super();
    }

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
    public void initActivity(){
        setContentView(R.layout.activity_signup);
        mSignupUserNameEditText = (EditText) findViewById(R.id.signup_user_name_edit_text);
        mSignupEmailEditText = (EditText) findViewById(R.id.signup_email_edit_text);
        mSignupPasswordEditText = (EditText) findViewById(R.id.signup_password_edit_text);
        mSignUpButton = (Button) findViewById(R.id.signup_button);
        // this is the layout which encloses all other views.
        // we are making this layout clickable from xml (prob not needed, works without that too)
        // and registering a onclicklistener on it to hide keyboard.
        mSignupRelativeLayout = (RelativeLayout) findViewById(R.id.signup_relative_layout);
        mSignupRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(SignUpActivity.this);
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!mSignupUserNameEditText.getText().toString().isEmpty()) &&
                        (!mSignupEmailEditText.getText().toString().isEmpty()) &&
                        (!mSignupPasswordEditText.getText().toString().isEmpty())) {
                    User user = DcidrApplication.getInstance().getUser();
                    //user.setUserCreationTime(new Date().getTime());
                    user.setLoginType(User.LoginType.DCIDR);
                    DcidrApplication.getInstance().getUserCache().set("loginType", String.valueOf(user.getLoginTypeValue()));

                    if (mSignupUserNameEditText.getText().toString().split(" ").length > 1) {
                        user.setFirstName(mSignupUserNameEditText.getText().toString().split(" ")[0]);
                        user.setLastName(mSignupUserNameEditText.getText().toString().split(" ")[1]);
                    } else {
                        user.setFirstName(mSignupUserNameEditText.getText().toString().split(" ")[0]);
                        user.setLastName("");
                    }
                    user.setEmailId(mSignupEmailEditText.getText().toString());
                    try {
                        user.setPasswordDigest(Utils.hashString(mSignupPasswordEditText.getText().toString(), "SHA-256"));
                    } catch (NoSuchAlgorithmException e) {
                        showAlertDialog(getResources().getString(R.string.signup_error_msg));
                    } catch (UnsupportedEncodingException e) {
                        showAlertDialog(getResources().getString(R.string.signup_error_msg));
                    }
                    DcidrApplication.getInstance().getUserCache().set("emailId", user.getEmailId());
                    Intent returnIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    showAlertDialog(getString(R.string.signup_unfilled_info_msg));
                }
            }
        });
    }
}
