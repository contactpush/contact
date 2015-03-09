package com.codepath.contact.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codepath.contact.GoogleApplication;
import com.codepath.contact.R;
import com.codepath.contact.activities.LoginActivity;
import com.google.android.gms.common.AccountPicker;

public class LoginFragment extends Fragment {
    private GoogleApplication.ParseAccountCreationListener listener;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1002;
    String email = "contacttestusr@gmail.com"; //hardcoding for now. need to persist
    // we may want to grab some of the data from the profile if the user is new
    private static final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String FULL_CONTACTS_SCOPE = "https://www.google.com/m8/feeds";
    public final static String SCOPES = "oauth2:" + PROFILE_SCOPE + " " + FULL_CONTACTS_SCOPE;
    private EditText etUserName;
    private EditText etPassword;
    private static final String CONTACT_PREFERENCES = "ContactPreferences";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private String userName;
    private String password;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        etUserName = (EditText) v.findViewById(R.id.etUserName);
        etPassword = (EditText) v.findViewById(R.id.etPassword);
        return v;
    }

    public void loginToRest(View view) {
        getAuthToken();
    }

    public void createAccount(View view) {
        if (validateCredentials()) {
            getAuthToken();
        }
    }

    //TODO implement better validation logic
    private boolean validateCredentials() {
        userName = etUserName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if (userName == null || userName.length() < 2) {
            etUserName.setError("Invalid username");
            return false;
        }
        if (password == null || password.length() < 2) {
            etPassword.requestFocus();
            etPassword.setError("Invalid password");
            return false;
        }
        return true;
    }

    /**
     * This method will prompt the user to select an account
     * and then begin the request for an auth token.
     */
    public void getAuthToken() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    public void signUpUserWithParse(String email){
        GoogleApplication.signUpWithParse(userName, password, email,
                new GoogleApplication.ParseAccountCreationListener() {
                    @Override
                    public void onAccountCreationResponse(boolean success) {
                        if (success) {
                            // production version should use a more secure storage space for password
                            SharedPreferences.Editor editor =
                                    getActivity().getSharedPreferences(CONTACT_PREFERENCES, getActivity().MODE_PRIVATE).edit();
                            editor.putString(USERNAME, userName);
                            editor.putString(PASSWORD, password);
                            editor.commit();
                            listener.onAccountCreationResponse(success);
                        } else {
                            listener.onAccountCreationResponse(success);
                        }
                    }
                });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (GoogleApplication.ParseAccountCreationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ParseAccountCreationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
