package com.codepath.contact.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.contact.R;
import com.codepath.contact.fragments.ContactsListFragment;
import com.codepath.contact.fragments.ReceivedRequestInteractionFragment;
import com.codepath.contact.fragments.SentRequestInteractionFragment;
import com.codepath.contact.helpers.LocationHelper;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LandingActivity extends ActionBarActivity implements ReceivedRequestInteractionFragment.RequestInteractionFragmentListener,
        SentRequestInteractionFragment.SentRequestInteractionFragmentListener,
        ContactsListFragment.ContactClickListener,
        LocationHelper.LocationHelperListener{
    private static final String TAG = "LandingActivity";

    private static final int ADD_USER = 432;

    private ProgressBar pb;
    private Toolbar toolbar;

    private LocationHelper locationHelper;

    private static final String CONTACT_PREFERENCES = "ContactPreferences";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private ContactsListFragment contactsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        setTheme(R.style.Theme_Contact);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = (ProgressBar) findViewById(R.id.pbLoading);
        getSupportFragmentManager();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        contactsListFragment = new ContactsListFragment();
        ft.replace(R.id.fragment_test, contactsListFragment);
        ft.commit();

        locationHelper = new LocationHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_add_contact){
            this.addContactButtonPressed();
            return true;
        }

        if (id == R.id.action_create_profile) {
            createProfileButtonPressed(null);
            return true;
        }

        if (id == R.id.action_log_out) {
            logOut();
            return true;
        }

        if(id == R.id.action_update_location){
            this.updateCurrentLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentLocation(){
        this.locationHelper.getRecentLocation(this);
    }

    private void logOut(){
        SharedPreferences.Editor editor =
                getSharedPreferences(CONTACT_PREFERENCES, MODE_PRIVATE).edit();
        editor.remove(USERNAME);
        editor.remove(PASSWORD);
        editor.commit();
        ParseUser.logOut();

        // Bring us to the login screen, clear the task stack
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void addContactButtonPressed(){
        //go to AddContactActivity
        startActivityForResult(new Intent(this, AddContactActivity.class), LandingActivity.ADD_USER);
        overridePendingTransition(R.anim.right_in, R.anim.leftt_out);
    }

    public void createProfileButtonPressed(String objectId){
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("objectId", objectId);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == LandingActivity.ADD_USER){
            switch(resultCode){
                case AddContactActivity.SUCCESSFUL_REQUEST:
                    contactsListFragment.refreshList();
                    break;

                case AddContactActivity.FAILED_REQUEST:
                    Log.d(TAG, "request failed");
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onReceivedRequestClick(String objectId) {
        ReceivedRequestInteractionFragment receivedRequestInteractionFragment =
                ReceivedRequestInteractionFragment.newInstance(objectId);
        receivedRequestInteractionFragment.show(getSupportFragmentManager(), "fragment_request");
    }

    @Override
    public void onSentRequestClick(String objectId) {
        SentRequestInteractionFragment requestInteractionFragment = SentRequestInteractionFragment.newInstance(objectId);
        requestInteractionFragment.show(getSupportFragmentManager(), "fragment_sent_request");
    }

    @Override
    public void updateContacts(){
        contactsListFragment.refreshList();
    }

    @Override
    public void onContactClicked(String objectId) {
        createProfileButtonPressed(objectId);
    }

    @Override
    public void onLocationUpdated(Location location){
        Log.d(TAG, "location updated: " + location.toString());
        ParseUser.getCurrentUser().put("lastLocation", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback(){
            @Override
            public void done(ParseException e){
                Toast.makeText(LandingActivity.this, "Saved location!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
