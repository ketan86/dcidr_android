package com.example.turbo.dcidr.main.activity_helper.friend_picker_fragment_helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.GroupAsyncHttpClient;
import com.example.turbo.dcidr.main.group.BaseGroup;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;

import java.util.ArrayList;

/**
 * Created by Turbo on 4/11/2016.
 */
public class FriendPickerCustomBaseAdapter extends BaseAdapter {

    private static final int TYPE_USER = 0;
    private static final int TYPE_GROUP = 1;
    private LayoutInflater mLayoutInflater;
    private ArrayList<Object> mArrayList = new ArrayList<Object>();
    private Context mContext;
    private GroupAsyncHttpClient mGroupAsyncHttpClient;
    private String mUserIdStr;
    //private ArrayList<User> mUserArrayList = new ArrayList<User>();
    //private ArrayList<BaseGroup> mGroupArrayList = new ArrayList<BaseGroup>();

    public  FriendPickerCustomBaseAdapter(Context context){
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGroupAsyncHttpClient = new GroupAsyncHttpClient(context);
        mUserIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
        mContext = context;
    }


    @Override
    public int getItemViewType(int position) {
        if(mArrayList.get(position) instanceof User){
            return TYPE_USER;
        }else {
            return TYPE_GROUP;
        }
    }

    public void setData(ArrayList<?> object){
        mArrayList.addAll(object);
    }

    public void clearArrayList(){
        mArrayList.clear();
    }


    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);
        convertView = mLayoutInflater.inflate(R.layout.fragment_friend_picker_custom_view, parent, false);

        switch (rowType){
            case TYPE_USER:
                final User user = (User) getItem(position);
                final ImageView userPic = (ImageView) convertView.findViewById(R.id.pic);
                TextView userName = (TextView) convertView.findViewById(R.id.name);
                TextView userTypeColor = (TextView) convertView.findViewById(R.id.type_color);
                userTypeColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.md_blue_800));
                userName.setTextColor(ContextCompat.getColor(mContext, R.color.md_blue_800));


                if (user.getUserProfilePicBitmap() != null) {
                    userPic.setImageBitmap(user.getUserProfilePicBitmap());
                } else if(user.getUserProfilePicBase64Str() !=null && user.getUserProfilePicBitmap() == null ){
                    user.setUserProfilePicBitmap(Utils.decodeBase64(user.getUserProfilePicBase64Str(),
                            userPic.getWidth(), userPic.getHeight()));
                    userPic.setImageBitmap(user.getUserProfilePicBitmap());
                } else {
                    user.onUserProfPicFetchDoneListener(new User.UserProfPicFetchInterface() {
                        @Override
                        public void onImageFetchDone(Bitmap bitmap) {
                            if (bitmap != null) {
                                // TODO need to implement viewHolder to avoid image overlapping (refer contacts adapter)
                                userPic.setImageBitmap(bitmap);
                            }
                        }

                        @Override
                        public int getImageWidth() {
                            return userPic.getWidth();
                        }

                        @Override
                        public int getImageHeight() {
                            return userPic.getHeight();
                        }
                    });
                }
                CheckBox userSelected = (CheckBox) convertView.findViewById(R.id.selected_checkbox);
                userName.setText(user.getFirstName()  + " " + user.getLastName());
                if(user.getIsSelected()){
                    userSelected.setChecked(true);
                }
                userSelected.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Log.i("Ketan:F3", user.toString());
                            user.setIsSelected(true);
                        } else {
                            user.setIsSelected(false);
                        }

                    }
                });
                break;
            case TYPE_GROUP:
                final BaseGroup baseGroup = (BaseGroup) getItem(position);
                final ImageView groupPic = (ImageView) convertView.findViewById(R.id.pic);
                TextView groupName = (TextView) convertView.findViewById(R.id.name);
                groupName.setTextColor(ContextCompat.getColor(mContext, R.color.md_red_500));
                TextView groupTypeColor = (TextView) convertView.findViewById(R.id.type_color);
                groupTypeColor.setBackgroundColor(ContextCompat.getColor(mContext, R.color.md_red_500));
                CheckBox groupSelected = (CheckBox) convertView.findViewById(R.id.selected_checkbox);
                groupName.setText(baseGroup.getGroupName());

                if(baseGroup.getGroupProfilePicBitmap() != null) {
                    groupPic.setImageBitmap(baseGroup.getGroupProfilePicBitmap());
                }else if(baseGroup.getGroupProfilePicBase64Str() != null && baseGroup.getGroupProfilePicBitmap() == null){
                    baseGroup.setGroupProfilePicBitmap(Utils.decodeBase64(baseGroup.getGroupProfilePicBase64Str(),
                            groupPic.getWidth(), groupPic.getHeight()));
                    groupPic.setImageBitmap(baseGroup.getGroupProfilePicBitmap());
                }else {
                    baseGroup.onGroupProfPicFetchDoneListener(new BaseGroup.GroupProfPicFetchInterface() {
                        @Override
                        public void onImageFetchDone(Bitmap bitmap) {
                            if (bitmap != null) {
                                // TODO need to implement viewHolder to avoid image overlapping (refer contacts adapter)
                                groupPic.setImageBitmap(bitmap);
                            }
                        }

                        @Override
                        public int getImageWidth() {
                            return groupPic.getWidth();
                        }

                        @Override
                        public int getImageHeight() {
                            return groupPic.getHeight();
                        }
                    });
                }
                if(baseGroup.getIsSelected()){
                    groupSelected.setChecked(true);
                }
                groupSelected.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            baseGroup.setIsSelected(true);
                        } else {
                            baseGroup.setIsSelected(false);
                        }

                    }
                });
                break;
            default:
                break;
        }
        return convertView;
    }
}
