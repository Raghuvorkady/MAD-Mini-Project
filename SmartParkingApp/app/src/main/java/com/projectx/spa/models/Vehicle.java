package com.projectx.spa.models;

import com.projectx.spa.interfaces.Settable;

public class Vehicle implements Settable {
    private String id;
    private String vehicleNumber;

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}