package com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper;

import android.os.Bundle;
import android.util.Log;

import com.example.turbo.dcidr.android_activity.ContactsFragment;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.FetchManager;
import com.example.turbo.dcidr.main.user.Contact;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Borat on 6/11/2016.
 */
public class FacebookContactsFragmentHelper extends BaseContactsFragmentHelper{

    private FetchManager mFetchManager;
    private String mUserIdStr;

    public FacebookContactsFragmentHelper(ContactsFragment contactsFragment) {
        super(contactsFragment);
        mContactsFragment = contactsFragment;
        mFetchManager = new FetchManager(5,10);
    }
    /**
     * fetch Contacts from facebook
     */
    public void fetchContacts(int visibleStartIndex, int visibleEndIndex) {
        mFetchManager.fetch(visibleStartIndex, visibleEndIndex, new FetchManager.FetchInterface() {
            @Override
            public void onFetch(int offset, int limit) {
                GraphRequest graphRequest =  GraphRequest.newGraphPathRequest(DcidrApplication.getInstance().getFbAccessToken(),"/me/invitable_friends", new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject jsonObject = response.getJSONObject();
                        try {
                            if (jsonObject != null) {
                                String strJson = jsonObject.getString("data");
                                Log.v("Kanishka", "TAG==" + strJson);
                                JSONArray jArray = new JSONArray(strJson);
                                for (int i = 0; i < jArray.length(); i++) {
                                    String idfb = jArray.getJSONObject(i).getString("id");
                                    String name[] = jArray.getJSONObject(i).getString("name").split("\\s+");
                                    String emailAddress = jArray.getJSONObject(i).getString("email");
                                    String firstName = "";
                                    String lastName = "";
                                    if (name[0] == null) {
                                        firstName = emailAddress;
                                    } else {
                                        firstName = name[0];
                                    }
                                    if (name[1] != null) {
                                        lastName = name[1];
                                    }
                                    mContactsFragment.updateContactContainerPhone(emailAddress, firstName, lastName, Contact.StatusType.NOT_FRIEND, Contact.ContactType.FACEBOOK);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("limit", "5000");
                parameters.putString("fields", "id,name,gender");
                //parameters.putString("fields", "id,name,first_name, last_name, email,gender, birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

        });
    }
}
