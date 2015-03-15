package com.codepath.contact.fragments.contacts;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.contact.R;
import com.codepath.contact.adapters.ContactsAdapter;
import com.codepath.contact.models.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class ContactsListFragment extends Fragment implements ContactInfo.OnContactsReturnedListener {
    private static final String TAG = "ContactsListFragment";
    ContactsAdapter contactsAdapter;
    List<ContactInfo> contacts = new ArrayList<>();
    ListView lvContacts;
    ProgressBar pb;
    SwipeRefreshLayout swipeContainer;
    ContactClickListener listener;

    public interface ContactClickListener{
        void onContactClicked(String objectId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactsAdapter = new ContactsAdapter(getActivity(), contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        lvContacts = (ListView) v.findViewById(R.id.lvContacts);
        lvContacts.setAdapter(contactsAdapter);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        pb.setVisibility(View.VISIBLE);
        ContactInfo.getContacts(this);
        setUpOnClickListener();
        return v;
    }

    private void setUpOnClickListener(){
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo c = contactsAdapter.getItem(position);
                String objectId = c.getObjectId();
                listener.onContactClicked(objectId);
            }
        });
    }

    @Override
    public void receiveContacts(List<ContactInfo> contactInfos) {
        pb.setVisibility(View.INVISIBLE);
        contactsAdapter.addAll(contactInfos);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ContactClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ContactClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
