package com.kassaiweb.ibiza.Data;

public class GroupMember {
    /**
     * 0: facebook
     */
    private int accountType;
    /**
     * firebase id of the user
     */
    private String userId;

    public GroupMember() {
    }

    public GroupMember(int accountType, String userId) {
        this.accountType = accountType;
        this.userId = userId;
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
}
