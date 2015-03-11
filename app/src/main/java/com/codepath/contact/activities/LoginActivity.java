package com.codepath.contact.activities;

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
import com.codepath.contact.fragments.CreateAccountFragment;
import com.codepath.contact.fragments.WelcomeFragment;
import com.codepath.contact.tasks.GetAuthTokenTask.OnAuthTokenResolvedListener;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;


public class LoginActivity extends ActionBarActivity implements OnAuthTokenResolvedListener,
        WelcomeFragment.InitialAppStartupListener, GoogleApplication.ParseAccountCreationListener {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private CreateAccountFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WelcomeFragment welcomeFragment = WelcomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flLogin, welcomeFragment);
        transaction.commit();
    }

    private void startMainActivity() {
        Intent i = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(i);
    }

    /**
     * This method is called when the Welcome page finishes loading.
     * The Welcome page attempts to find the user's Parse credentials
     * and log them in.  If it succeeds, then we take the user to the
     * landing page.  If not, we start the login screen.
     * @param success
     */
    @Override
    public void onFinishedLoading(boolean success) {
        if (success){
            startMainActivity();
            finish();
        } else {
            loginFragment = CreateAccountFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flLogin, loginFragment);
            transaction.commit();
        }
    }

    /**
     * This is called by the GetGoogleAuthTokenTask upon
     * completion.  Next step is to sign the user up with
     * Parse.
     * @param token
     */
    @Override
    public void receiveAuthToken(String token) {
        loginFragment.signUpUserWithParse();
    }

    /**
     * This method is called by the login screen after the user
     * has attempted to create an account with parse.  May want to
     * consider having the login screen go straight to the landing
     * page.
     * @param success
     */
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
        if (item.getItemId() == R.id.action_create_profile) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
