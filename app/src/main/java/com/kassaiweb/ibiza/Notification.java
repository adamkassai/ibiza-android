package com.kassaiweb.ibiza;


import java.util.ArrayList;
import java.util.Date;

public class Notification {

    private String title;
    private String body;
    private String creatorId;
    private Date date;


    public Notification() {
    }

    public Notification(String title, String body, String creatorId) {
        this.title = title;
        this.body = body;
        this.creatorId = creatorId;
        this.date = new Date();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
