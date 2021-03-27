package com.example.turbo.dcidr.android_activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.turbo.dcidr.BuildConfig;
import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper.ContactsFragmentCustomArrayAdapter;
import com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper.DcidrContactsFragmentHelper;
import com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper.FacebookContactsFragmentHelper;
import com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper.GoogleContactsFragmentHelper;
import com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper.PhoneContactsFragmentHelper;
import com.example.turbo.dcidr.main.container.ContactContainer;
import com.example.turbo.dcidr.main.user.Contact;
import com.example.turbo.dcidr.main.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Borat on 5/21/2016.
 */
public class ContactsFragment extends Fragment {
    private BaseActivity mBaseActivity;
    private ListView mContactsListView;
    private View mContactsView;
    private ContactsFragmentCustomArrayAdapter mContactsCustomArrayAdapter;
    private ProgressDialog mProgressDialog;
    private DcidrContactsFragmentHelper mDcidrContactsFragmentHelper;
    private GoogleContactsFragmentHelper mGoogleContactsFragmentHelper;
    private FacebookContactsFragmentHelper mFacebookContactsFragmentHelper;
    private PhoneContactsFragmentHelper mPhoneContactsFragmentHelper;
    private ContactContainer mContactContainer;
    public static final String TAG = "ContactsFragment";


    public void onRequestPermissionsResult(int requestCode,
                                                 String permissions[], int[] grantResults) {
        switch (requestCode) {
            case BaseActivity.READ_PHONE_CONTACTS_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mPhoneContactsFragmentHelper.fetchContacts(0, 5);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public BaseActivity getBaseActivity()
    {
        return mBaseActivity;
    }

    //Called by HTTP based onFetch
    public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
        if (statusCode == 200) {
            try {
                JSONArray jsonArray = new JSONObject(new String(response)).getJSONArray("result");
                mContactContainer.populateMe(jsonArray, Contact.ContactType.DCIDR);
                //Now populate phone contacts
                mContactContainer.refreshContactList();
                mContactsCustomArrayAdapter.notifyDataSetChanged();
                mBaseActivity.dismissProgressDialog(mProgressDialog);
                // let the GC claim the progress bar memeory
                mProgressDialog = null;
            } catch (Exception e) {
                if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchSuccess] Exception:" + e.toString());}
            }
        }
    }

    public ContactsFragmentCustomArrayAdapter getContactsFragmentCustomArrayAdapter(){
        return mContactsCustomArrayAdapter;
    }


    public ContactContainer getContactContainer() {
        return mContactContainer;
    }


