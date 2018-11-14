package com.kassaiweb.ibiza.Notification;

import com.kassaiweb.ibiza.Util.DateUtil;

import java.util.Calendar;
import java.util.List;

public class Notification {

    private String title;
    private String body;
    private String creatorId;
    private String creationDate;
    /**
     * The id of the groupMembers, the notification will be sent to.
     */
    private List<String> to;

    public Notification() {
    }

    public Notification(String title, String body, String creatorId) {
        this.title = title;
        this.body = body;
        this.creatorId = creatorId;
        this.creationDate = DateUtil.convert(Calendar.getInstance());
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }
}
