package com.codepath.contact;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.codepath.contact.activities.LoginActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * https://developer.android.com/google/auth/http-auth.html
 */
public class GetUsernameTask extends AsyncTask {
    private static final String TAG = GetUsernameTask.class.getSimpleName();
    LoginActivity mActivity;
    String mScope;
    String mEmail;
    private final static String BOOKS_API_SCOPE
            = "https://www.googleapis.com/auth/books";
    private static final String SCOPE =
            "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private final static String GPLUS_SCOPE
            = "https://www.googleapis.com/auth/plus.login";
    private final static String mScopes
            = "oauth2:" + BOOKS_API_SCOPE + " " + GPLUS_SCOPE;

    public GetUsernameTask(Activity activity, String name, String scope) {
        this.mActivity = (LoginActivity) activity;
        this.mScope = scope;
        this.mEmail = name;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected Void doInBackground(Object[] params) {
        try {
            String token = fetchToken();
            if (token != null) {
                Log.d(TAG, "found token!: " + token);
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

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken() throws IOException {
        try {
            return GoogleAuthUtil.getToken(mActivity, mEmail, mScope);
        } catch (UserRecoverableAuthException userRecoverableException) {
            // GooglePlayServices.apk is either old, disabled, or not present
            // so we need to show the user some UI in the activity to recover.
            mActivity.handleException(userRecoverableException);
        } catch (GoogleAuthException fatalException) {
            // Some other type of unrecoverable exception has occurred.
            // Report and log the error as appropriate for your app.
            //...
        }
        return null;
    }
    //...
}