package com.codepath.contact.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.contact.models.AddressBook;
import com.codepath.contact.tasks.GetAuthTokenTask;
import com.codepath.contact.GoogleClient;
import com.codepath.contact.R;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;


public class LoginActivity extends ActionBarActivity implements GetAuthTokenTask.OnAuthTokenResolvedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1002;
    String email = "contacttestusr@gmail.com"; //hardcoding for now. need to persist
    // we may want to grab some of the data from the profile if the user is new
    private static final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String FULL_CONTACTS_SCOPE = "https://www.google.com/m8/feeds";
    private final static String scopes = "oauth2:" + PROFILE_SCOPE + " " + FULL_CONTACTS_SCOPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void loginToRest(View view) {
        getAuthToken();
    }

    /**
     * This method will prompt the user to select an account
     * and then begin the request for an auth token.
     */
    private void getAuthToken() {
        if (email == null) {
            String[] accountTypes = new String[]{"com.google"};
            Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                    accountTypes, false, null, null, null, null);
            startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
        } else {
            //if (isDeviceOnline()) {
            new GetAuthTokenTask(LoginActivity.this, email, scopes).execute();
            /*} else {
                Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
            }*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getAuthToken();
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            getAuthToken();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void receiveAuthToken(final String token) {
        GoogleClient.getFullAddressBook(token, email, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                AddressBook ab = AddressBook.getAddressBook(jsonObject);
                if (ab != null) Log.d(TAG, ab.toString());
                else Log.e(TAG, "Could not parse AddressBook");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString);
                Log.e(TAG, throwable.getMessage());
                // allegedly, if you try again when you get 401, it will work...
                //http://stackoverflow.com/questions/26969622/how-to-get-refreshtoken-when-using-googleauthutil
                if (statusCode == 401) {
                    GetAuthTokenTask.clearToken(LoginActivity.this, token);
                    getAuthToken();
                }
            }
        });
    }

    @Override
    public void handleAuthTokenException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
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
