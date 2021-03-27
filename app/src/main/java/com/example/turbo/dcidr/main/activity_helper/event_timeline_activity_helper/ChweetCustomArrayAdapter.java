package com.example.turbo.dcidr.main.activity_helper.event_timeline_activity_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.main.event.Chweet;

import java.util.ArrayList;

/**
 * Created by borat on 10/2/2016.
 */
public class ChweetCustomArrayAdapter extends ArrayAdapter<Chweet> {
    public ChweetCustomArrayAdapter(Context context, int resource, ArrayList<Chweet> chweetArrayList) {
        super(context, resource, chweetArrayList);
    }

    public class ViewHolder {
        TextView chweetText;
        TextView chweetUserDateText;
        int position;
    }

    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.chweet_bubble, parent, false);
            holder = new ViewHolder();
            holder.chweetText = (TextView) convertView.findViewById(R.id.chweet_textview);
            holder.chweetUserDateText = (TextView) convertView.findViewById(R.id.chweet_user_info_date_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Chweet chweet = (Chweet) getItem(position);
        holder.chweetUserDateText.setTextColor(chweet.getChweetColor());
        holder.chweetText.setText(chweet.getChweetText());
//        if(chweet.getUserId() == DcidrApplication.getInstance().getUser().getUserId()) {
//            holder.chweetText.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.yellowGreenColor));
//        }
//        holder.chweetUserDateText.setText(chweet.getChweetUserFirstName() + " " + chweet.getChweetUserLastName() + " at " +
//                String.valueOf(Utils.convertEpochToDateTime(chweet.getChweetTime(),
//                TimeZone.getDefault(), "MM/dd/yyyy hh:mm aa")));

        holder.chweetUserDateText.setText(chweet.getChweetUserFirstName() + " " + chweet.getChweetUserLastName());
        return convertView;
    }
}
