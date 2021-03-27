package com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.ContactsFragment;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.activity_helper.FetchManager;
import com.example.turbo.dcidr.main.user.Contact;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by Borat on 6/11/2016.
 */
public class PhoneContactsFragmentHelper extends BaseContactsFragmentHelper {

    private ContactsFragment mContactsFragment;
    private UserAsyncHttpClient mContactsAsyncHttpClient;
    private FetchManager mFetchManager;
    private int mOffset, mLimit;
    private HashMap<String, String> mValidPhoneContactEntriesMap;

    private ContactProfPicFetchInterface mContactsProfPicFetchInterface;

    public interface ContactProfPicFetchInterface {
        void onImageFetchDone(Bitmap bitmap);
    }

    public void onContactProfPicFetchDoneListener(ContactProfPicFetchInterface contactProfPicFetchInterface){
        this.mContactsProfPicFetchInterface = contactProfPicFetchInterface;
    }

    public PhoneContactsFragmentHelper(ContactsFragment contactsFragment) {
        super(contactsFragment);
        mContactsFragment = contactsFragment;
        mFetchManager = new FetchManager(5,10);
        mValidPhoneContactEntriesMap = new HashMap<String,String>();
    }

    //AsyncQueryHandler wraps the ContentResolver object
    public interface OnQueryCompleteListener {
        public void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    class MyAsyncQueryHandler extends AsyncQueryHandler{
        private WeakReference<OnQueryCompleteListener> mListener;

        public MyAsyncQueryHandler(ContentResolver cr, OnQueryCompleteListener listener) {
            super(cr);
            this.mListener = new WeakReference<OnQueryCompleteListener>(listener);
        }

        //Override the onQueryComplete method defined in AsyncQueryHandler
        @Override
        public void onQueryComplete(int token, Object cookie, Cursor cursor) {
            final OnQueryCompleteListener onQueryCompleteListener = mListener.get();
            if (onQueryCompleteListener != null) {
                onQueryCompleteListener.onQueryComplete(token, cookie, cursor);
            } else {
                cursor.close();
            }
        }
    }

