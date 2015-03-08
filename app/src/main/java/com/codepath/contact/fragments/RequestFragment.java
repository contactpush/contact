package com.codepath.contact.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.contact.R;

public class RequestFragment extends DialogFragment {
    private static final String NAME = "name";
    private String name;

    public static RequestFragment newInstance(String name) {
        RequestFragment fragment = new RequestFragment();
        Bundle args = new Bundle();
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        Button btAccept = (Button) v.findViewById(R.id.btAccept);
        Button btDecline = (Button) v.findViewById(R.id.btDecline);
        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvName.setText(name);
        return v;
    }


}
