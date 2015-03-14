package com.codepath.contact.models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
    public static final String REQUESTS_TABLE_NAME = "Request";

    public interface OnRequestsReturnedListener{
        void receiveRequests(List<Request> requests);
    }

    public interface OnRequestReturnedListener{
        void receiveRequest(Request request);
    }

    public static void getSentRequestsInBackground(String userName, OnRequestsReturnedListener listener){
        getRequestsInBackground("from", userName, listener);
    }

    public static void getRequestsInBackground(String userName, OnRequestsReturnedListener listener){
        getRequestsInBackground("to", userName, listener);
    }

    private static void getRequestsInBackground(String direction,
                                               String userName,
                                               final OnRequestsReturnedListener listener){
        Log.d(TAG, String.format("select * from %s where %s = %s and approved = false",
                REQUESTS_TABLE_NAME, direction, userName));
        final ParseQuery<Request> requestQuery = ParseQuery.getQuery(REQUESTS_TABLE_NAME);
        requestQuery.include("fromUser");
        requestQuery.whereEqualTo(direction, userName); // direction "to" or "from"
        requestQuery.whereEqualTo("approved", false);
        requestQuery.findInBackground(new FindCallback<Request>() {
            public void done(List<Request> requests, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + requests.size() + " requests");
                    listener.receiveRequests(requests);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void getRequestForObjectId(final String objectId, final OnRequestReturnedListener listener){
        Log.d(TAG, String.format("select * from %s where objectId = %s",
                REQUESTS_TABLE_NAME, objectId));
        ParseQuery<Request> requestQuery = ParseQuery.getQuery(REQUESTS_TABLE_NAME);
        requestQuery.include("fromUser");
        requestQuery.getInBackground(objectId, new GetCallback<Request>() {
            @Override
            public void done(Request request, ParseException e) {
                if (e == null && request != null) {
                    listener.receiveRequest(request);
                } else {
                    Log.e(TAG, "Error getting request", e);
                }
            }
        });
    }

    private static void createRequestInBackground(){
        // TODO fix this methods so that it actually creates requests correctly...
        ParseQuery<ContactInfo> request = ParseQuery.getQuery(ContactInfo.CONTACT_INFO_TABLE_NAME);
        request.getInBackground("QQtOPZrCsa", new GetCallback<ContactInfo>() {
            @Override
            public void done(ContactInfo contactInfo, ParseException e) {
                Log.d(TAG, "Taylor, done with requests");
                if(e == null){
                    if(contactInfo != null){
                        ContactInfo c =  contactInfo;
                        Log.d(TAG, "Name: " + c.getName());
                        Request r = new Request();
                        r.setTo(ParseUser.getCurrentUser().getUsername());
                        r.setFromUser(c);
                        r.setApprovedStatus(false);
                        r.saveInBackground();
                    }
                }else{
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * @return This user's ID.
     */
    public String getTo() {
        return getString("to");
    }

    public ContactInfo getFromUser() {
        return (ContactInfo) getParseObject("fromUser");
    }

    public String getFrom(){
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

    public void setFromUser(ContactInfo from) {
        put("fromUser", from);
    }

    public void setFrom(String from){
        put("from", from);
    }

    public void setApprovedStatus(boolean approved){
        put("approved", approved);
    }

    public static void makeRequestForUsername(final String username, final requestAttemptHandler handler){
        if(username.equalsIgnoreCase(ParseUser.getCurrentUser().getUsername())){
            Log.e(TAG, "Can't request yourself.");
            handler.onFailure(null, requestAttemptHandler.RequestFailureReason.SELF);
            return;
        }

        ParseQuery<ParseUser> query = ParseUser.getQuery().whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(final List<ParseUser> resultList, ParseException e) {
                if (e == null && resultList.size() == 1) {
                    Log.d(TAG, "Requested user exists.");

                    final Request request = new Request();
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    request.setFrom(currentUser.getUsername());
                    ContactInfo fromUser = (ContactInfo) currentUser.getParseObject(ContactInfo.CONTACT_INFO_TABLE_NAME);
                    if (fromUser != null){
                        request.setFromUser(fromUser);
                    }
                    request.setTo(username);
                    request.setApprovedStatus(false);

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
