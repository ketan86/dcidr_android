package com.example.turbo.dcidr.main.activity_helper.image_viewer_activity_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.main.common.ImageViewer;

import java.util.ArrayList;

/**
 * Created by Turbo on 2/18/2016.
 */
public class ImageViewerCustomArrayAdapter extends ArrayAdapter<ImageViewer> {
    public ImageViewerCustomArrayAdapter(Context context, int resource, ArrayList<ImageViewer> imageBitmapArrayList) {
        super(context, resource, imageBitmapArrayList);
    }

    public class ViewHolder {
        ImageView imageView;
        TextView addedByUserName;
        int position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ImageViewer imageViewer = getItem(position);
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.image_custom_view, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            holder.addedByUserName = (TextView) convertView.findViewById(R.id.added_by_user_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageBitmap(imageViewer.getImageBitmap());
        holder.addedByUserName.setText(imageViewer.getAddedByUserName());

        return convertView;
    }
}
