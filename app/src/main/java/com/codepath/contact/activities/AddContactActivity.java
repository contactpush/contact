package com.codepath.contact.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.contact.R;
import com.codepath.contact.fragments.SearchUsernameFragment;
import com.codepath.contact.models.Request;

public class AddContactActivity extends ActionBarActivity implements SearchUsernameFragment.SearchUsernameFragmentListener{

    public static final int SUCCESSFUL_REQUEST = 524;
    public static final String SUCCESSFUL_REQUEST_ID_KEY = "requestId";

    public static final int FAILED_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // place username search fragment in activity first
        SearchUsernameFragment searchUsernameFragment = SearchUsernameFragment.newInstance(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flAddContactActivityFragmentFrame, searchUsernameFragment);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_create_profile){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void searchSuccess(Request request){
        Intent i = new Intent();
        i.putExtra(AddContactActivity.SUCCESSFUL_REQUEST_ID_KEY, request.getObjectId());
        setResult(AddContactActivity.SUCCESSFUL_REQUEST, i);
        finish();
    }

    @Override
    public void searchFailure(){
        setResult(AddContactActivity.FAILED_REQUEST);
        finish();
    }

    @Override
    public void onBackPressed(){
        searchFailure();
    }
}
