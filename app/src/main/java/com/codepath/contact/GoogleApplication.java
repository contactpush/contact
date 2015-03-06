package com.codepath.contact;

import android.content.Context;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     RestClient client = RestApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class GoogleApplication extends com.activeandroid.app.Application {
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		GoogleApplication.context = this;
	}

	public static GoogleClient getRestClient() {
		return (GoogleClient) GoogleClient.getInstance(GoogleClient.class, GoogleApplication.context);
	}
}