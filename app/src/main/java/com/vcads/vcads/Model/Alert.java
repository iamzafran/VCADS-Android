package com.vcads.vcads.Model;


import com.firebase.geofire.GeoLocation;

/**
 * Created by Zaly on 3/14/2018.
 */

public class Alert {
    private String type;
    private GeoLocation mLocation;
    private float mDistance;
    public Alert(String type, GeoLocation location, float distance) {
        this.type = type;
        mLocation = location;
        mDistance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeoLocation getLocation() {
        return mLocation;
    }

    public void setLocation(GeoLocation location) {
        mLocation = location;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        this.mDistance = distance;
    }
}
