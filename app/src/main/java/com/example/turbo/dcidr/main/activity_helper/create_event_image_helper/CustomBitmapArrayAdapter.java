package com.example.turbo.dcidr.main.activity_helper.create_event_image_helper;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.utils.image_utils.image_selector.CustomBitmap;
import com.example.turbo.dcidr.utils.layout_utils.HorizontalListView;
import com.example.turbo.dcidr.utils.layout_utils.ListViewUpdater;

import java.util.ArrayList;

/**
 * Created by borat on 8/29/2016.
 */
public class CustomBitmapArrayAdapter extends ArrayAdapter<CustomBitmap> {
    Context mContext;
    View mView;

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    public CustomBitmapArrayAdapter(Context context, int resource, ArrayList<CustomBitmap> bitmapArrayList) {
        super(context, resource, bitmapArrayList);
        mContext = context;
    }

    public class ViewHolder {
        ImageView placePicImageView;
        CheckBox checkBox;
        ImageView zoomImage;
        int position;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.bitmap_layout, parent, false);
            holder = new ViewHolder();
            holder.placePicImageView = (ImageView) convertView.findViewById(R.id.placeImage);
            holder.checkBox = (CheckBox)  convertView.findViewById(R.id.bitmap_check_box);
            holder.zoomImage = (ImageView)  convertView.findViewById(R.id.image_zoom_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.placePicImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.checkBox.setChecked(true);
//            }
//        });

        final CustomBitmap customBitmap = (CustomBitmap) getItem(position);
        holder.checkBox.setSelected(customBitmap.mSelected);

        holder.zoomImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BaseActivity baseActivity = (BaseActivity) getContext();
                baseActivity.showImageViewDialog(null, customBitmap.mBitmap);
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                boolean changed = false;
                if (isChecked) {
                    if (customBitmap.mSelected !=true) {
                        customBitmap.mSelected = true;
                        //loop through the entire array and disable all other selected items
                        for (int i = 0; i < getCount(); i++) {
                            if (i != position) {
                                CustomBitmap cbLocal = getItem(i);
                                cbLocal.mSelected = false;
                            }
                        }
                        changed = true;
                    }
                } else {
                    if (customBitmap.mSelected !=false ) {
                        customBitmap.mSelected = false;
                        changed = true;
                    }
                }
                if (changed) {
                    //notifyDataSetChanged();
                    // update other visible view's status label
                    ListViewUpdater listViewUpdater = new ListViewUpdater();
                    listViewUpdater.updateListViewVisibleItems((HorizontalListView)parent, position, R.id.bitmap_check_box, new ListViewUpdater.OnListItemFoundListener() {
                        @Override
                        public void onListItemFound(int position, View view) {
                            CheckBox checkBox = (CheckBox) view;
                            checkBox.setChecked(false);

                        }
                    });
//                    ListView lv = (ListView)parent;
//                    for (int j=lv.getFirstVisiblePosition();j < lv.getLastVisiblePosition();j++) {
//                        if (j!=position) {
//                            View cv = lv.getChildAt(j);
//                            CheckBox checkBox = (CheckBox)cv.findViewById(R.id.checkBox);
//                            checkBox.setChecked(false);
//                        }
//                    }
                }
            }
        });
        //make sure that hte changelistener is overwritten with the new checkbox and only then setchecked is called
        holder.checkBox.setChecked(customBitmap.mSelected);
        holder.placePicImageView.setImageBitmap(customBitmap.mBitmap);
        return convertView;
    }
}
