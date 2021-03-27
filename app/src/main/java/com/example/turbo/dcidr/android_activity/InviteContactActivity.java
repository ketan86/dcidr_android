package com.example.turbo.dcidr.android_activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.user.Contact;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Borat on 4/9/2016.
 */
public class InviteContactActivity extends  BaseActivity {
    private RelativeLayout mCreateContactRelativeLayout;
    private UserAsyncHttpClient mInviteAsFriendAsyncHttpClient;
    private String mUserIdStr;

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
        mInviteAsFriendAsyncHttpClient = new UserAsyncHttpClient(getApplicationContext());
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
        setContentView(R.layout.activity_contact_invite);

        // set app toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCreateContactActivity();

    }

    /**
     * initialize create group activity
     */
    public void initCreateContactActivity() {
        mCreateContactRelativeLayout = (RelativeLayout) findViewById(R.id.invite_contact_relative_layout);
        Button inviteButton = (Button) mCreateContactRelativeLayout.findViewById(R.id.contact_invite_button);
        final TextView textView = (TextView) mCreateContactRelativeLayout.findViewById(R.id.contact_email_edit_text);
        inviteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InviteContactActivity.this);
                builder.setMessage("Send invitation to " + textView.getText() + " ?")
                        .setPositiveButton(R.string.positive_button_msg, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //yes will invoke a server API to send out an email
                                String userIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
                                Contact contact;
                                if (DcidrApplication.getInstance().getGlobalContactContainer().getContactMap().get(textView.getText().toString()) != null ) {
                                    contact = DcidrApplication.getInstance().getGlobalContactContainer().getContactMap().get(textView.getText().toString());
                                } else {
                                    contact = new Contact(InviteContactActivity.this, Contact.ContactType.DCIDR);
                                    contact.setEmailId(textView.getText().toString());
                                }
                                mInviteContactAsyncHttpResponseHandler.mContact = contact;
                                mInviteAsFriendAsyncHttpClient.inviteContact(userIdStr, contact.getEmailId(), mInviteContactAsyncHttpResponseHandler);
                            }
                        })
                        .setNegativeButton(R.string.negative_button_msg, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //no will do nothing , just close the dialog
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public abstract class CustomAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
        public Contact mContact;
    }

    protected CustomAsyncHttpResponseHandler mInviteContactAsyncHttpResponseHandler = new  CustomAsyncHttpResponseHandler() {

        @Override
        public void onStart() {
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
            if (statusCode == 200) {
                JSONObject jsonObject = null;
                try {
                    if(mContact.getStatusType() != Contact.StatusType.FRIEND) {
                        mContact.setStatusType(Contact.StatusType.INVITED); //set status to invited
                    }
                    jsonObject = new JSONObject(new String(response));
                    String result = jsonObject.getString("result");
                    showAlertDialog(result);

                } catch (JSONException e) {
                    showAlertDialog("Unexpected Error occurred: " + e.toString());
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            JSONObject jsonObject = null;
            String errorString = null;
            try {
                jsonObject = new JSONObject(new String(errorResponse));
                errorString = (String) jsonObject.get("error");
            } catch (JSONException error) {
                Toast toast = Toast.makeText(InviteContactActivity.this, R.string.invite_contact_error_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
            showAlertDialog(errorString);
        }
        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
        }
    };
}

