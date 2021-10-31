package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class ParkedHistory extends ParkedVehicle implements Parcelable {
    private Timestamp exitTime;
    private double amountPaid;

    public ParkedHistory() {
    }

    public ParkedHistory(String id, String vehicleNumber, Timestamp entryTime, Timestamp exitTime, double amountPaid) {
        super(id, vehicleNumber, entryTime);
        this.exitTime = exitTime;
        this.amountPaid = amountPaid;
    }

    public Timestamp getExitTime() {
        return exitTime;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public static final Creator<ParkedHistory> CREATOR = new Creator<ParkedHistory>() {
        @Override
        public ParkedHistory createFromParcel(Parcel in) {
            return new ParkedHistory(in);
        }

        @Override
        public ParkedHistory[] newArray(int size) {
            return new ParkedHistory[size];
        }
    };

    protected ParkedHistory(Parcel in) {
        super(in);
        exitTime = in.readParcelable(Timestamp.class.getClassLoader());
        amountPaid = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeParcelable(exitTime, i);
        parcel.writeDouble(amountPaid);
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
        return "ParkedHistory{" + super.toString() + ", exitTime=" + exitTime + ", amountPaid=" + amountPaid + '}';
    }
}