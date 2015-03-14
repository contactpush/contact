package com.codepath.contact.fragments.sent;

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
import com.codepath.contact.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;


public class SentRequestInteractionFragment extends DialogFragment{
    private static final String TAG = "SentRequestInteractionFragment";
    protected Request request;
    protected SentRequestInteractionFragmentListener listener;

    public interface SentRequestInteractionFragmentListener{
        public void updateSent();
    }

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
                        listener.updateSent();
                    }
                });
                dismiss();
            }
        });
        tvName.setText(request.getTo());
        return v;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        if (!(activity instanceof SentRequestInteractionFragmentListener)){
            throw new ClassCastException("Activity must implement SentRequestInteractionFragmentListener");
        }
        listener = (SentRequestInteractionFragmentListener)activity;
    }
}
