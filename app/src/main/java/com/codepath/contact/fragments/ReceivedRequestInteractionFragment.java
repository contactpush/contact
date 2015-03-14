package com.codepath.contact.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.ContactInfo;
import com.codepath.contact.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class ReceivedRequestInteractionFragment extends DialogFragment {
    private static final String TAG = "ReceivedReqIntFrag";
    protected Request request;
    protected RequestInteractionFragmentListener listener;

    public interface RequestInteractionFragmentListener{
        public void updateInbox();
    }

    public static ReceivedRequestInteractionFragment newInstance(Request request) {
        ReceivedRequestInteractionFragment fragment = new ReceivedRequestInteractionFragment();
        fragment.request = request;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        TextView tvName = (TextView) v.findViewById(R.id.tvName);
        Button btAccept = (Button) v.findViewById(R.id.btnRight);
        Button btDecline = (Button) v.findViewById(R.id.btnLeft);
        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setApprovedStatus(true);
                request.saveInBackground(new SaveCallback(){
                    @Override
                    public void done(ParseException e){
                        if(e != null){
                            Log.e(TAG, "Error with background save accept", e);
                            return;
                        }
                        listener.updateInbox();
                    }
                });
                dismiss();
            }
        });
        btDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.deleteInBackground(new DeleteCallback(){
                    @Override
                    public void done(ParseException e){
                        if(e != null){
                            Log.e(TAG, "Error with background delete deny", e);
                            return;
                        }
                        listener.updateInbox();
                    }
                });
                dismiss();
            }
        });
        ContactInfo fromUser = request.getFromUser();
        tvName.setText(fromUser.getName());
        return v;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        if (!(activity instanceof RequestInteractionFragmentListener)){
            throw new ClassCastException("Activity must implement RequestInteractionFragmentListener");
        }
        listener = (RequestInteractionFragmentListener)activity;
    }
}
