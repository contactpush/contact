package com.codepath.contact.models;

import android.util.Log;
import android.widget.Toast;

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
@ParseClassName("UserData")
public class UserData extends ParseObject {
    private static final String TAG = "UserData";
    public static final String USER_DATA_TABLE_NAME = "UserData";

    // Ensure that your subclass has a public default constructor
    public UserData() {
        super();
    }

    // TODO(emily) fill in
    public UserData(ParseUser user, String dataType, String dataTag, String dataString) {
        super();
        setUser(user);
        setDataType(dataType);
        setDataTag(dataTag);
        setDataString(dataString);
    }

    public UserData(ParseUser user, String dataType, String dataTag, ParseFile dataFile) {
        super();
        setUser(user);
        setDataType(dataType);
        setDataTag(dataTag);
        setDataFile(dataFile);
    }

    /*
     * Types to support:
     *  user has multiple: company, address, email, phone, social
     *  user has single: first_name, last_name, middle_name, birthday
     */
    public void setDataType(String dataType) {
        put("dataType", dataType);
    }

    public String getDataType() {
        return getString("dataType");
    }

    /*
     * Tags to support:
     *  "home" as in home phone, email, address
     *  "work" as in work phone, email, address
     *  "facebook" as in facebook social profile
     *  "twitter" as in twitter social profile
     *  "instagram" as in instagram social profile
     */
    public void setDataTag(String dataTag) {
        put("dataTag", dataTag);
    }

    public String getDataTag() {
        return getString("dataTag");
    }

    public void setDataString(String dataString) {
        put("dataString", dataString);
    }

    public String getDataString() {
        return getString("dataString");
    }

    public void setDataFile(ParseFile dataFile) {
        put("dataFile", dataFile);
    }

    public ParseFile getDataFile() {
        return getParseFile("dataFile");
    }

    // Get the user for this item
    public ParseUser getUser()  {
        return getParseUser("user");
    }

    // Associate each item with a user
    public void setUser(ParseUser user) {
        put("user", user);
    }

}
