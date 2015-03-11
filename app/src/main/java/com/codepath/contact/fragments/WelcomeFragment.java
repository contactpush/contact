package com.codepath.contact.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.contact.GoogleApplication;
import com.codepath.contact.R;

public class WelcomeFragment extends Fragment {
    private static final String TAG = "WelcomeFragment";
    private static final String CONTACT_PREFERENCES = "ContactPreferences";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private ProgressBar pbPloading;
    private InitialAppStartupListener listener;

    public interface InitialAppStartupListener{
        void onFinishedLoading(boolean success);
    }

    public static WelcomeFragment newInstance(){
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForExistingAccount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        pbPloading = (ProgressBar) v.findViewById(R.id.pbLoading);
        pbPloading.setVisibility(ProgressBar.VISIBLE);
        return v;
    }

    private void checkForExistingAccount() {
        SharedPreferences prefs = getActivity().getSharedPreferences(CONTACT_PREFERENCES, getActivity().MODE_PRIVATE);
        String userName = prefs.getString(USERNAME, null);
        String password = prefs.getString(PASSWORD, null);

        if (userName == null || password == null) {
            listener.onFinishedLoading(false);
            return;
        }

        GoogleApplication.signIntoParse(userName, password, new GoogleApplication.ParseLoginListener() {
            @Override
            public void onLoginResponse(boolean success) {
                pbPloading.setVisibility(ProgressBar.INVISIBLE);
                listener.onFinishedLoading(success);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof InitialAppStartupListener)){
            throw new ClassCastException("Activity must implement InitialAppStartupListener");
        }
        listener = (InitialAppStartupListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
