package com.codepath.contact.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * This class models an incoming request for this user's contact info.
 */
@ParseClassName("Request")
public class Request extends ParseObject {
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

}
