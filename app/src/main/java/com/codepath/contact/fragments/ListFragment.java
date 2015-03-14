package com.codepath.contact.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.codepath.contact.R;
import com.codepath.contact.adapters.RequestsAdapter;
import com.codepath.contact.models.Request;

import java.util.ArrayList;
import java.util.List;

public abstract class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";
    protected ListView lvRequests;
    protected ProgressBar pb;
    protected SwipeRefreshLayout swipeContainer;
    protected RequestsAdapter requestsAdapter;
    protected List<Request> requests = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        lvRequests = (ListView) v.findViewById(R.id.lvContacts);
        lvRequests.setAdapter(requestsAdapter);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        setUpOnClickListener();
        return v;
    }

    public void addRequestToList(Request request){
        if (requestsAdapter != null) requestsAdapter.add(request);
        else Log.d(TAG, "guess what, requestsAdapter is null...");
    }

    public void refreshList(){
        this.requestsAdapter.clear();
        this.populateList();
    }

    protected abstract void setUpOnClickListener();

    protected abstract void populateList();
}
