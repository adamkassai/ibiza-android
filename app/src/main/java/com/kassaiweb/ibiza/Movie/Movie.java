package com.kassaiweb.ibiza.Movie;

import com.google.gson.Gson;

public class Movie {

    private String cover;
    private String title;
    private String genre;
    private String description;
    private String cast;

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
