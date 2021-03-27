package com.example.turbo.dcidr.main.activity_helper.contact_invitation_helper;

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
import android.widget.TextView;

import com.example.turbo.dcidr.R;
import com.example.turbo.dcidr.android_activity.BaseActivity;
import com.example.turbo.dcidr.android_activity.ContactInvitation;
import com.example.turbo.dcidr.global.DcidrApplication;
import com.example.turbo.dcidr.httpclient.UserAsyncHttpClient;
import com.example.turbo.dcidr.main.container.ContactContainer;
import com.example.turbo.dcidr.main.user.Contact;
import com.example.turbo.dcidr.main.user.User;
import com.example.turbo.dcidr.utils.common_utils.Utils;
import com.example.turbo.dcidr.utils.image_utils.rounded_image_view.CircularImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Turbo on 7/19/2016.
 */
public class InInvitationCustomArrayAdapter extends ArrayAdapter<Contact> {

    private UserAsyncHttpClient mUserAsyncHttpClient;
    private String mUserIdStr;
    private String mUserEmailId;
    private BaseActivity mBaseActivity;
    private ContactContainer mContactContainer;
    private Bitmap mDefaultUserProfilePic = Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.user_icon));

    public InInvitationCustomArrayAdapter(Context context, int resource, ArrayList<Contact> userArrayList) {
        super(context, resource, userArrayList);
        mBaseActivity = (BaseActivity) context;
        mUserAsyncHttpClient = new UserAsyncHttpClient(getContext());
        mUserIdStr = DcidrApplication.getInstance().getUser().getUserIdStr();
        mUserEmailId = DcidrApplication.getInstance().getUser().getEmailId();
        mContactContainer = DcidrApplication.getInstance().getGlobalContactContainer();
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Contact contact = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_incoming_invite_view, parent, false);
        }

        final TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        final CircularImageView contactProfilePic = (CircularImageView) convertView.findViewById(R.id.contact_profile_pic);
        Button acceptInviteButton = (Button) convertView.findViewById(R.id.accept_invite_button);
        Button declineInviteButton = (Button) convertView.findViewById(R.id.decline_invite_button);
        acceptInviteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAcceptAlertDialog(contact);
            }
        });

        declineInviteButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeclineAlertDialog(contact);
            }
        });

        contactProfilePic.setImageBitmap(mDefaultUserProfilePic);
        if (contact.getUserProfilePicBitmap() != null) {
            contactProfilePic.setImageBitmap(contact.getUserProfilePicBitmap());
        } else if(contact.getUserProfilePicBase64Str() !=null && contact.getUserProfilePicBitmap() == null ){
            contact.setUserProfilePicBitmap(Utils.decodeBase64(contact.getUserProfilePicBase64Str(),
                    contactProfilePic.getWidth(), contactProfilePic.getHeight()));
            contactProfilePic.setImageBitmap(contact.getUserProfilePicBitmap());
        } else {
            contact.onUserProfPicFetchDoneListener(new User.UserProfPicFetchInterface() {
                @Override
                public void onImageFetchDone(Bitmap bitmap) {
                    if (bitmap != null) {
                        // TODO need to implement viewHolder to avoid image overlapping (refer contacts adapter)
                        contactProfilePic.setImageBitmap(bitmap);
                    }
                }
                @Override
                public int getImageWidth() {
                    return contactProfilePic.getWidth();
                }

                @Override
                public int getImageHeight() {
                    return contactProfilePic.getHeight();
                }
            });
        }

        contactProfilePic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(contact.getUserProfilePicBitmap() != null ) {
                    mBaseActivity.showImageViewDialog(null, contact.getUserProfilePicBitmap());
                }
            }
        });

        if(contact.getUserName() == null){
            contactName.setText(contact.getEmailId());
        } else {
            contactName.setText(contact.getUserName());
        }
        return convertView;
    }

    private void showAcceptAlertDialog(final Contact contact){
        AlertDialog.Builder builder = new AlertDialog.Builder(mBaseActivity);
        builder.setMessage("Are you sure you want to accept the invitation ?")
                .setPositiveButton(R.string.positive_button_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            mUserAsyncHttpClient.acceptInvitation(mUserIdStr, mUserEmailId, contact.getUserIdStr(), contact.getEmailId(), new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        if(mContactContainer.getContactMap().get(contact.getEmailId()) != null){
                                            mContactContainer.getContactMap().get(contact.getEmailId()).setStatusType(Contact.StatusType.FRIEND);
                                        }
                                        mBaseActivity.showAlertDialog("Invitation accepted");
                                        ContactInvitation contactInvitation = (ContactInvitation) mBaseActivity;
                                        contactInvitation.getInInvitationContactContainer().getContactMap().remove(contact.getEmailId());
                                        contactInvitation.getInInvitationContactContainer().refreshContactList();
                                        notifyDataSetChanged();

                                        if(contactInvitation.getOutInvitationContactContainer().getContactMap().get(contact.getEmailId()) != null) {
                                            contactInvitation.getOutInvitationContactContainer().getContactMap().remove(contact.getEmailId());
                                            contactInvitation.getOutInvitationContactContainer().refreshContactList();
                                            contactInvitation.getOutInvitationCustomArrayAdapter().notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    mBaseActivity.showAlertDialog("Error accepting the invitation");
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            mBaseActivity.showAlertDialog("Unexpected Error occurred" + e.toString());
                        }
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

    private void showDeclineAlertDialog(final Contact contact){
        AlertDialog.Builder builder = new AlertDialog.Builder(mBaseActivity);
        builder.setMessage("Are you sure you want to decline the invitation ?")
                .setPositiveButton(R.string.positive_button_msg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            mUserAsyncHttpClient .declineInvitation(mUserIdStr, mUserEmailId, contact.getUserIdStr(), contact.getEmailId(), new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    if (statusCode == 200) {
                                        if (mContactContainer.getContactMap().get(contact.getEmailId()) != null) {
                                            mContactContainer.getContactMap().get(contact.getEmailId()).setStatusType(Contact.StatusType.NOT_FRIEND);
                                        }
                                        mBaseActivity.showAlertDialog("Invitation declined");
                                        ContactInvitation contactInvitation = (ContactInvitation) mBaseActivity;
                                        contactInvitation.getInInvitationContactContainer().getContactMap().remove(contact.getEmailId());
                                        contactInvitation.getInInvitationContactContainer().refreshContactList();
                                        notifyDataSetChanged();

                                        if(contactInvitation.getOutInvitationContactContainer().getContactMap().get(contact.getEmailId()) != null) {
                                            contactInvitation.getOutInvitationContactContainer().getContactMap().remove(contact.getEmailId());
                                            contactInvitation.getOutInvitationContactContainer().refreshContactList();
                                            contactInvitation.getOutInvitationCustomArrayAdapter().notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    mBaseActivity.showAlertDialog("Error declining the invitation");
                                }
                            });
                        } catch (UnsupportedEncodingException e) {
                            mBaseActivity.showAlertDialog("Unexpected Error occurred" + e.toString());
                        }
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

}
