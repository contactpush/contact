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
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.ContactInfo;
import com.codepath.contact.models.Request;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

public class ReceivedRequestInteractionFragment extends DialogFragment {
    private static final String TAG = "ReceivedReqIntFrag";
    public static final String SENT_OBJ_ID = "objectId";
    protected Request request;
    protected RequestInteractionFragmentListener listener;
    private String requestObjId;
    private ImageView ivProfileImage;
    private TextView tvName;
    private Button btAccept;
    private Button btDecline;

    public interface RequestInteractionFragmentListener{
        public void updateContacts();
    }

    public static ReceivedRequestInteractionFragment newInstance(String requestObjId) {
        ReceivedRequestInteractionFragment fragment = new ReceivedRequestInteractionFragment();
        Bundle args = new Bundle();
        args.putString(SENT_OBJ_ID, requestObjId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_request, container, false);
        TextView tvRequestFragmentTitle = (TextView) v.findViewById(R.id.tvRequestFragmentTitle);
        tvRequestFragmentTitle.setText("Incoming Request");
        tvName = (TextView) v.findViewById(R.id.tvName);
        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        btAccept = (Button) v.findViewById(R.id.btnRight);
        btDecline = (Button) v.findViewById(R.id.btnLeft);
        fetchRequest();
        return v;
    }

    private void fetchRequest(){
        if (getArguments() == null) return;
        requestObjId = getArguments().getString(SENT_OBJ_ID);
        Request.getRequestForObjectId(requestObjId, new Request.OnRequestReturnedListener() {
            @Override
            public void receiveRequest(Request request) {
                ReceivedRequestInteractionFragment.this.request = request;
                setValues();
            }
        });
    }

    private void setValues(){
        btAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.setApprovedStatus(true);
                request.saveInBackground(new SaveCallback(){
                    @Override
                    public void done(ParseException e){
                        if(e != null){
                            Log.e(TAG, "Error with background save accept", e);
                        } else {
                            listener.updateContacts();
                        }
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
                        } else {
                            listener.updateContacts();
                        }
                    }
                });
                dismiss();
            }
        });
        ContactInfo fromUser = request.getFromUser();
        tvName.setText(fromUser.getName());
        String imageFileUrl = fromUser.getProfileImage();
        if (imageFileUrl != null){
            Picasso.with(getActivity()).load(imageFileUrl).into(ivProfileImage);
        }
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
