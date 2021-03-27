package com.example.turbo.dcidr.main.activity_helper.group_fragment_helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.example.turbo.dcidr.utils.image_utils.rounded_image_view.CircularImageView;

import java.util.ArrayList;


/**
 * Created by Turbo on 2/25/2016.
 */
public class GroupFragmentCustomArrayAdapter extends ArrayAdapter<BaseGroup> {


    public GroupFragmentCustomArrayAdapter(Context context, int resource, ArrayList<BaseGroup> eventGroupList) {
        super(context, resource, eventGroupList);
    }

    public class CustomViewHolder {
        CircularImageView groupProfilePic;
        TextView groupLastModifiedTime;
        Button groupUnreadEventCount;
        TextView groupName;
        TextView totalEventCount;
        int position;
        int groupProfilePicWidth;
        int groupProfilePicHeight;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        final BaseGroup baseGroup = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final CustomViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_group_custom_view, parent, false);
            holder = new CustomViewHolder();
            holder.groupProfilePic = (CircularImageView) convertView.findViewById(R.id.group_profile_pic);
            holder.groupProfilePicWidth = holder.groupProfilePic.getDrawable().getIntrinsicWidth();
            holder.groupProfilePicHeight = holder.groupProfilePic.getDrawable().getIntrinsicHeight();
            holder.groupLastModifiedTime = (TextView) convertView.findViewById(R.id.group_last_modified_time);
            holder.groupUnreadEventCount = (Button) convertView.findViewById(R.id.group_unread_event_count);
            holder.groupName = (TextView) convertView.findViewById(R.id.group_name);
            holder.totalEventCount = (TextView) convertView.findViewById(R.id.total_event_count);
            convertView.setTag(holder);
        }else {
            holder = (CustomViewHolder) convertView.getTag();
        }
        // lets assign a current position to holder position
        // it helps to decide if data to load to view or not
        // for ex, during fast scrolling, holder.imageView reference may get overridden by the time,
        // image is loaded into expected view
        holder.position = position;

        holder.groupProfilePic.setImageBitmap(Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.group_pic_icon)));

        holder.totalEventCount.setText(baseGroup.getTotalEventCountStr() + " Activities");

        if(baseGroup.getGroupProfilePicBitmap() != null) {
            holder.groupProfilePic.setImageBitmap(baseGroup.getGroupProfilePicBitmap());
        }else if(baseGroup.getGroupProfilePicBase64Str() != null && baseGroup.getGroupProfilePicBitmap() == null){
            baseGroup.setGroupProfilePicBitmap(Utils.decodeBase64(baseGroup.getGroupProfilePicBase64Str(),
                    holder.groupProfilePicWidth,  holder.groupProfilePicHeight));
            holder.groupProfilePic.setImageBitmap(baseGroup.getGroupProfilePicBitmap());
        }else {
            baseGroup.onGroupProfPicFetchDoneListener(new BaseGroup.GroupProfPicFetchInterface() {
                @Override
                public void onImageFetchDone(Bitmap bitmap) {
                    if(holder.position == position) {
                        if (bitmap != null) {
                            // TODO need to implement viewHolder to avoid image overlapping (refer contacts adapter)
                            holder.groupProfilePic.setImageBitmap(bitmap);
                        }
                    }
                }

                @Override
                public int getImageWidth() {
                    return holder.groupProfilePicWidth;
                }

                @Override
                public int getImageHeight() {
                    return holder.groupProfilePicHeight;
                }
            });
        }

        holder.groupProfilePic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(baseGroup.getGroupProfilePicBitmap() != null) {
                    BaseActivity baseActivity = (BaseActivity) getContext();
                    baseActivity.showImageViewDialog(baseGroup.getGroupProfilePicBase64Str(), null);
                }
            }
        });

        if(baseGroup.getUnreadEventCount()> 0){
            holder.groupUnreadEventCount.setText(String.valueOf(baseGroup.getUnreadEventCount()));
            holder.groupUnreadEventCount.setVisibility(View.VISIBLE);
        }else{
            holder.groupUnreadEventCount.setVisibility(View.GONE);
        }
        //eventGroupMembers.setText(String.valueOf(baseGroup.getMemberCount()) + " Members");
        holder.groupName.setText(baseGroup.getGroupName());
//        holder.groupLastModifiedTime.setText(String.valueOf(Utils.convertEpochToDateTime( baseGroup.getGroupLastModifiedTime(),
//                TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa" )));
        holder.groupLastModifiedTime.setText(Utils.convertDateToMeaningfulText(baseGroup.getGroupLastModifiedTime(), false));
        return convertView;
    }
}