package com.example.turbo.dcidr.android_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.turbo.dcidr.R;

public class SplashActivity extends BaseActivity {

    /**
     * calling super class for basic initialization
     */
    public SplashActivity(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        int delayMilliSecs = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        }, delayMilliSecs);


    }

}