    private void getPhotoUriFromEmailAsync(final String email, final String firstName,
                                           final String lastName, final Contact.StatusType statusType,
                                           Contact.ContactType contactType) {
        //Log.i("Kanishka", "PhoneContactsfragHelperFunc: now going to fetch picture async for email = " + email);
        MyAsyncQueryHandler queryHandler = new MyAsyncQueryHandler(mContactsFragment.getBaseActivity().getContentResolver(), new OnQueryCompleteListener() {
            public void onQueryComplete(int token, Object cookie, Cursor phones) {
                if (phones != null) {
                    //Log.i("Kanishka", "PhoneContactsfragHelperFunc: onQueryComplete for email = " + email);

                    if (!phones.moveToFirst()) {
                        return;
                    }

                    String image_uri = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                    if (image_uri != null) {
                        String Name = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String email = phones
                                .getString(phones
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContactsFragment.getBaseActivity().getContentResolver(), Uri.parse(image_uri));
                            mContactsFragment.updateContactContainer(email, firstName, lastName,
                                    statusType, bitmap, Contact.ContactType.PHONE);
                            //Log.i("Kanishka", "Loading picture for contact firstName:" + firstName + ", LastName = "+ lastName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    phones.close();
                }
// else {
//                    Log.i("Kanishka", "PhoneContactsfragHelperFunc: onQueryComplete for email = " + email + ", but cursor is NULL");
//                }
            }
        });

        final String[] projection = { ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        String likeStr = "%"+email+"%";
        queryHandler.startQuery(1, null,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Email.ADDRESS + " LIKE ?", new String[]{likeStr}, null);


    }

    private void fetchContactsHelperFunc (int offset, int limit) {
        ContentResolver cr = mContactsFragment.getBaseActivity().getContentResolver();
        String[] PROJECTION =
                {
                        ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Email._ID,
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.LABEL,
                };

        /*
         * Defines the selection clause. Search for a lookup key
         * and the Email MIME type
         */
        String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?" +
                " AND " +
                ContactsContract.Data.MIMETYPE + " = " +
                "'" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";

        String SORT_ORDER = ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME + " ASC ";
        //Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI.buildUpon().encodedQuery("limit="+offset+","+limit).build(),PROJECTION,null,null,SORT_ORDER +  "LIMIT " + limit +" OFFSET "+ offset);
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, null, null, SORT_ORDER + "LIMIT " + limit + " OFFSET " + offset);

        if (cur!=null && cur.getCount() > 0) {
            int count = cur.getCount();
            //Log.i("Kanishka", "PhoneContactsfragHelperFunc: count = " + count);
            if (cur.moveToFirst()) {
                do {
                    //String id = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Email._ID));
                    //String address = cur.getString(cur.getColumnIndex( ContactsContract.CommonDataKinds.Email.ADDRESS));

                    final String emailAddress = cur.getString(2);
                    String[] name = cur.getString(0).split(" ");
                    if (emailAddress.length() > 0) {

                        if (name.length > 0) {
                            mValidPhoneContactEntriesMap.put(emailAddress, name[0]);
                            if (name.length >= 2) {
                                Log.i("Ketan", "found contact: "  + emailAddress  + " " + name[0] + " " + name[1]);
                                mContactsFragment.updateContactContainerPhone(emailAddress, name[0], name[1], Contact.StatusType.NOT_FRIEND, Contact.ContactType.PHONE);

                                //fetch image which will asynchronously update the ContactContainer when done
                                this.getPhotoUriFromEmailAsync(emailAddress, name[0], name[1], Contact.StatusType.NOT_FRIEND, Contact.ContactType.PHONE);
                            } else if (name.length == 1) {
                                //Log.i("Ketan", "found contact: " + name[0]);
                                mContactsFragment.updateContactContainerPhone(emailAddress, name[0], "", Contact.StatusType.NOT_FRIEND, Contact.ContactType.PHONE);
                                this.getPhotoUriFromEmailAsync(emailAddress, name[0], "", Contact.StatusType.NOT_FRIEND, Contact.ContactType.PHONE);
                            }
                        }
                    }
                } while (cur.moveToNext());
            }
            cur.close();
            //Log.i("Ketan", "mValidPhoneContactEntriesMap size = " + mValidPhoneContactEntriesMap.size() + "offset+limit=" + (offset+limit));
            //update the minGap with the difference between map size and (offset+limit)
            if ( (offset+limit) - mValidPhoneContactEntriesMap.size() > 0) {
                //Log.i("Ketan", "no worthy contact, incrementing minGap by " + ((offset + limit) - mValidPhoneContactEntriesMap.size()));
                mFetchManager.incrMinGap((offset + limit) -mValidPhoneContactEntriesMap.size());
            }
            //Log.i("Ketan", "minGap size = " + mFetchManager.getMinGap());
            mContactsFragment.refreshContactList();
            //update the end index with the size of the container
            mFetchManager.setEndIndex(offset + limit);
        }
    }

    /**
     * initialize Contacts from Phone Contacts
     */
    public void fetchContacts(int visibleStartIndex, int visibleEndIndex) {
        //Log.i("Kanishka", "Now fetching PhoneContacts for visiableStartIndex = " + visibleStartIndex + ", visibleEndIndex = " + visibleEndIndex);
        mFetchManager.fetch(visibleStartIndex, visibleEndIndex, new FetchManager.FetchInterface(){
            @Override
            public void onFetch(int offset, int limit) {
                if (ContextCompat.checkSelfPermission(mContactsFragment.getBaseActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    //request for permissions
                    mContactsFragment.requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            BaseActivity.READ_PHONE_CONTACTS_PERMISSIONS_REQUEST_CODE);
                } else {
                    //fragment has read permissions. Proceed with fetching the contacts.
                    //Log.i("Kanishka", "in PhoneContacts OnFetch: Now fetching for offset = " + offset + ", limit = " + limit);
                    fetchContactsHelperFunc(offset,limit);
                }
            }

        });
    }
}
