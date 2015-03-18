package com.codepath.contact.adapters;

import android.content.Context;
import android.util.Log;

import com.codepath.contact.models.ContactInfo;
import com.codepath.contact.models.Request;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InboxAdapter extends RequestsAdapter {
    private static final String TAG = "InboxAdapter";

    public InboxAdapter(Context context, List<Request> objects) {
        super(context, objects);
    }

    void setItemValues(final ViewHolder viewHolder, final Request request){
        viewHolder.tvName.setText(request.getFrom());

        ContactInfo fromUserContactInfo = request.getFromUser();
        if(fromUserContactInfo == null){
            Log.e(TAG, "fromUserContactInfo null in inbox request");
            return;
        }

        fromUserContactInfo.fetchIfNeededInBackground(new GetCallback<ContactInfo>(){
            @Override
            public void done(ContactInfo contactInfo, ParseException e){
                if(e != null){
                    Log.e(TAG, "Error in setItemValues callback for fetching fromUser in bg.", e);
                    return;
                }

                if(!viewHolder.requestObjectId.equals(request.getObjectId())){
                    Log.w(TAG, "ViewHolder object id=" + viewHolder.requestObjectId + ", request objectId=" + request.getObjectId());
                    return;
                }

                ParseFile imageFile = contactInfo.getParseFile("profileImage");
                if(imageFile == null){
                    Log.d(TAG, "Image file null for request from " + request.getFrom());
                    return;
                }

                String imageFileUrl = imageFile.getUrl();
                if(imageFileUrl == null){
                    Log.d(TAG, "Image file URL null for request from " + request.getFrom());
                    return;
                }

                Log.d("InboxAdapter", "inbox URL for pic: " + imageFileUrl);
                Picasso.with(getContext()).load(imageFileUrl).into(viewHolder.ivProfileImage);
            }
        });
    }
}
