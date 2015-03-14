package com.codepath.contact.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.codepath.contact.adapters.InboxAdapter;
import com.codepath.contact.models.Request;
import com.parse.ParseUser;

import java.util.List;

public class InboxListFragment extends ListFragment implements Request.OnRequestsReturnedListener {
    private static final String TAG = "RequestsListFragment";
    private OnRequestListFragListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestsAdapter = new InboxAdapter(getActivity(), requests);
        populateList();
    }

    @Override
    public void receiveRequests(List<Request> requests) {
        requestsAdapter.addAll(requests);
    }

    @Override
    void setUpOnClickListener(){
        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request request = requestsAdapter.getItem(position);
                listener.onReceivedRequestClick(request);
            }
        });
    }

    public void refreshList(){
        this.requestsAdapter.clear();
        this.populateList();
    }

    private void populateList(){
        Request.getRequestsInBackground(ParseUser.getCurrentUser().getUsername(), new Request.OnRequestsReturnedListener() {
            @Override
            public void receiveRequests(List<Request> requests) {
                requestsAdapter.addAll(requests);
            }
        });
    }

    public void addRequestToList(Request request){
        requestsAdapter.add(request);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnRequestListFragListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRequestListFragListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnRequestListFragListener {
        void onReceivedRequestClick(Request request);
    }
}
