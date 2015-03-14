package com.codepath.contact.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.contact.R;
import com.codepath.contact.adapters.ContactsAdapter;
import com.codepath.contact.models.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends Fragment implements ContactInfo.OnContactsReturnedListener {
    private static final String TAG = "Contacts";
    ContactsAdapter contactsAdapter;
    List<ContactInfo> contacts = new ArrayList<>();
    ListView lvContacts;
    ProgressBar pb;
    SwipeRefreshLayout swipeContainer;

    public static Contacts newInstance() {
        Contacts fragment = new Contacts();
        return fragment;
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
        return v;
    }

    @Override
    public void receiveContacts(List<ContactInfo> contactInfos) {
        pb.setVisibility(View.INVISIBLE);
        contactsAdapter.addAll(contactInfos);
    }
}
