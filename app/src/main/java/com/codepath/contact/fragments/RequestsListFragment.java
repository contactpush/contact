package com.codepath.contact.fragments;

import android.os.Bundle;

public class RequestsListFragment extends ListFragment {

    public static RequestsListFragment newInstance() {
        RequestsListFragment fragment = new RequestsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
