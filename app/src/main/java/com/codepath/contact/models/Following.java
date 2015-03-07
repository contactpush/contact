package com.codepath.contact.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * This class models the users that the current user
 * is receiving updated contact info for.
 */
@ParseClassName("Following")
public class Following extends ParseObject {
    /**
     * @return This user's ID.
     */
    public String getUserId() {
        return getString("userId");
    }

    /**
     * @return The user ID this user is receiving updated contact info from.
     */
    public String getFollowingId() {
        return getString("followingId");
    }

    /**
     * @param userId This user's ID.
     */
    public void setUserId(String userId) {
        put("userId", userId);
    }

    /**
     * @param followingId The user ID this user is receiving updated contact info from.
     */
    public void setFollowingId(String followingId) {
        put("followingId", followingId);
    }
}
