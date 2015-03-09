package com.codepath.contact.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.contact.GoogleApplication;
import com.codepath.contact.R;
import com.codepath.contact.fragments.LoginFragment;
import com.codepath.contact.fragments.WelcomeFragment;
import com.codepath.contact.tasks.GetAuthTokenTask;
import com.codepath.contact.tasks.GetAuthTokenTask.OnAuthTokenResolvedListener;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;


public class LoginActivity extends ActionBarActivity implements OnAuthTokenResolvedListener,
        WelcomeFragment.InitialAppStartupListener, GoogleApplication.ParseAccountCreationListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1002;
    String email;
    // we may want to grab some of the data from the profile if the user is new
    private static final String PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String FULL_CONTACTS_SCOPE = "https://www.google.com/m8/feeds";
    public final static String SCOPES = "oauth2:" + PROFILE_SCOPE + " " + FULL_CONTACTS_SCOPE;
    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flLogin, welcomeFragment);
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                //if (isDeviceOnline()) {
                new GetAuthTokenTask(this, email, SCOPES).execute();
                /*} else {
                    Toast.makeText(this, R.string.not_online, Toast.LENGTH_LONG).show();
                }*/
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            // Receiving a result that follows a GoogleAuthException, try auth again
            loginFragment.getAuthToken();
        }
    }

    @Override
    public void receiveAuthToken(String token) {
        loginFragment.signUpUserWithParse(email);
    }

    private void startMainActivity() {
        Intent i = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(i);
    }

    @Override
    public void onFinishedLoading(boolean success) {
        if (success){
            startMainActivity();
            finish();
        } else {
            loginFragment = LoginFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flLogin, loginFragment);
            transaction.commit();
        }
    }

    @Override
    public void onAccountCreationResponse(boolean success) {
        if (success){
            startMainActivity();
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Parse Account Creation failed.", Toast.LENGTH_LONG).show();
        }
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
                    Toast.makeText(LoginActivity.this, "Play services error.", Toast.LENGTH_LONG).show();
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
}
