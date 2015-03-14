package com.codepath.contact.adapters;

import android.content.Context;

import com.codepath.contact.models.Request;

import java.util.List;

public class InboxAdapter extends RequestsAdapter {

    public InboxAdapter(Context context, List<Request> objects) {
        super(context, objects);
    }

    void setItemValues(ViewHolder viewHolder, Request request){
        viewHolder.tvName.setText(request.getFrom());
    }
}
