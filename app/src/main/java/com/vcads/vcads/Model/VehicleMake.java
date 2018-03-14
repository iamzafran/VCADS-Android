package com.vcads.vcads.Model;

/**
 * Created by Zaly on 3/10/2018.
 */

public class VehicleMake {

    private int mMakeId;
    private String mMake;

    public VehicleMake(int mMakeId) {
        this.mMakeId = mMakeId;
    }

    public VehicleMake(int makeId, String make) {
        mMakeId = makeId;
        mMake = make;
    }

    public int getMakeId() {
        return mMakeId;
    }

    public void setMakeId(int makeId) {
        mMakeId = makeId;
    }

    public String getMake() {
        return mMake;
    }

    public void setMake(String make) {
        mMake = make;
    }
}
