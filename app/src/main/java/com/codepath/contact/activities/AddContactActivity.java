package com.codepath.contact.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.codepath.contact.R;
import com.codepath.contact.fragments.SearchUsernameFragment;
import com.codepath.contact.fragments.SearchUsernameFragment.SearchUsernameFragmentListener;
import com.codepath.contact.models.Request;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddContactActivity extends ActionBarActivity implements SearchUsernameFragmentListener, ZXingScannerView.ResultHandler {
    private static final String TAG = "ActionBarActivity";
    public static final int SUCCESSFUL_REQUEST = 524;
    public static final String SUCCESSFUL_REQUEST_ID_KEY = "requestId";
    public static final int FAILED_REQUEST = 234;

    private SearchUsernameFragment searchUsernameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setTheme(R.style.Theme_Contact);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // place username search fragment in activity first
        searchUsernameFragment = SearchUsernameFragment.newInstance(this);
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
    public void searchSuccess(Request request){
        Intent i = new Intent();
        i.putExtra(AddContactActivity.SUCCESSFUL_REQUEST_ID_KEY, request.getObjectId());
        setResult(AddContactActivity.SUCCESSFUL_REQUEST, i);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void searchFailure(){
        setResult(AddContactActivity.FAILED_REQUEST);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
            String contents = result.getText();
            if (contents != null) {
                Log.d(TAG, contents);
                searchUsernameFragment.searchForUsername(contents);
            } else {
                Log.d(TAG, "QR scan failed ZXingScannerView");
            }
        }
    }

    @Override
    public void onBackPressed(){
        searchFailure();
    }
}
