package com.codepath.contact;

import android.content.Context;

public class GoogleApplication extends com.activeandroid.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleApplication.context = this;

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "uXNe2kO8BuPKGuhLNF4zhMH1VD2YyIVPfjn5h9ZM", "plrTKpseHWkO1eLnn0OzMrV1lnhdv6DRRY8brqlS");


        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}