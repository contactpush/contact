package com.codepath.contact.fragments;

import android.app.Activity;
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
import com.codepath.contact.models.AddressBookEntry;

import java.util.ArrayList;
import java.util.List;
// TODO Turns out ContactsListFragment and RequestsListFragment are not
// similar enough to make inheritance a good strategy. Need to refactor...
public abstract class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";
    final String userName = "contacttestusr"; //hardcoding for now. need to persist
    final String email = "contacttestusr@gmail.com";
    ContactsAdapter contactsAdapter;
    List<AddressBookEntry> contacts = new ArrayList<>();
    ListView lvContacts;
    ProgressBar pb;
    SwipeRefreshLayout swipeContainer;

    OnFragmentInteractionListener mListener;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pb = (ProgressBar) getActivity().findViewById(R.id.pbLoading);
        contactsAdapter = new ContactsAdapter(getActivity(), contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        lvContacts = (ListView) v.findViewById(R.id.lvContacts);
        lvContacts.setAdapter(contactsAdapter);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onReceivedRequestClick(String name);
        void onSentRequestClick(String name);
    }

}