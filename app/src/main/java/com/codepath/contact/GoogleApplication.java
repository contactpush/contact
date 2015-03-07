package com.codepath.contact;

import android.content.Context;

public class GoogleApplication extends com.activeandroid.app.Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleApplication.context = this;
    }
}