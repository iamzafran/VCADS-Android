package com.vcads.vcads.Model;

/**
 * Created by Zaly on 3/10/2018.
 */

public class VehicleType {
    private int mTypeId;
    private String mType;

    public VehicleType(int mTypeId) {
        this.mTypeId = mTypeId;
    }

    public VehicleType(int typeId, String type) {
        mTypeId = typeId;
        mType = type;
    }
}
