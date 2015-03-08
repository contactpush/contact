package com.codepath.contact.activities;

import android.content.Intent;
import android.net.Uri;
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

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.contact.R;
import com.codepath.contact.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.contact.fragments.ContactsListFragment;
import com.codepath.contact.fragments.RequestsListFragment;
import com.codepath.contact.tasks.GetAuthTokenTask.OnAuthTokenResolvedListener;

public class LandingActivity extends ActionBarActivity implements ContactsListFragment.OnFragmentInteractionListener,
        OnAuthTokenResolvedListener{
    private static final String TAG = "LandingActivity";
    private ViewPager vpPager;
    private ContactPagerAdapter contactPagerAdapter;
    private ProgressBar pb;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = (ProgressBar) findViewById(R.id.pbLoading);
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        contactPagerAdapter = new ContactPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(contactPagerAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(vpPager);
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addContactButtonPressed(){
        //go to AddContactActivity
        startActivity(new Intent(this, AddContactActivity.class));
    }

    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO implement method
    }

    @Override
    public void receiveAuthToken(String token) {
        ((OnAuthTokenResolvedListener) contactPagerAdapter.getRegisteredFragment(0)).receiveAuthToken(token);
    }

    @Override
    public void handleAuthTokenException(Exception e) {
        ((OnAuthTokenResolvedListener) contactPagerAdapter.getRegisteredFragment(0)).handleAuthTokenException(e);
    }

    public class ContactPagerAdapter extends SmartFragmentStatePagerAdapter {
        private final String[] tabTitles = {"Contacts", "Requests"};

        public ContactPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return ContactsListFragment.newInstance(); // frag 1
            } else if (position == 1) {
                return RequestsListFragment.newInstance(); // frag 2
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
}
