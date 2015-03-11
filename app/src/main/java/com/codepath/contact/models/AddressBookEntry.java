package com.codepath.contact.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressBookEntry {
    private String name;
    private String email;
    private String phone;
    private Request request; // this is here by necessity until requestlistfragment and contactlistfragment are separated...

    public AddressBookEntry(){
    }

    public static List<AddressBookEntry> getAddressBookEntries(JSONArray jsonArray){
        List<AddressBookEntry> entries = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                AddressBookEntry e = new AddressBookEntry();
                e.name = json.getJSONObject("title").getString("$t");
                e.email = json.getJSONArray("gd$email").getJSONObject(0).getString("address");
                e.phone = json.getJSONArray("gd$phoneNumber").getJSONObject(0).getString("$t");
                entries.add(e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return entries;
    }

    @Override
    public String toString() {
        return String.format("{name: %s, phone: %s, email: %s}", name, phone, email);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Request getRequest(){
        return request;
    }

    public void setRequest(Request request){
        this.request = request;
    }
}
