package com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper;

import android.util.Log;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.ContactsFragment;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.FetchManager;
import com.example.turbo.dcidr.utils.common_utils.SmartSearch;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Borat on 6/11/2016.
 */
public class GoogleContactsFragmentHelper extends BaseContactsFragmentHelper {
    private SmartSearch mSmartSearch;
    private SmartSearch.OnFinishCallback mGetDataForSearchFinishCallback;
    private ContactsFragment mContactsFragment;
    private UserAsyncHttpClient mContactsAsyncHttpClient;
    private FetchManager mFetchManager;
    private String mUserIdStr;
    private BaseActivity mBaseActivity;

    public GoogleContactsFragmentHelper(ContactsFragment contactsFragment) {
        super(contactsFragment);
        mContactsFragment = contactsFragment;
        mFetchManager = new FetchManager(5,10);
    }

    private AsyncHttpResponseHandler mGetContactsAsyncHttpResponseHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onStart() {
            // called before request is started
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            mContactsFragment.onFetchSuccess(statusCode,headers,responseBody);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
            mContactsFragment.onFetchFailure(statusCode, headers, errorResponse, e);
        }

        @Override
        public void onRetry(int retryNo) {
            // called when request is retried
            Log.e("Retry", "Retry");
        }
    };

    /**
     * fetchcontacts
     */
    public void fetchContacts(int visibleStartIndex, int visibleEndIndex) {
        mFetchManager.fetch(visibleStartIndex, visibleEndIndex, new FetchManager.FetchInterface(){
            @Override
            public void onFetch(int offset, int limit) {
                if (mUserIdStr != null) {
                    //mContactsAsyncHttpClient.getContacts(mUserIdStr, offset, limit, mGetContactsAsyncHttpResponseHandler);
                    //Kanishka: TODO, fetch google contacts
                    //https://developers.google.com/google-apps/contacts/v3/#retrieving_all_contacts
                    //https://tush.wordpress.com/2014/07/15/android-google-contact-api-3-0-example/
                } else {
                    //// TODO: 2/14/2016
                    Log.i("Ketan", "User id is null");
                }
            }

        });
    }
}
