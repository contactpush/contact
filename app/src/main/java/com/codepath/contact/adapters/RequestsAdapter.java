package com.codepath.contact.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.Request;

import java.util.List;

public abstract class RequestsAdapter extends ArrayAdapter<Request> {

    public RequestsAdapter(Context context, List<Request> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Request request = getItem(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.requestObjectId = request.getObjectId();
        setItemValues(viewHolder, request);
        return convertView;
    }

    abstract void setItemValues(ViewHolder viewHolder, Request request);

    static class ViewHolder{
        ImageView ivProfileImage;
        TextView tvName;

        /**
         * remember requestObjectId so we don't async-ly load the wrong data in this viewholder
         */
        String requestObjectId;
    }
}