    //Called by Phone local fetch
    public void updateContactContainerPhone(String email, String firstName, String lastName, Contact.StatusType statusType, Contact.ContactType contactType) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[updateContactContainerPhone] Populating phone contacts");}
        mContactContainer.populateMePhone(email, firstName, lastName, statusType, contactType);
    }
    //Called by Phone local fetch
    public void updateContactContainer(String email, String firstName, String lastName, Contact.StatusType statusType, Bitmap imageBitMap,
                                       Contact.ContactType contactType) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[updateContactContainer] Populating phone contacts");}
        mContactContainer.populateMe(email, firstName, lastName, statusType,
                imageBitMap, contactType);
    }

    public void refreshContactList() {
        mContactContainer.refreshContactList();
        mContactsCustomArrayAdapter.notifyDataSetChanged();
    }

    public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchFailure] Failure getting dcidr contacts");}
        JSONObject jsonObject = null;
        String errorString = null;
        try {
            jsonObject = new JSONObject(new String(errorResponse));
            errorString = (String) jsonObject.get("error");
            if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchFailure] Error: " + errorString) ;}
        } catch (JSONException error) {
            if (BuildConfig.DEBUG){Log.e(TAG, "[onFetchFailure] JSONException : "+ error.toString());}
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[onCreateView] Creating contact fragment");}
        if (mContactsView == null) {
            mContactsView = inflater.inflate(R.layout.fragment_contacts, container, false);
            if (mBaseActivity != null) {
                // create progress bar
                //mProgressDialog = mBaseActivity.getAndShowProgressDialog(mBaseActivity, getResources().getString(R.string.loading_msg));
                mContactsListView = (ListView) mContactsView.findViewById(R.id.contacts_list_view);
                mContactsCustomArrayAdapter = new ContactsFragmentCustomArrayAdapter(this, mBaseActivity, R.id.contacts_list_view, mContactContainer.getContactList(), mBaseActivity);
                mContactsListView.setAdapter(mContactsCustomArrayAdapter);
                mContactsListView.setOnScrollListener(mContactsListViewOnScrollListener);
                // create contacts fragment helper and initialize contacts
                mDcidrContactsFragmentHelper = new DcidrContactsFragmentHelper(this);
                mDcidrContactsFragmentHelper.fetchContacts(0, 5);  //in response will call the fetch method
                mPhoneContactsFragmentHelper = new PhoneContactsFragmentHelper(this);
                mPhoneContactsFragmentHelper.fetchContacts(0, 5);
                //find out the primary gmail account for the android device
                String loginTypeStr = DcidrApplication.getInstance().getUserCache().get("loginType");
                //Log.i("Kanishka", "logintypeStr=" + loginTypeStr);
                User.LoginType loginType = User.LoginType.values()[Integer.parseInt(loginTypeStr)- 1];
                String userEmail = DcidrApplication.getInstance().getUserCache().get("emailId");

                if (loginType == User.LoginType.GOOGLE) {
                    if (userEmail != null) {
                        fetchGoogleContacts(userEmail);
                    }
                } else if (loginType == User.LoginType.FACEBOOK) {
                    if (userEmail != null) {
                        fetchFacebookContacts(userEmail);
                    }
                }
            }else {
                if (BuildConfig.DEBUG){Log.e(TAG, "[onCreateView] BaseActivity is null");}
            }
        }else{
            if (BuildConfig.DEBUG){Log.e(TAG, "[onCreateView] contactsView is null");}

        }
        return mContactsView;
    }

    public void fetchGoogleContacts(String userEmail) {
        if (BuildConfig.DEBUG){Log.i(TAG, "[fetchGoogleContacts] Fetching google contacts");}
        AccountManager manager = (AccountManager) mBaseActivity.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String primaryGoogleAccount = new String("");
        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                primaryGoogleAccount = account.name;
                break;
            }
        }
        if (primaryGoogleAccount.equals(userEmail)) {
            mGoogleContactsFragmentHelper = new GoogleContactsFragmentHelper(this);
            mGoogleContactsFragmentHelper.fetchContacts(0, 5);
        }
    }

    public void fetchFacebookContacts(String userEmail) {
        mFacebookContactsFragmentHelper = new FacebookContactsFragmentHelper(this);
        mFacebookContactsFragmentHelper.fetchContacts(0, 5);
    }

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContactContainer = DcidrApplication.getInstance().getGlobalContactContainer();
    }

    private ListView.OnItemClickListener mGroupItemClickListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User user = mContactsCustomArrayAdapter.getItem(position);
            Intent selectedGroupEventActivity = new Intent(mBaseActivity, SelectedGroupEventActivity.class);
            //TODO: figure out what is to be done when a user is clicked. perhaps show more information
            //selectedGroupEventActivity.putExtra(getString(R.string.selected_group_id), group.getGroupIdStr());
            //startActivity(selectedGroupEventActivity);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseActivity = (BaseActivity) context;
    }

    /**
     * onScrollListener view listener for Contacts list view
     * 1.Setting member on click listener here
     */
    private ListView.OnScrollListener mContactsListViewOnScrollListener = new ListView.OnScrollListener(){

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mContactsCustomArrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem != 0 && visibleItemCount != 0) {
                if (mDcidrContactsFragmentHelper !=null) {
                    mDcidrContactsFragmentHelper.fetchContacts(firstVisibleItem, firstVisibleItem + visibleItemCount);
                }
                if (mPhoneContactsFragmentHelper != null) {
                    mPhoneContactsFragmentHelper.fetchContacts(firstVisibleItem, firstVisibleItem + visibleItemCount);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mContactsCustomArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactContainer.clear();
        mProgressDialog = null;
    }
}
