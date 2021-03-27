package com.example.turbo.dcidr.main.activity_helper.user_activity_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.main.user.User;

import java.util.ArrayList;

/**
 * Created by Turbo on 4/10/2016.
 */
public class NameCapsuleCustomArrayAdapter extends ArrayAdapter<User>{

    public NameCapsuleCustomArrayAdapter(Context context, int resource, ArrayList<User> userArrayList) {
        super(context, resource, userArrayList);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.name_capsule_custom_view, parent, false);
        ImageView userPic = (ImageView) convertView.findViewById(R.id.capsule_image_view);
        TextView userName = (TextView) convertView.findViewById(R.id.capsule_name_text_view);
        Button cancelButton = (Button) convertView.findViewById(R.id.capsule_cancel_button);
        userName.setText(user.getFirstName()  + " " + user.getLastName());
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(user);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
