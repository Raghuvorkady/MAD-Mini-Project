package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.projectx.spa.interfaces.Settable;

public abstract class Vehicle implements Parcelable, Settable {
    private String id;
    private String vehicleNumber;

    public Vehicle() {
    }

    public Vehicle(String id, String vehicleNumber) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
    }

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

    protected Vehicle(Parcel in) {
        id = in.readString();
        vehicleNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(vehicleNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id.equals(vehicle.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Vehicle{" + "id='" + id + '\'' + ", vehicleNumber='" + vehicleNumber + '\'' + '}';
    }
}