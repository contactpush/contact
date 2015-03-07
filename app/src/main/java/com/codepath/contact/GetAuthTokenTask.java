package com.codepath.contact;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * https://developer.android.com/google/auth/http-auth.html
 */
public class GetAuthTokenTask extends AsyncTask<Object[], Void, String>  {
    private static final String TAG = GetAuthTokenTask.class.getSimpleName();
    Activity mActivity;
    OnAuthTokenResolvedListener listener;
    String mScope;
    String mEmail;

    public interface OnAuthTokenResolvedListener{
        void receiveAuthToken(String token);
        void handleAuthTokenException(Exception e);
    }

    public GetAuthTokenTask(Activity activity, String email, String scope) {
        if (!(activity instanceof OnAuthTokenResolvedListener))
            throw new ClassCastException("Class must implement OnAuthTokenResolvedListener");
        this.mActivity =  activity;
        this.listener = (OnAuthTokenResolvedListener) activity;
        this.mScope = scope;
        this.mEmail = email;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected String doInBackground(Object[]... params) {
        try {
            String token = fetchToken();
            if (token != null) {
                return token;
                // Insert the good stuff here.
                // Use the token to access the user's Google data.
                //...

            }
        } catch (IOException e) {
            Log.d(TAG, "error getting token!: " + e.getMessage());
            // The fetchToken() method handles Google-specific exceptions,
            // so this indicates something went wrong at a higher level.
            // TIP: Check for network connectivity before starting the AsyncTask.
            //...
        }
        return null;
    }

    @Override
    protected void onPostExecute(final String token) {
        listener.receiveAuthToken(token);
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            //TODO add in mechanism to check if token is valid rather than clearing each time
            //clear cached token
            String token = GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
            GoogleAuthUtil.clearToken(mActivity, token);
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            listener.handleAuthTokenException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            //...
        }
        return null;
    }
    //...
}