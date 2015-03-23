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
import com.codepath.contact.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;


public class SentRequestInteractionFragment extends DialogFragment{
    private static final String TAG = "SentRequestIntFrag";
    public static final String REQUEST_OBJ_ID = "objectId";
    protected Request request;
    private String requestObjId;
    protected SentRequestInteractionFragmentListener listener;
    private Button btnRevoke;
    private TextView tvName;

    public interface SentRequestInteractionFragmentListener{
        public void updateContacts();
    }

    public static SentRequestInteractionFragment newInstance(String requestObjId) {
        SentRequestInteractionFragment fragment = new SentRequestInteractionFragment();
        Bundle args = new Bundle();
        args.putString(REQUEST_OBJ_ID, requestObjId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        TextView tvRequestFragmentTitle = (TextView) v.findViewById(R.id.tvRequestFragmentTitle);
        tvRequestFragmentTitle.setText("Sent Request");
        tvName = (TextView) v.findViewById(R.id.tvName);
        Button btnClose = (Button) v.findViewById(R.id.btnRight);
        btnClose.setText(getActivity().getResources().getString(R.string.close));
        btnRevoke = (Button) v.findViewById(R.id.btnLeft);
        btnRevoke.setText(getActivity().getResources().getString(R.string.revoke));
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        fetchRequest();
        return v;
    }

    private void fetchRequest(){
        if (getArguments() == null) return;
        requestObjId = getArguments().getString(REQUEST_OBJ_ID);
        Request.getRequestForObjectId(requestObjId, new Request.OnRequestReturnedListener() {
            @Override
            public void receiveRequest(Request request) {
                SentRequestInteractionFragment.this.request = request;
                setValues();
            }
        });
    }

    private void setValues(){
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
                        listener.updateContacts();
                    }
                });
                dismiss();
            }
        });
        tvName.setText(request.getTo());
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
