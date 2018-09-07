package com.kassaiweb.ibiza.News;

import com.google.gson.Gson;

public class News {

    private String cover;
    private String title;
    private String header;
    private String body;

    private transient Gson gson = new Gson();

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
