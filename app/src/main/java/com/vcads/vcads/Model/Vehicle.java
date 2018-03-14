package com.vcads.vcads.Model;

import java.io.Serializable;

/**
 * Created by Zaly on 3/10/2018.
 */

public class Vehicle implements Serializable {
    private int mId;
    private VehicleModel mVehicleModel;
    private String mLicensePlate;
    private String mKey;
    private boolean mIsActive = false;

    public Vehicle(int id, VehicleModel mVehicleModel, String mLicensePlate, String mKey) {
        mId = id;
        this.mVehicleModel = mVehicleModel;
        this.mLicensePlate = mLicensePlate;
        this.mKey = mKey;
    }

    public VehicleModel getVehicleModel() {
        return mVehicleModel;
    }

    public void setVehicleModel(VehicleModel mVehicleModel) {
        this.mVehicleModel = mVehicleModel;
    }

    public String getLicensePlate() {
        return mLicensePlate;
    }

    public void setLicensePlate(String mLicensePlate) {
        this.mLicensePlate = mLicensePlate;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }
}
