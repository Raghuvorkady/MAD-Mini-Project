package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.projectx.spa.interfaces.Settable;

public class History implements Parcelable, Settable {
    private String id;
    private String vehicleNumber;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private String amount;

    public History() {
    }

    public History(String id, Vehicles vehicles, String amount) {
        this.id = id;
        this.vehicleNumber = vehicles.getVehicleNumber();
        this.entryTime = vehicles.getEntryTime();
        this.exitTime = vehicles.getExitTime();
        this.amount = amount;
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

    public Timestamp getEntryTime() {
        return entryTime;
    }

    public Timestamp getExitTime() {
        return exitTime;
    }

    public String getAmount() {
        return amount;
    }

    public static Creator<History> getCREATOR() {
        return CREATOR;
    }

    protected History(Parcel in) {
        id = in.readString();
        vehicleNumber = in.readString();
        entryTime = in.readParcelable(Timestamp.class.getClassLoader());
        exitTime = in.readParcelable(Timestamp.class.getClassLoader());
        amount = in.readString();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(vehicleNumber);
        parcel.writeParcelable(entryTime, i);
        parcel.writeParcelable(exitTime, i);
        parcel.writeString(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        History history = (History) o;
        return id.equals(history.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}