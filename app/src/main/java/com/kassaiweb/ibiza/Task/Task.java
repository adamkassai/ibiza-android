package com.kassaiweb.ibiza.Task;


import java.util.ArrayList;
import java.util.Date;

public class Task {

    private String id;
    private String description;
    private String creatorId;
    private Date date;
    private int volunteerNumber;
    private ArrayList<String> volunteers = new ArrayList<>();
    private boolean ready = false;
    private Date deadline;
    private String type;

    public Task() {
    }

    public Task(String id, String description, String creatorId, int volunteerNumber) {
        this.id = id;
        this.description = description;
        this.creatorId = creatorId;
        this.volunteerNumber = volunteerNumber;
        this.date = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getVolunteerNumber() {
        return volunteerNumber;
    }

    public void setVolunteerNumber(int volunteerNumber) {
        this.volunteerNumber = volunteerNumber;
    }

    public ArrayList<String> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(ArrayList<String> volunteers) {
        this.volunteers = volunteers;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
