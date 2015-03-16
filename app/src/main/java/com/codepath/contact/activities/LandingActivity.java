package com.codepath.contact.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.contact.R;
import com.codepath.contact.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.contact.fragments.contacts.ContactsListFragment;
import com.codepath.contact.fragments.inbox.InboxListFragment;
import com.codepath.contact.fragments.inbox.ReceivedRequestInteractionFragment;
import com.codepath.contact.fragments.sent.SentListFragment;
import com.codepath.contact.fragments.sent.SentRequestInteractionFragment;
import com.codepath.contact.helpers.LocationHelper;
import com.codepath.contact.models.Request;
import com.codepath.contact.tasks.GetAuthTokenTask.OnAuthTokenResolvedListener;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LandingActivity extends ActionBarActivity implements InboxListFragment.OnRequestListFragListener,
        OnAuthTokenResolvedListener, ReceivedRequestInteractionFragment.RequestInteractionFragmentListener,
        SentRequestInteractionFragment.SentRequestInteractionFragmentListener,
        SentListFragment.OnSentListFragListener, ContactsListFragment.ContactClickListener, LocationHelper.LocationHelperListener{
    private static final String TAG = "LandingActivity";

    private static final int ADD_USER = 432;

    private ViewPager vpPager;
    private ContactPagerAdapter pagerAdapter;

    private ProgressBar pb;
    private Toolbar toolbar;

    private LocationHelper locationHelper;

    private static final String CONTACT_PREFERENCES = "ContactPreferences";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = (ProgressBar) findViewById(R.id.pbLoading);getSupportFragmentManager();

        vpPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ContactPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(pagerAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);

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
        finish();
    }

    public void addContactButtonPressed(){
        //go to AddContactActivity
        startActivityForResult(new Intent(this, AddContactActivity.class), LandingActivity.ADD_USER);
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
                    String requestId = data.getStringExtra(AddContactActivity.SUCCESSFUL_REQUEST_ID_KEY);
                    Request.getRequestForObjectId(requestId, new Request.OnRequestReturnedListener() {
                        @Override
                        public void receiveRequest(Request request) {
                            //int currentPosition = vpPager.getCurrentItem();
                            vpPager.setCurrentItem(pagerAdapter.SENT);
                            ((SentListFragment) pagerAdapter
                                    .getRegisteredFragment(pagerAdapter.SENT)).addRequestToList(request);
                            //vpPager.setCurrentItem(currentPosition);
                        }
                    });
                    break;

                case AddContactActivity.FAILED_REQUEST:
                    // TODO need to do anything here?
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
    public void onReceivedRequestClick(Request request) {
        ReceivedRequestInteractionFragment receivedRequestInteractionFragment = ReceivedRequestInteractionFragment.newInstance(request);
        receivedRequestInteractionFragment.show(getSupportFragmentManager(), "fragment_request");
    }

    @Override
    public void onSentRequestClick(Request request) {
        SentRequestInteractionFragment requestInteractionFragment = SentRequestInteractionFragment.newInstance(request);
        requestInteractionFragment.show(getSupportFragmentManager(), "fragment_sent_request");
    }


    @Override
    public void updateInbox(){
        ((InboxListFragment) pagerAdapter.getRegisteredFragment(pagerAdapter.INBOX)).refreshList();
    }

    @Override
    public void updateSent(){
        ((SentListFragment) pagerAdapter.getRegisteredFragment(pagerAdapter.SENT)).refreshList();
    }

    @Override
    public void receiveAuthToken(String token) {
        ((OnAuthTokenResolvedListener) pagerAdapter.getRegisteredFragment(pagerAdapter.CONTACTS)).receiveAuthToken(token);
    }

    @Override
    public void handleAuthTokenException(Exception e) {
        ((OnAuthTokenResolvedListener) pagerAdapter.getRegisteredFragment(pagerAdapter.CONTACTS)).handleAuthTokenException(e);
    }

    @Override
    public void onContactClicked(String objectId) {
        createProfileButtonPressed(objectId);
    }

    public class ContactPagerAdapter extends SmartFragmentStatePagerAdapter {
        final int CONTACTS = 0;
        final int INBOX = 1;
        final int SENT = 2;
        private final String[] tabTitles = {"Contacts", "Inbox", "Sent"};

        public ContactPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return new ContactsListFragment();
            } else if (position == 1) {
                return new InboxListFragment();
            } else if(position == 2){
                return new SentListFragment();
            }
            Log.e(TAG, "frag index not found");
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
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
