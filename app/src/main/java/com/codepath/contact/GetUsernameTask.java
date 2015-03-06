package com.codepath.contact;

import android.accounts.AccountManager;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.codepath.contact.activities.LoginActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

import java.io.IOException;
import java.net.URL;

/**
 * https://developer.android.com/google/auth/http-auth.html
 */
public class GetUsernameTask extends AsyncTask<Object[], Void, String>  {
    private static final String TAG = GetUsernameTask.class.getSimpleName();
    LoginActivity mActivity;
    String mScope;
    String mEmail;

    public GetUsernameTask(Activity activity, String email, String scope) {
        this.mActivity = (LoginActivity) activity;
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
        AsyncHttpClient client = new AsyncHttpClient();
        String headerValue = "Bearer " + token;
        Log.d(TAG, "Authorization: " + headerValue);
        client.addHeader("Authorization", headerValue);
        RequestParams params = new RequestParams();
        params.add("alt", "json");
        String url = "https://www.google.com/m8/feeds/contacts/" + mEmail + "/full";
        client.get(mActivity, url, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG, "success: " + new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "fail: " + new String(responseBody));
                    }
                });
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