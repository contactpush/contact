package com.codepath.contact.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.codepath.contact.models.AddressBookEntry;
import com.codepath.contact.models.ContactInfo;
import com.codepath.contact.models.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class RequestsListFragment extends ListFragment {
    private static final String TAG = "RequestsListFragment";

    private boolean isLookingForReceivedRequests = true;

    public static RequestsListFragment newInstance(boolean forReceivedRequests) {
        RequestsListFragment fragment = new RequestsListFragment();
        fragment.isLookingForReceivedRequests = forReceivedRequests;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "creating requests...");
        populateList();
        //getMyContactInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setUpOnClickListener();
        return v;
    }

    private void setUpOnClickListener(){
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddressBookEntry abe = contactsAdapter.getItem(position);
                String name = abe.getName();
                mListener.onRequestClick(name);
            }
        });
    }

    private void populateList(){
        final ParseQuery<ParseObject> request = ParseQuery.getQuery("Request");
        request.whereEqualTo(isLookingForReceivedRequests ? "userId" : "requesterId", userName);
        request.whereEqualTo("approved", false);
        request.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> requests, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + requests.size() + " requests");
                    for (int i = 0; i < requests.size(); i++){
                        Request r = (Request) requests.get(i);
                        RequestsListFragment.this.addRequestToList(r);
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addRequestToList(Request request){
        AddressBookEntry a = new AddressBookEntry();
        a.setName(request.getRequesterName());
        contactsAdapter.add(a);
    }

    private void createRequest(ContactInfo contactInfo){
        Request r = new Request();
        r.setUserId("contacttestusr");
        r.setRequesterId("contacttestusr2");
        r.put("parent", contactInfo);
        r.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d(TAG, "save success");
                } else {
                    Log.d(TAG, "save failed: " + e.getMessage());
                }
            }
        });
    }

    private void getMyContactInfo(){
        ParseQuery<ParseObject> request = ParseQuery.getQuery("ContactInfo");
        request.whereEqualTo("userId", userName);
        request.findInBackground(new FindCallback<ParseObject>(){
            public void done(List<ParseObject> requests, ParseException e){
                if(e == null){
                    Log.d(TAG, "Retrieved " + requests.size() + " requests");
                    if(requests.size() > 0){
                        ContactInfo c = (ContactInfo) requests.get(0);
                        Log.d(TAG, "Name: " + c.getName());
                        createRequest(c);
                    }
                }else{
                    Log.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
