package com.kassaiweb.ibiza.Data;

import com.google.firebase.database.Exclude;

public class CheckableGroupMember extends GroupMember {

    private Boolean isChecked;
    private String name;
    private String imageUrl;

    @Exclude
    public Boolean isChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    @Exclude
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
