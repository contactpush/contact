package com.codepath.contact.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Models the contact info we have for this user in parse.
 */
@ParseClassName("ContactInfo")
public class ContactInfo extends ParseObject {
    public static final String CONTACT_INFO_TABLE_NAME = "contact_info";

    public String getUserId() {
        return getString("userId");
    }

    // TODO refactor to concat first, middle, last
    public String getName() {
        return getString("name");
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

    public String getProfileImage(){
        return getString("profileImage");
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

    public void setProfileImage(String profileImage) {
        put("profileImage", profileImage);
    }

    public void setCompany(String company){
        put("company", company);
    }
}
