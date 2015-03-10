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
    public String getTo() {
        return getString("to");
    }

    /**
     * @return The user ID requesting access to this user's contact info.
     */
    public String getFrom() {
        return getString("from");
    }

    /**
     * @return If this user has approved the request for his or her contact info.
     */
    public boolean getApprovedStatus(){
        return getBoolean("approved");
    }

    /**
     * @param to This user's ID.
     */
    public void setTo(String to) {
        put("to", to);
    }

    /**
     * @param from The user ID of the person requesting to receive updates
     *                    to this user's contact info.
     */
    public void setFrom(String from) {
        put("from", from);
    }

    public void setApprovedStatus(boolean approved){
        put("approved", approved);
    }

    public String getFromName(){
        return getString("fromName");
    }

    public void setFromName(String name){
        put("fromName", name);
    }

    public static void makeRequestForUsername(final String username, final requestAttemptHandler handler){
        if(username.equalsIgnoreCase(ParseUser.getCurrentUser().getUsername())){
            Log.e(TAG, "Can't request yourself.");
            handler.onFailure(null, requestAttemptHandler.RequestFailureReason.SELF);
            return;
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery().whereMatches("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> resultList, ParseException e) {
                if (e == null && resultList.size() == 1) {
                    Log.d(TAG, "Requested user exists.");

                    final Request request = new Request();
                    request.setFrom(ParseUser.getCurrentUser().getUsername());
                    request.setTo(username);
                    request.setApprovedStatus(false);
                    //request.setRequesterName("requester name");// TODO get name of current user (necessary tho?)

                    request.saveInBackground(new SaveCallback(){
                        @Override
                        public void done(ParseException e){
                            if (e == null) {
                                handler.onSuccess(resultList.get(0), request);
                            } else {
                                Log.d(TAG, "request save failed: " + e.getMessage());
                                handler.onFailure(e, requestAttemptHandler.RequestFailureReason.BAD_SAVE_EXCEPTION);
                            }
                        }
                    });

                } else {
                    if(resultList.size() != 1){
                        Log.e(TAG, (resultList.size() + " username(s) match request"));
                        handler.onFailure(e, resultList.size() > 1 ? requestAttemptHandler.RequestFailureReason.MULTIPLE_MATCHES : requestAttemptHandler.RequestFailureReason.NO_MATCHES);
                    }else{
                        Log.e(TAG, "Error: " + e.getMessage());
                        handler.onFailure(e, requestAttemptHandler.RequestFailureReason.EXCEPTION);
                    }

                    return;
                }
            }
        });
    }

    public interface requestAttemptHandler{
        public void onSuccess(ParseUser requestee, Request request);
        public void onFailure(ParseException e, RequestFailureReason requestFailureReason);

        public enum RequestFailureReason{
            EXCEPTION,
            NO_MATCHES,
            MULTIPLE_MATCHES,
            SELF,
            BAD_SAVE_EXCEPTION,
        }
    }

}
