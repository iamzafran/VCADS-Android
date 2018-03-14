package com.vcads.vcads.Model;

/**
 * Created by Zaly on 3/9/2018.
 */

public class VehicleModel {
    private int mModelId;
    private String mModel;
    private VehicleType mType;
    private VehicleMake mMake;

    public VehicleModel(int mModelId, String mModel, int mTypeId, int mMakeId) {
        this.mModelId = mModelId;
        this.mModel = mModel;
        mType = new VehicleType(mTypeId);
        this.mMake = new VehicleMake(mMakeId);
    }

    public VehicleModel(int model_id, String model) {
        mModelId = model_id;
        mModel = model;

    }

    public VehicleModel(int modelId, String model, VehicleType type, VehicleMake make) {
        mModelId = modelId;
        mModel = model;
        mType = type;
        mMake = make;
    }

    public int getModelId() {
        return mModelId;
    }


    public String getModel() {
        return mModel;
    }

    public VehicleType getType() {
        return mType;
    }

    public void setType(VehicleType mType) {
        this.mType = mType;
    }

    public VehicleMake getMake() {
        return mMake;
    }

    public void setMake(VehicleMake mMake) {
        this.mMake = mMake;
    }
}
