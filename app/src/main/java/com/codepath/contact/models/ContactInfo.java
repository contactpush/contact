package com.codepath.contact.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Models the contact info we have for this user in parse.
 */
@ParseClassName("ContactInfo")
public class ContactInfo extends ParseObject {
    private static final String TAG = "ContactInfo";
    public static final String CONTACT_INFO_TABLE_NAME = "ContactInfo";

    public interface OnContactsReturnedListener{
        void receiveContacts(List<ContactInfo> contactInfos);
    }

    public interface OnContactReturnedListener{
        void receiveContact(ContactInfo contactInfo);
    }

    public static void getContacts(final OnContactsReturnedListener listener){
        ParseRelation<ContactInfo> contactsQuery = ParseUser.getCurrentUser().getRelation("contacts");
        contactsQuery.getQuery().findInBackground(new FindCallback<ContactInfo>() {
            @Override
            public void done(List<ContactInfo> contactInfos, ParseException e) {
                if (e == null){
                    Log.d(TAG, "found " + contactInfos.size() + " contacts");
                    listener.receiveContacts(contactInfos);
                } else {
                    Log.e(TAG, e.getMessage());
                    listener.receiveContacts(new ArrayList<ContactInfo>());
                }
            }
        });
    }

    public static void getContactInfo(String objectId, final OnContactReturnedListener listener){
        ParseQuery<ContactInfo> contactQuery = new ParseQuery<>(CONTACT_INFO_TABLE_NAME);
        contactQuery.getInBackground(objectId, new GetCallback<ContactInfo>() {
            @Override
            public void done(ContactInfo contactInfo, ParseException e) {
                if (e == null){
                    listener.receiveContact(contactInfo);
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }


    public String getUserId() {
        return getString("userId");
    }

    public String getName() {
        return getFirstName() + " " + getLastName();
    }

    public String getFirstName() {
        return getString("firstName");
    }

    public String getMiddleName() {
        return getString("middleName");
    }

    public String getLastName() {
        return getString("lastName");
    }

    public String getAddressType() {
        return getString("addressType");
    }

    public String getAddress() {
        return getString("address");
    }

    public String getSocialProfileType() {
        return getString("socialProfileType");
    }

    public String getSocialProfile() {
        return getString("socialProfile");
    }

    public String getEmailType() {
        return getString("emailType");
    }

    public String getEmail() {
        return getString("email");
    }

    public String getPhoneType() {
        return getString("phoneType");
    }

    public String getPhone() {
        return getString("phone");
    }

    public byte[] getProfileImage(){
        ParseFile file = getParseFile("profileImage");
        if (file == null){
            return null;
        }
        byte[] photo = null;
        try {
            photo = file.getData();
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return photo;
    }

    public String getCompany(){
        return getString("company");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setEmailType(String emailType) {
        put("emailType", emailType);
    }

    public void setEmail(String email) {
        put("email", email);
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public void setPhoneType(String phoneType) {
        put("phoneType", phoneType);
    }

    public void setFirstName(String firstName) {
        put("firstName", firstName);
    }

    public void setMiddleName(String middleName) {
        put("middleName", middleName);
    }

    public void setLastName(String lastName) {
        put("lastName", lastName);
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public void setAddressType(String addressType) {
        put("addressType", addressType);
    }

    public void setSocialProfile(String socialProfile) {
        put("socialProfile", socialProfile);
    }

    public void setSocialProfileType(String socialProfileType) {
        put("socialProfileType", socialProfileType);
    }

    public void setProfileImage(byte[] profileImage) {
        if (profileImage == null || profileImage.length == 0){
            return;
        }
        ParseFile file = new ParseFile("profileImage.png", profileImage);
        put("profileImage", file);
    }

    public void setCompany(String company){
        put("company", company);
    }
}
