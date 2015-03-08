package com.codepath.contact;

import android.content.Context;
import android.util.Log;

import com.codepath.contact.models.ContactInfo;
import com.codepath.contact.models.Request;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class GoogleApplication extends com.activeandroid.app.Application {
    private static Context context;
    private static final String TAG = "GoogleApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleApplication.context = this;

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // register classes extending ParseObject.
        ParseObject.registerSubclass(Request.class);
        ParseObject.registerSubclass(ContactInfo.class);

        // Add your initialization code here
        Parse.initialize(this, "uXNe2kO8BuPKGuhLNF4zhMH1VD2YyIVPfjn5h9ZM", "plrTKpseHWkO1eLnn0OzMrV1lnhdv6DRRY8brqlS");


        //ParseUser.enableAutomaticUser();
        //ParseUser.getCurrentUser().saveInBackground();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

        Log.d(TAG, "creating user...");
        signIntoParse();

        // register device for push notifications by subscribing to a particular channel
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    public void signIntoParse(){
        ParseUser.logInInBackground("contacttestusr", "contacttestusr!", new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.d(TAG, "Login successful");
                } else {
                    Log.d(TAG, "Login failed: " + e.getMessage());
                }
            }
        });
    }

    public void signUpWithParse(){
        ParseUser user = new ParseUser();
        user.setUsername("contacttestusr");
        user.setPassword("contacttestusr!");
        user.setEmail("contacttestusr@gmail.com");
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "SignUp successful");
                    //Log.d(TAG, "Logged in with: " + ParseUser.getCurrentUser().getObjectId());
                } else {
                    Log.d(TAG, "SignUp failed: " + e.getMessage());
                }
            }
        });
    }
}