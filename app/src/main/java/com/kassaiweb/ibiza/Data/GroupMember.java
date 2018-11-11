package com.kassaiweb.ibiza.Data;

import com.google.firebase.database.Exclude;

public class GroupMember {
    /**
     * 0: facebook
     */
    private int accountType;
    /**
     * firebase id of the user
     */
    private String userId;
    private String firebaseId;

    public GroupMember() {
    }

    public GroupMember(int accountType, String userId, String firebaseId) {
        this.accountType = accountType;
        this.userId = userId;
        this.firebaseId = firebaseId;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }
}
