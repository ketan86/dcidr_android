package com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper;

import android.util.Log;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.ContactsFragment;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.fetch_manager.AsyncHttpFetchManager;
import com.example.turbo.dcidr.main.user.Contact;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Borat on 6/11/2016.
 */
public class DcidrContactsFragmentHelper extends BaseContactsFragmentHelper{

    private ContactsFragment mContactsFragment;
    private UserAsyncHttpClient mContactsAsyncHttpClient;
    private AsyncHttpFetchManager mAsyncHttpFetchManager;
    private String mUserIdStr;
    private BaseActivity mBaseActivity;

    public DcidrContactsFragmentHelper(ContactsFragment contactsFragment) {
        super(contactsFragment);
        this.mContactsFragment = contactsFragment;
        this.mBaseActivity = contactsFragment.getBaseActivity();
        mContactsAsyncHttpClient = new UserAsyncHttpClient(this.mBaseActivity.getApplicationContext());
        mAsyncHttpFetchManager = new AsyncHttpFetchManager(mBaseActivity, 5, 10);
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
        //get the user id's login type, based on which we will show contacts

    }


    /**
     * initialize Contacts from dcidr server
     */
    public void fetchContacts(int visibleStartIndex, int visibleEndIndex) {
        mAsyncHttpFetchManager.fetch(visibleStartIndex, visibleEndIndex, new AsyncHttpFetchManager.AsyncHttpFetchManagerInterface() {
            @Override
            public void onFetchRequested(int offset, int limit) {
                Log.i("Kanishka", "fetchContactsoffset = " + offset + ", limit = " + limit);
                mContactsAsyncHttpClient.getDirectAndIndirectContacts(mUserIdStr, offset, limit, mAsyncHttpFetchManager.getAsyncHttpResponseHandler());
            }

            @Override
            public int postFetchSetEndIndex() {
                return mContactsFragment.getContactContainer().getContactCount(Contact.ContactType.DCIDR);
            }

            @Override
            public void onFetchStart() {

            }

            @Override
            public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
                mContactsFragment.onFetchSuccess(statusCode, headers, response);
            }

            @Override
            public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                mContactsFragment.onFetchFailure(statusCode, headers, errorResponse, e);

            }
        });
    }
}
