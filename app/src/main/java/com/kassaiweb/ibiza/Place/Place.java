package com.kassaiweb.ibiza.Place;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

public class Place {

    private String firebaseId;
    private String name;
    private String groupId;
    private String creatorId;
    /**
     * In a format of double!
     */
    private String latitude;
    /**
     * In a format of double!
     */
    private String longitude;

    public Place() {
    }

    @Exclude
    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Exclude
    public double getLatitudeD() {
        return Double.parseDouble(this.latitude);
    }

    @Exclude
    public double getLongitudeD() {
        return Double.parseDouble(this.longitude);
    }

    @Exclude
    public LatLng getLatLng() {
        return new LatLng(getLatitudeD(), getLongitudeD());
    }
}
