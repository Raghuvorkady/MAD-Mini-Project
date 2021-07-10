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

    public History(String id, Vehicles vehicles, int amt) {
        this.id = id;
        this.vehicleNumber = vehicles.getVehicleNumber();
        this.entryTime = vehicles.getEntryTime();
        this.exitTime = vehicles.getExitTime();
        this.amount = amount;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(vehicleNumber);
        dest.writeParcelable(entryTime, flags);
        dest.writeParcelable(exitTime, flags);
        dest.writeString(amount);
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}