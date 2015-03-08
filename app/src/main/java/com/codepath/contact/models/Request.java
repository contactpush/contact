package com.codepath.contact.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * This class models an incoming request for this user's contact info.
 */
@ParseClassName("Request")
public class Request extends ParseObject {
    private static final String TAG = "REQUEST";

    /**
     * @return This user's ID.
     */
    public String getUserId() {
        return getString("userId");
    }

    /**
     * @return The user ID requesting access to this user's contact info.
     */
    public String getRequesterId() {
        return getString("requesterId");
    }

    /**
     * @return If this user has approved the request for his or her contact info.
     */
    public boolean getApprovedStatus(){
        return getBoolean("approved");
    }

    /**
     * @param userId This user's ID.
     */
    public void setUserId(String userId) {
        put("userId", userId);
    }

    /**
     * @param requesterId The user ID of the person requesting to receive updates
     *                    to this user's contact info.
     */
    public void setRequesterId(String requesterId) {
        put("requesterId", requesterId);
    }

    public void setApprovedStatus(boolean approved){
        put("approved", approved);
    }

    public String getRequesterName(){
        return getString("requesterName");
    }

    public void setRequesterName(String name){
        put("requesterName", name);
    }

    public static void makeRequestForUsername(final String username, final requestAttemptHandler handler){
        if(username.equalsIgnoreCase(ParseUser.getCurrentUser().getUsername())){
            Log.e(TAG, "Can't request yourself.");
            handler.onFailure(null, -1);
            return;
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery().whereMatches("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> resultList, ParseException e) {
                if (e == null && resultList.size() == 1) {
                    Log.d(TAG, "Requested user exists.");

                    final Request request = new Request();
                    request.setRequesterId(ParseUser.getCurrentUser().getUsername());
                    request.setUserId(username);
                    //request.setRequesterName("requester name");// TODO get name of current user (necessary tho?)

                    request.saveInBackground(new SaveCallback(){
                        @Override
                        public void done(ParseException e){
                            handler.onSuccess(resultList.get(0), request);
                        }
                    });

                } else {
                    if(resultList.size() != 1){
                        Log.e(TAG, (resultList.size() + " username(s) match request"));
                    }else{
                        Log.e(TAG, "Error: " + e.getMessage());
                    }

                    handler.onFailure(e, resultList.size());
                    return;
                }
            }
        });
    }

    public interface requestAttemptHandler{
        public void onSuccess(ParseUser requestee, Request request);
        public void onFailure(ParseException e, int numberOfMatches);
    }

}
