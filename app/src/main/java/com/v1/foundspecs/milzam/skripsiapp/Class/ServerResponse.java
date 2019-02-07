package com.v1.foundspecs.milzam.skripsiapp.Class;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Milzam on 10/28/2017.
 */

public class ServerResponse {
    private Category[] categories;
    private Feeds[] feeds;
    private Complaint[] complaints;
    private User[] users;

    @SerializedName("success")
    private boolean success;

    @SerializedName("refresh_token")
    public String refresh_token;

    @SerializedName("access_token")
    public String access_token;

    @SerializedName("token_type")
    public String token_type;

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public Category[] getCategories() {
        return categories;
    }

    public Feeds[] getFeeds() {
        return feeds;
    }

    public boolean isSuccess() {
        return success;
    }

    public User[] getUsers() {
        return users;
    }

    public Complaint[] getComplaints() {
        return complaints;
    }
}
