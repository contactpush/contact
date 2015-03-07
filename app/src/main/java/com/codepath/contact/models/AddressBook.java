package com.codepath.contact.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddressBook {
    private String id;
    private String name;
    private List<AddressBookEntry> addressBook;

    private AddressBook(){
    }

    public static AddressBook getAddressBook(JSONObject json){
        AddressBook a = new AddressBook();
        try {
            JSONObject feed = json.getJSONObject("feed");
            a.id = feed.getJSONObject("id").getString("$t");
            a.name = feed.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t");
            a.addressBook = AddressBookEntry.getAddressBookEntries(feed.getJSONArray("entry"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return a;
    }

    @Override
    public String toString() {
        return String.format("{id: %s, name: %s, addressBook: %s}", id, name, addressBook);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<AddressBookEntry> getAddressBook() {
        return addressBook;
    }
}
