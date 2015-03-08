package com.codepath.contact.fragments;

import android.os.Bundle;
import android.util.Log;
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

    public static RequestsListFragment newInstance() {
        RequestsListFragment fragment = new RequestsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "creating requests...");
        populateList();
        //getMyContactInfo();
    }

    private void populateList(){
        final ParseQuery<ParseObject> request = ParseQuery.getQuery("Request");
        request.whereEqualTo("userId", userName);
        request.whereEqualTo("approved", false);
        request.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> requests, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + requests.size() + " requests");
                    if (requests.size() > 0){
                        for (int i = 0; i < requests.size(); i++){
                            Request r = (Request) requests.get(0);
                            AddressBookEntry a = new AddressBookEntry();
                            a.setName(r.getRequesterName());
                            contactsAdapter.add(a);
                        }
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        request.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> requests, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + requests.size() + " requests");
                    if (requests.size() > 0){
                        ContactInfo c = (ContactInfo) requests.get(0);
                        Log.d(TAG, "Name: " + c.getName());
                        createRequest(c);
                    }
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
