package com.codepath.contact.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Models the contact info we have for this user in parse.
 */
@ParseClassName("ContactInfo")
public class ContactInfo extends ParseObject {

    public String getUserId() {
        return getString("userId");
    }

    public String getName() {
        return getString("name");
    }

    public String getEmail() {
        return getString("email");
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setEmail(String email) {
        put("email", email);
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }
}
