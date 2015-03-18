package com.codepath.contact.fragments.inbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.codepath.contact.adapters.InboxAdapter;
import com.codepath.contact.fragments.ListFragment;
import com.codepath.contact.models.Request;
import com.parse.ParseUser;

import java.util.List;

public class InboxListFragment extends ListFragment implements Request.OnRequestsReturnedListener {
    private static final String TAG = "InboxListFragment";
    private OnRequestListFragListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestsAdapter = new InboxAdapter(getActivity(), requests);
    }

    @Override
    public void receiveRequests(List<Request> requests) {
        requestsAdapter.addAll(requests);
    }

    @Override
    public void setUpOnClickListener(){
        lvRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Request request = requestsAdapter.getItem(position);
                listener.onReceivedRequestClick(request);
            }
        });
        pb.setVisibility(View.INVISIBLE);
    }

    protected void populateList(){
        Request.getRequestsInBackground(ParseUser.getCurrentUser().getUsername(), new Request.OnRequestsReturnedListener() {
            @Override
            public void receiveRequests(List<Request> requests) {
                swipeContainer.setRefreshing(false);
                requestsAdapter.addAll(requests);
            }
        });
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
