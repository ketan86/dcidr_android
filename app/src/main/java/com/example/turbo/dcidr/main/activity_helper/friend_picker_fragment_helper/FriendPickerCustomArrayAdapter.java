package com.example.turbo.dcidr.main.activity_helper.friend_picker_fragment_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.main.user.User;

import java.util.ArrayList;

/**
 * Created by Turbo on 4/9/2016.
 */
public class FriendPickerCustomArrayAdapter extends ArrayAdapter<User> {



    public FriendPickerCustomArrayAdapter(Context context, int resource, ArrayList<User> userArrayList) {
        super(context, resource, userArrayList);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.fragment_friend_picker_custom_view, parent, false);
        ImageView userPic = (ImageView) convertView.findViewById(R.id.pic);
        TextView userName = (TextView) convertView.findViewById(R.id.name);
        CheckBox userSelected = (CheckBox) convertView.findViewById(R.id.selected_checkbox);
        userName.setText(user.getFirstName()  + " " + user.getLastName());
        if(user.getIsSelected()){
            userSelected.setChecked(true);
        }
        userSelected.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    user.setIsSelected(true);
                } else {
                    user.setIsSelected(false);
                }

            }
        });
        return convertView;
    }
}
