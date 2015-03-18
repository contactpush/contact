package com.codepath.contact.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.codepath.contact.models.Request;

import java.util.List;

public class InboxAdapter extends RequestsAdapter {
    private static final String TAG = "InboxAdapter";

    public InboxAdapter(Context context, List<Request> objects) {
        super(context, objects);
    }

    void setItemValues(ViewHolder viewHolder, Request request){
        viewHolder.tvName.setText(request.getFrom());
        byte[] photo = request.getFromUser().getProfileImage();
        if (photo != null && photo.length > 0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            viewHolder.ivProfileImage.setImageBitmap(bitmap);
        } else {
            Log.d(TAG, "Image for requested contact is null");
        }
    }
}
