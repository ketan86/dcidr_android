package com.example.turbo.dcidr.android_activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.container.GroupContainer;

/**
 * Created by Turbo on 2/27/2016.
 */
public class NotificationPopupDialog extends Activity {

    private Button notificationOkButton;
    private Button notificationGotoActivityButton;
    private String mMessage;
    private String mSource;
    private GroupContainer mGroupContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_popup_dialog);
        mMessage = getIntent().getStringExtra("message");
        mSource = getIntent().getStringExtra("source");
        notificationOkButton = (Button) findViewById(R.id.notification_ok_button);
        notificationGotoActivityButton = (Button) findViewById(R.id.notification_goto_event_button);
        // setSharedPreferences by passing activity context.
        DcidrApplication.getInstance().getUserCache().setSharedPreferences(this);
        // get userId from cache
        final String userId = DcidrApplication.getInstance().getUserCache().get("id");

        notificationOkButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId == null){ finish();}
                Intent intent = new Intent("MainActivityGroupNotificationAction");
                // You can also include some extra data.
                intent.putExtra("source", mSource);
                intent.putExtra("message", mMessage);
                LocalBroadcastManager.getInstance(NotificationPopupDialog.this).sendBroadcast(intent);
                finish();
            }
        });

        notificationGotoActivityButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId == null){
                    launchLoginActivity();
                } else {
                    Intent intent = new Intent(NotificationPopupDialog.this, MainActivity.class);
                    // This make the magic
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //add some data extra
                    intent.putExtra("source", mSource);
                    intent.putExtra("message", mMessage);
                    getApplicationContext().startActivity(intent);
                }
            }
        });
    }

    /**
     * launch login activity
     */
    public void launchLoginActivity(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * show dialog method
     * @param msg : msg to show with ok button only
     */
    public void showAlertDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .create().show();
    }

}
