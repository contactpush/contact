package com.codepath.contact.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codepath.contact.GoogleClient;
import com.codepath.contact.activities.LoginActivity;
import com.codepath.contact.models.AddressBook;
import com.codepath.contact.tasks.GetAuthTokenTask;
import com.codepath.contact.tasks.GetAuthTokenTask.OnAuthTokenResolvedListener;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ContactsListFragment extends ListFragment implements OnAuthTokenResolvedListener{
    private static final String TAG = "ContactsListFragment";
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;

    public static ContactsListFragment newInstance() {
        ContactsListFragment fragment = new ContactsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetAuthTokenTask(getActivity(), email, LoginActivity.SCOPES).execute();
    }

    @Override
    public void receiveAuthToken(final String token) {
        GoogleClient.getFullAddressBook(token, email, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                AddressBook ab = AddressBook.getAddressBook(jsonObject);
                contactsAdapter.addAll(ab.getAddressBook());
                if (ab != null) {
                    Log.d(TAG, ab.toString());
                } else {
                    Log.e(TAG, "Could not parse AddressBook");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "Auth Token retrieval failed: " + responseString);
                Log.e(TAG, "Auth Token retrieval failed: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void handleAuthTokenException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    /*Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            HelloActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show(); */
                    Log.d(this.getClass().getSimpleName(), "Play services error: " + statusCode);
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

}
