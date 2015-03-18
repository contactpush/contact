package com.codepath.contact.fragments.sent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.codepath.contact.adapters.SentAdapter;
import com.codepath.contact.fragments.ListFragment;
import com.codepath.contact.models.Request;
import com.parse.ParseUser;

import java.util.List;

public class SentListFragment extends ListFragment {
    private static final String TAG = "SentListFragment";
    private OnSentListFragListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestsAdapter = new SentAdapter(getActivity(), requests);
    }

    protected void populateList() {
        Request.getSentRequestsInBackground(ParseUser.getCurrentUser().getUsername(), new Request.OnRequestsReturnedListener() {
            @Override
            public void receiveRequests(List<Request> requests) {
                swipeContainer.setRefreshing(false);
                requestsAdapter.addAll(requests);
            }
        });
    }

    @Override
    protected void setUpOnClickListener() {
        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request request = requestsAdapter.getItem(position);
                listener.onSentRequestClick(request);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSentListFragListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSentListFragListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSentListFragListener {
        void onSentRequestClick(Request request);
    }
}
