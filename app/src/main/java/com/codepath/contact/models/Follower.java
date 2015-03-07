package com.codepath.contact.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * This class models a user who is following the current user.
 */
@ParseClassName("Follower")
public class Follower extends ParseObject {
    /**
     * @return This user's ID.
     */
    public String getUserId() {
        return getString("userId");
    }

    /**
     * @return The user ID following this user's contact info.
     */
    public String getFollowerId() {
        return getString("followerId");
    }

    /**
     * @param userId This user's ID.
     */
    public void setUserId(String userId) {
        put("userId", userId);
    }

    /**
     * @param followerId The user ID following this user's contact info.
     */
    public void setFollowerId(String followerId) {
        put("followerId", followerId);
    }
}
