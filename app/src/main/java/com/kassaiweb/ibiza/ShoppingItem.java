package com.kassaiweb.ibiza;

import java.util.Date;

public class ShoppingItem {


    private String id;
    private String item;
    private String userId;
    private Date date;
    private boolean ready = false;

    public ShoppingItem() {
    }

    public ShoppingItem(String id, String item, String userId) {
        this.id = id;
        this.item = item;
        this.userId = userId;
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
