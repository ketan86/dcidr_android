package com.example.turbo.dcidr.main.activity_helper.contacts_fragment_helper;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.ContactsFragment;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.user.Contact;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.example.turbo.dcidr.utils.image_utils.rounded_image_view.CircularImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Borat on 5/21/2016.
 */
public class ContactsFragmentCustomArrayAdapter extends ArrayAdapter<Contact> {
    public static class CustomViewHolder {
        public CircularImageView contactProfilePic;
        public TextView contactName;
        public TextView contactEmailAddress;
        public ImageView contactTypeImageView;
        public Button contactInviteButton;
        public Bitmap phoneIconBitmap;
        public Bitmap facebookIconBitmap;
        public Bitmap dcidrIconBitmap;
        public int position;
    }


    public ContactsFragmentCustomArrayAdapter(ContactsFragment fragment, Context context, int resource, ArrayList<Contact> userArrayList, BaseActivity baseActivity) {
        super(context, resource, userArrayList);
    }
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //final ViewHolder holder;
        final Contact contact = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final BaseActivity baseActivity = (BaseActivity) getContext();
        final CustomViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_contacts_custom_view, parent, false);
            holder = new CustomViewHolder();
            holder.contactProfilePic = (CircularImageView) convertView.findViewById(R.id.user_profile_pic);
            holder.contactName = (TextView) convertView.findViewById(R.id.user_name);
            holder.contactEmailAddress = (TextView) convertView.findViewById(R.id.user_email_address);
            holder.contactTypeImageView = (ImageView) convertView.findViewById(R.id.contact_type_image_view);
            holder.contactInviteButton = (Button) convertView.findViewById(R.id.invite_button);
            holder.phoneIconBitmap = Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.phone_circle_icon));
            holder.facebookIconBitmap = Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.facebook_circle_icon));
            holder.dcidrIconBitmap = Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.dcidr_circle_icon));
            convertView.setTag(holder);
        }else {
            holder = (CustomViewHolder) convertView.getTag();
        }
        // lets assign a current position to holder position
        // it helps to decide if data to load to view or not
        // for ex, during fast scrolling, holder.imageView reference may get overridden by the time,
        // image is loaded into expected view
        holder.position = position;
        holder.contactProfilePic.setImageBitmap(Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.user_icon)));


        if (contact.getUserProfilePicBitmap() != null) {
            holder.contactProfilePic.setImageBitmap(contact.getUserProfilePicBitmap());
        } else if(contact.getUserProfilePicBase64Str() !=null && contact.getUserProfilePicBitmap() == null ){
            contact.setUserProfilePicBitmap(Utils.decodeBase64(contact.getUserProfilePicBase64Str(),
                    holder.contactProfilePic.getWidth(), holder.contactProfilePic.getHeight()));
            holder.contactProfilePic.setImageBitmap(contact.getUserProfilePicBitmap());
        } else {
            contact.onUserProfPicFetchDoneListener(new Contact.UserProfPicFetchInterface() {
                @Override
                public void onImageFetchDone(Bitmap bitmap) {
                    if (holder.position == position) {
                        holder.contactProfilePic.setImageBitmap(bitmap);
                    }
                }

                @Override
                public int getImageWidth() {
                    return holder.contactProfilePic.getWidth();
                }

                @Override
                public int getImageHeight() {
                    return holder.contactProfilePic.getHeight();
                }
            });
        }

        holder.contactProfilePic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(contact.getUserProfilePicBitmap() != null){
                    BaseActivity baseActivity = (BaseActivity) getContext();
                    if(contact.getUserProfilePicBase64Str() != null) {
                        baseActivity.showImageViewDialog(contact.getUserProfilePicBase64Str(), null);
                    }
                    if(contact.getUserProfilePicBitmap() != null) {
                        baseActivity.showImageViewDialog(null, contact.getUserProfilePicBitmap());
                    }
                }
            }
        });


        holder.contactName.setText(contact.getUserName());
        holder.contactEmailAddress.setText(contact.getEmailId());

        if(contact.getContactType() == Contact.ContactType.DCIDR) {
            holder.contactTypeImageView.setImageBitmap(holder.dcidrIconBitmap);
        }else if (contact.getContactType() == Contact.ContactType.FACEBOOK) {
            holder.contactTypeImageView.setImageBitmap(holder.facebookIconBitmap);
        }else if (contact.getContactType() == Contact.ContactType.PHONE) {
            holder.contactTypeImageView.setImageBitmap(holder.phoneIconBitmap);
        }
        //Check if the contact is a Dcidr friend, and if yes, hide the Invite button
        holder.contactInviteButton.setVisibility(View.VISIBLE);
        holder.contactInviteButton.setText("INVITE");
        if (contact.getStatusType() == Contact.StatusType.NOT_FRIEND) {
            holder.contactInviteButton.setEnabled(true); //enable click
            holder.contactInviteButton.setVisibility(View.VISIBLE);
            //add clicklistener on the invite button
            holder.contactInviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                    builder.setMessage("Send invitation to " + contact.getFirstName() + " " + contact.getLastName() + " ?")
                            .setPositiveButton(R.string.positive_button_msg, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //yes will invoke a server API to send out an email
                                    String userIdStr = DcidrApplication.getInstance().getUserCache().get("userId");
                                    UserAsyncHttpClient inviteAsFriendAsyncHttpClient = new UserAsyncHttpClient(baseActivity);
                                    inviteAsFriendAsyncHttpClient.inviteContact(userIdStr, contact.getEmailId(), new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                            if (statusCode == 201 || statusCode == 200) {
                                                //change the status of the contact
                                                contact.setStatusType(Contact.StatusType.INVITED); //set status to invited
                                                //refresh the adapter
                                                notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                                            if(statusCode == 400 || statusCode == 403) {
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.negative_button_msg, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //no will do nothing , just close the dialog
                                    return;
                                }
                            })
                            .create().show();
                }
            });
        } else  if (contact.getStatusType() ==  Contact.StatusType.FRIEND) {
            holder.contactInviteButton.setVisibility(View.GONE);
            holder.contactInviteButton.setEnabled(false); //disable click
        } else if (contact.getStatusType() ==  Contact.StatusType.INVITED) {
            holder.contactInviteButton.setText("Pending");
            holder.contactInviteButton.setEnabled(false); //disable click
        }
        return convertView;
    }

}
