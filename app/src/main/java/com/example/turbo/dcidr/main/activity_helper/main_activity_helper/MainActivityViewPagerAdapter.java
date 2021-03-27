package com.example.turbo.dcidr.main.activity_helper.main_activity_helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.turbo.dcidr.android_activity.ContactsFragment;
import com.example.turbo.dcidr.android_activity.GroupFragment;
import com.example.turbo.dcidr.android_activity.HistoryFragment;

import java.util.HashMap;

/**
 * Created by Turbo on 3/11/2016.
 */
public class MainActivityViewPagerAdapter extends FragmentPagerAdapter {
    private int mTotalPages;
    private CharSequence mTitlesCharSequence[];
    private HashMap<String, Fragment> mFragmentHashMap;

    public MainActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
        clearFragments(fm);
        // instantiate fragmentMap
        mFragmentHashMap = new HashMap<String, Fragment>();
        mTotalPages = 3;
        mTitlesCharSequence = new CharSequence[]{"History","Activities","Contacts"};
    }

    public void clearFragments(FragmentManager fm){
        //for a given Activity when someone creates a MainActivityPagerAdapter object, the
        // fragmentmanager may be holding fragments created previously. For example, if we
        //create MainActivityViewPagerAdapter from MainActivity, which is a single task activity,
        //the fragment manager object is not recreated (as the activity itself is not recreated).
        //In such a scenario where fragmentManager already holds the fragments needed by
        //mPagerAdapter then getItem will not be called because it is called
        // whenever the adapter needs a fragment and the fragment does not exist.
        //Becuase of the fragmentManager holding previous fragments and getItem not being called,
        // a new Fragment object is not created.
        //Consequently onAttach does not happen (previous fragment is already attached) and thus
        //getHistory is not called which gets history from the server and populates historyContainer
        //and in turn the listAdapter uses the historyContainer etc.
        //Thus, we need to clear the fragment manager's fragments if we want to reload history
        //from the server.
        if (fm.getFragments() != null) {
            fm.getFragments().clear();
        }
    }
    @Override
    //getItem will be called whenever the adapter needs a fragment and the fragment does not exist.
    public Fragment getItem(int position) {
        if(position == 0) {
            HistoryFragment historyFragment = new HistoryFragment();
            mFragmentHashMap.put("HistoryFragment", historyFragment);
            return historyFragment;
        } else if(position == 1) {
            GroupFragment groupFragment = new GroupFragment();
            mFragmentHashMap.put("GroupFragment", groupFragment);
            return groupFragment;
        }else if(position == 2){
            ContactsFragment contactsFragment = new ContactsFragment();
            mFragmentHashMap.put("ContactsFragment", contactsFragment);
            return contactsFragment;
        }
        return new Fragment();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitlesCharSequence[position];
    }

    @Override
    public int getCount() {
        return mTotalPages;
    }

    public Fragment getFragmentByName(String name){
        return mFragmentHashMap.get(name);
    }

}
