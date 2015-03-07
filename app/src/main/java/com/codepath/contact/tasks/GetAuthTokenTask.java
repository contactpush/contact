package com.codepath.contact.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

public class GetAuthTokenTask extends AsyncTask<Object[], Void, String>  {
    private static final String TAG = GetAuthTokenTask.class.getSimpleName();
    Activity activity;
    OnAuthTokenResolvedListener listener;
    String scope;
    String email;

    public interface OnAuthTokenResolvedListener{
        void receiveAuthToken(String token);
        void handleAuthTokenException(Exception e);
    }

    public GetAuthTokenTask(Activity activity, String email, String scope) {
        if (!(activity instanceof OnAuthTokenResolvedListener))
            throw new ClassCastException("Class must implement OnAuthTokenResolvedListener");
        this.activity =  activity;
        this.listener = (OnAuthTokenResolvedListener) activity;
        this.scope = scope;
        this.email = email;
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
            }
        } catch (IOException e) {
            Log.d(TAG, "error getting token!: " + e.getMessage());
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
            //String token = GoogleAuthUtil.getToken(activity, email, scope);
            //GoogleAuthUtil.clearToken(activity, token);
            return GoogleAuthUtil.getToken(activity, email, scope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            listener.handleAuthTokenException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            Log.e(TAG, fatalException.getMessage());
        }
        return null;
    }

    public static void clearToken(Activity activity, String token){
        try {
            GoogleAuthUtil.clearToken(activity, token);
        } catch (GoogleAuthException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}