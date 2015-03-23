package com.codepath.contact.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.fragments.ContactsListFragment;
import com.codepath.contact.models.ContactInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactsAdapterLollipop extends RecyclerView.Adapter<ContactsAdapterLollipop.VH> {
    private static final String TAG = "ContactsAdapterLollipop";
    private Context mContext;
    private List<ContactInfo> mContacts;
    private final ContactsListFragment.ContactClickListener listener;
    private int primaryLight;
    private int accentColor;
    private int primaryText;
    private int secondaryText;

    public ContactsAdapterLollipop(Context context, List<ContactInfo> contacts) {
        mContext = context;
        if (!(context instanceof ContactsListFragment.ContactClickListener)){
            throw new ClassCastException("context must implement ContactsListFragment.ContactClickListener");
        }
        listener = (ContactsListFragment.ContactClickListener) context;
        if (contacts == null) {
            throw new IllegalArgumentException("contacts must not be null");
        }
        mContacts = contacts;

        accentColor = context.getResources().getColor(R.color.accent);
        secondaryText = context.getResources().getColor(R.color.secondary_text);
        primaryLight = context.getResources().getColor(R.color.primary_light);
        primaryText = context.getResources().getColor(R.color.primary_text);
    }

    @Override
    public ContactsAdapterLollipop.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new VH(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(final ContactsAdapterLollipop.VH holder, int position) {
        clearCachedProfileImage(holder);

        final ContactInfo contact = mContacts.get(position);
        holder.rootView.setTag(contact);

        switch (contact.getRequestStatus()){
            case INCOMING:
                holder.vPalette.setBackgroundColor(accentColor);
                break;
            case SENT:
                holder.vPalette.setBackgroundColor(primaryLight);
                break;
            case NA:
                break;
        }
        holder.tvName.setText(contact.getName());
        String imageFileUrl = contact.getProfileImage();
        if (imageFileUrl != null){
            Picasso.with(mContext).load(imageFileUrl).into(holder.ivProfileImage);
        }
    }

    private void clearCachedProfileImage(ContactsAdapterLollipop.VH holder){
        holder.ivProfileImage.setTag(null);
        holder.ivProfileImage.setImageResource(android.R.drawable.alert_dark_frame);
        holder.vPalette.setBackgroundColor(0x00000000);
        holder.tvName.setTextColor(primaryText);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public final class VH extends RecyclerView.ViewHolder {
        final View rootView;
        private ImageView ivProfileImage;
        private TextView tvName;
        View vPalette;

        public VH(View itemView, Context context) {
            super(itemView);
            rootView = itemView;
            vPalette = itemView.findViewById(R.id.vPalette);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ContactInfo contact = (ContactInfo) v.getTag();
                    if (contact != null) {
                        switch (contact.getRequestStatus()){
                            case INCOMING:
                                listener.onReceivedRequestClick(contact.getRequestObjectId());
                                break;
                            case SENT:
                                listener.onSentRequestClick(contact.getRequestObjectId());
                                break;
                            default:
                                listener.onContactClicked(contact.getObjectId());
                                break;
                        }
                    }
                }
            });
            }
        }
}
