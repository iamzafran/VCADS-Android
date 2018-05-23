package com.vcads.vcads.Model;


import com.firebase.geofire.GeoLocation;

import java.io.Serializable;

/**
 * Created by Zaly on 3/14/2018.
 */

public class Alert implements Serializable {
    private String type;
    private String mAlertId;
    private double mLatitude;
    private double mLongitude;
    private float mDistance;
    public Alert(String type, GeoLocation location, float distance, String alertId) {
        this.type = type;
        mLatitude = location.latitude;
        mLongitude = location.longitude;
        mDistance = distance;
        mAlertId = alertId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }



    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getAlertId() {
        return mAlertId;
    }

    public void setAlertId(String alertId) {
        mAlertId = alertId;
    }
}
