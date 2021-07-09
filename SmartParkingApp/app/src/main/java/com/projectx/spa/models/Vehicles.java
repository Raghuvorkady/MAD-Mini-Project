package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.projectx.spa.interfaces.Settable;

public class Vehicles implements Parcelable, Settable {
    private String id;
    private String vehicleNumber;
    private Timestamp entryTime;
    private Timestamp exitTime;

    /**
     *  for parked vehicles
     * */
    public Vehicles(@Nullable String id, @NonNull String vehicleNumber, @NonNull Timestamp entryTime) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
    }

    public Vehicles(String id, Timestamp exitTime) {
        this.id = id;
        this.exitTime = exitTime;
    }

    /**
     *  for parked history
     * */
    public Vehicles(String id, String vehicleNumber, Timestamp entryTime, Timestamp exitTime) {
        this.id = id;
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    public Vehicles()
    {

    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
    public String getId() {
        return id;
    }

    public Timestamp getEntryTime() {
        return entryTime;
    }

    public Timestamp getExitTime() {
        return exitTime;
    }




    protected Vehicles(Parcel in) {
        id = in.readString();
        vehicleNumber = in.readString();
        entryTime = in.readParcelable(Timestamp.class.getClassLoader());
        exitTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Vehicles> CREATOR = new Creator<Vehicles>() {
        @Override
        public Vehicles createFromParcel(Parcel in) {
            return new Vehicles(in);
        }

        @Override
        public Vehicles[] newArray(int size) {
            return new Vehicles[size];
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
    }

    @Override
    public void setId(String id) {
        this.id = id;

    }
}