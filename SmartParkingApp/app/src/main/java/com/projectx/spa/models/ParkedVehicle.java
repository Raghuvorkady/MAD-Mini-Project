package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class ParkedVehicle extends Vehicle implements Parcelable {
    private Timestamp entryTime;

    public ParkedVehicle() {
    }

    public ParkedVehicle(String id, String vehicleNumber, Timestamp entryTime) {
        super(id, vehicleNumber);
        this.entryTime = entryTime;
    }

    public Timestamp getEntryTime() {
        return entryTime;
    }

    public static final Creator<ParkedVehicle> CREATOR = new Creator<ParkedVehicle>() {
        @Override
        public ParkedVehicle createFromParcel(Parcel in) {
            return new ParkedVehicle(in);
        }

        @Override
        public ParkedVehicle[] newArray(int size) {
            return new ParkedVehicle[size];
        }
    };

    protected ParkedVehicle(Parcel in) {
        super(in);
        entryTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(entryTime, i);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "ParkedVehicle{" + super.toString() + ", entryTime=" + entryTime + '}';
    }
}