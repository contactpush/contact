package com.codepath.contact.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.contact.R;
import com.codepath.contact.models.Request;

public abstract class RequestInteractionFragment extends DialogFragment {
    protected Request request;
    protected RequestInteractionFragmentListener listener;

    public interface RequestInteractionFragmentListener{
        public void shouldUpdateRequestList(RequestsListFragment.Type whichList);
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
