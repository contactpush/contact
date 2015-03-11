package com.codepath.contact.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;

/**
 * Created by mekilah on 3/10/15.
 */
public class SentRequestInteractionFragment extends RequestInteractionFragment{
    private static final String TAG = "SentRequestInteractionFragment";

    public static SentRequestInteractionFragment newInstance(Request request) {
        SentRequestInteractionFragment fragment = new SentRequestInteractionFragment();
        fragment.request = request;
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
                request.deleteInBackground(new DeleteCallback(){
                    @Override
                    public void done(ParseException e){
                        if(e != null){
                            Log.e(TAG, "Error with background delete revoke", e);
                            return;
                        }
                        listener.shouldUpdateRequestList(RequestsListFragment.Type.SENT);
                    }
                });
                dismiss();
            }
        });
        tvName.setText(request.getTo());
        return v;
    }

}
