package com.codepath.contact;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GoogleClient {
	public static final String CONTACTS_URL = "https://www.google.com/m8/feeds/contacts/";
    private static final AsyncHttpClient client = new AsyncHttpClient();

	public static void getFullAddressBook(String token, String email, AsyncHttpResponseHandler handler) {
        client.removeAllHeaders();
        String headerValue = "Bearer " + token;
        client.addHeader("Authorization", headerValue);
        RequestParams params = new RequestParams();
        params.add("alt", "json");
        String url = CONTACTS_URL + email + "/full";
		client.get(url, params, handler);
	}
}