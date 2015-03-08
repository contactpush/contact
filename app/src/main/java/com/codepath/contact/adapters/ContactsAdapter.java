package com.codepath.contact.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.AddressBookEntry;

import java.util.List;

public class ContactsAdapter extends ArrayAdapter<AddressBookEntry> {

    public ContactsAdapter(Context context, List<AddressBookEntry> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AddressBookEntry contact = getItem(position);
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
        viewHolder.tvName.setText(contact.getName().length() > 0 ? contact.getName() : contact.getEmail());
        return convertView;
    }

    private static class ViewHolder{
        private ImageView ivProfileImage;
        private TextView tvName;
    }
}
