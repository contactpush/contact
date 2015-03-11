package com.codepath.contact.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.contact.R;

/**
 * Created by mekilah on 3/10/15.
 */
public class SentRequestInteractionFragment extends RequestInteractionFragment{

    public static SentRequestInteractionFragment newInstance(String name) {
        SentRequestInteractionFragment fragment = new SentRequestInteractionFragment();
        Bundle args = new Bundle();
        args.putString(NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        Button btnClose = (Button) v.findViewById(R.id.btnRight);
        btnClose.setText(getActivity().getResources().getString(R.string.close));
        Button btnRevoke = (Button) v.findViewById(R.id.btnLeft);
        btnRevoke.setText(getActivity().getResources().getString(R.string.revoke));
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnRevoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvName.setText(name);
        return v;
    }

}
