package com.codepath.contact.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.contact.R;
import com.codepath.contact.fragments.CreateProfileFragment;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String objectId = getIntent().getExtras().getString(CreateProfileFragment.OBJECT_ID);

        CreateProfileFragment createProfileFragment = CreateProfileFragment.newInstance(objectId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flCreateProfile, createProfileFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create_profile) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
