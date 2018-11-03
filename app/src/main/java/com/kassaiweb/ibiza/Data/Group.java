package com.kassaiweb.ibiza.Data;

import com.google.firebase.database.Exclude;

public class Group {

    private String name;
    private String createdAt;
    private String creator;
    private String password;
    @Exclude
    private String groupKey;

    public Group() {
    }

    public Group(String name, String createdAt, String creator, String password, String groupKey) {
        this.name = name;
        this.createdAt = createdAt;
        this.creator = creator;
        this.password = password;
        this.groupKey = groupKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }
}
