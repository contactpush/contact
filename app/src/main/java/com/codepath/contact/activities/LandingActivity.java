package com.codepath.contact.activities;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.contact.R;
import com.codepath.contact.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.contact.fragments.ContactsFragment;

public class LandingActivity extends ActionBarActivity implements ContactsFragment.OnFragmentInteractionListener {
    private static final String TAG = "LandingActivity";
    private ViewPager vpPager;
    private ContactPagerAdapter contactPagerAdapter;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public class ContactPagerAdapter extends SmartFragmentStatePagerAdapter {
        private final String[] tabTitles = {"Contacts", "Requests"};

        public ContactPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return ContactsFragment.newInstance("Contacts"); // frag 1
            } else if (position == 1) {
                return ContactsFragment.newInstance("Requests"); // frag 2
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
