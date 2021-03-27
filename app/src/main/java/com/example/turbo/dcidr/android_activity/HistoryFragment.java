package com.example.turbo.dcidr.android_activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.main.activity_helper.history_fragment_helper.HistoryCustomArrayAdapter;
import com.example.turbo.dcidr.main.activity_helper.history_fragment_helper.HistoryFragmentHelper;
import com.example.turbo.dcidr.main.container.HistoryContainer;
import com.example.turbo.dcidr.main.event.BaseEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 3/11/2016.
 */
public class HistoryFragment extends Fragment {

    private BaseActivity mBaseActivity;
    private HistoryFragmentHelper mHistoryFragmentHelper;
    private ListView mHistoryListView;
    private HistoryCustomArrayAdapter mHistoryCustomArrayAdapter;
    private View historyView;
    private HistoryContainer mHistoryContainer;
    private ProgressDialog mProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mHistoryContainer = DcidrApplication.getInstance().getGlobalHistoryContainer();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //set the sort key for the all the events in the historyContainer's EventContainers
        mHistoryContainer.setEventSortKey(BaseEvent.EventSortKey.EVENT_DECIDED_TIME);
        Log.i("FLOW", "onCrate");
    }

    public BaseActivity getBaseActivity(){
        return mBaseActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("FLOW", "History Fragment onCreateView");
        if (historyView == null) {
            historyView = inflater.inflate(R.layout.fragment_history, container, false);
            if (mBaseActivity != null) {
                // create progress bar
                mProgressDialog = mBaseActivity.getAndShowProgressDialog(mBaseActivity, getResources().getString(R.string.loading_msg));
                //get historylistview and associate a customarrayadapter with it
                mHistoryListView = (ListView) historyView.findViewById(R.id.history_list_view);
                mHistoryCustomArrayAdapter = new HistoryCustomArrayAdapter(mBaseActivity, R.id.history_list_view, mHistoryContainer.getGroupContainerEventList());
                mHistoryListView.setAdapter(mHistoryCustomArrayAdapter);
                //mHistoryListView.setOnItemClickListener(mHistoryCustomListViewOnItemClickListener);
                mHistoryListView.setOnScrollListener(mHistoryListViewOnScrollListener);
                // create history fragment helper and initialize history
                mHistoryFragmentHelper = new HistoryFragmentHelper(this);
                mHistoryFragmentHelper.fetchHistory(0, 5);  //in response will call the fetch method
            }else {
                // show error msg
            }
        }
        return historyView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("FLOW", "onAttach");
        mBaseActivity = (BaseActivity) context;
    }



    /**
     * onScrollListener view listener for history list view
     * 1.Setting member on click listener here
     */
    private ListView.OnScrollListener mHistoryListViewOnScrollListener = new ListView.OnScrollListener(){

        int mFirstVisibleItem;
        int mVisibleItemCount;
        int mLastFirstVisibleItem;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(scrollState == 0) {
                mHistoryFragmentHelper.fetchHistory(mFirstVisibleItem, mFirstVisibleItem + mVisibleItemCount - 1);
            }
            final int currentFirstVisibleItem = mHistoryListView.getFirstVisiblePosition();
            if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                ActionBar actionBar = mBaseActivity.getSupportActionBar();
                actionBar.hide();
            } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                ActionBar actionBar = mBaseActivity.getSupportActionBar();
                actionBar.show();
            }
            mLastFirstVisibleItem = currentFirstVisibleItem;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem != 0 && visibleItemCount != 0) {
                this.mFirstVisibleItem = firstVisibleItem;
                this.mVisibleItemCount = visibleItemCount;

            }
        }
    };


    public void onMemberTouched(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Ketan", "sdfsd");
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // custom options for menu
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onFetchSuccess(int statusCode, Header[] headers, byte[] response) {
        if (statusCode == 200) {
            try {
                JSONArray jsonArray = new JSONObject(new String(response)).getJSONArray("result");
                //populate the group container so as to create an array of basegroup objects
                mHistoryContainer.populateGroup(jsonArray);
                //for each row in the response, represented as a JSONObject in the JSONArray
                //check whether groupId exists and if yes, then populate the EventContainer
                //for that groupId with the newly received event information
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.has("groupId")){
                        Long groupId = jsonObject.getLong("groupId");
                        mHistoryContainer.getGroupMap().get(groupId).getEventContainer().populateEvent(jsonObject);
                    }
                }
            } catch (Exception e) { //TODO: Catch only relevant exceptions -- not "Exception"
                mBaseActivity.showAlertDialog("Unexpected Error occurred" + e.toString());
            }
            mHistoryContainer.refreshGroupContainerEventList();
            mHistoryCustomArrayAdapter.notifyDataSetChanged();
            mBaseActivity.dismissProgressDialog(mProgressDialog);

            // let the GC claim the progress bar memeory
            mProgressDialog = null;

        }
    }

    public void onFetchFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
        JSONObject jsonObject = null;
        String errorString = null;
        try {
            jsonObject = new JSONObject(new String(errorResponse));
            errorString = (String) jsonObject.get("error");
        } catch (JSONException error) {
            Toast toast = Toast.makeText(mBaseActivity, R.string.getgroup_error_msg, Toast.LENGTH_SHORT);
            toast.show();
        }
        mBaseActivity.showAlertDialog(errorString);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHistoryContainer.clear();
        mHistoryContainer = null;
        mProgressDialog = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
