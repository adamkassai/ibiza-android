package com.kassaiweb.ibiza.Data;

public class FbUser {

    private String id;
    private String token;
    private String imageUrl;
    private String name;

    public FbUser() {
    }

    public FbUser(String id, String token, String imageUrl, String name) {
        this.id = id;
        this.token = token;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
