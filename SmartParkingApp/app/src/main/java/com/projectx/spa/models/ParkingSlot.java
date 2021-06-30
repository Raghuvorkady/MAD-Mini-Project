package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class ParkingSlot implements Parcelable {
    private String id, building, address;
    private int totalSpace, availableSpace;
    private Timestamp lastUpdatedTime;
    //    private String reference; // if DocumentReference is not feasible

    public ParkingSlot() {
    }

    public ParkingSlot(UUID id, String building, String address, int totalSpace, int availableSpace, Timestamp lastUpdatedTime) {
        this.id = id.toString();
        this.building = building;
        this.address = address;
        this.totalSpace = totalSpace;
        this.availableSpace = availableSpace;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getId() {
        return id;
    }

    public String getBuilding() {
        return building;
    }

    public String getAddress() {
        return address;
    }

    public int getTotalSpace() {
        return totalSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }

    public Timestamp getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    // Parcelable implementations
    public static final Creator<ParkingSlot> CREATOR = new Creator<ParkingSlot>() {
        @Override
        public ParkingSlot createFromParcel(Parcel in) {
            return new ParkingSlot(in);
        }

        @Override
        public ParkingSlot[] newArray(int size) {
            return new ParkingSlot[size];
        }
    };

    protected ParkingSlot(Parcel in) {
        id = in.readString();
        building = in.readString();
        address = in.readString();
        totalSpace = in.readInt();
        availableSpace = in.readInt();
        lastUpdatedTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(building);
        parcel.writeString(address);
        parcel.writeInt(totalSpace);
        parcel.writeInt(availableSpace);
        parcel.writeParcelable(lastUpdatedTime, i);
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id='" + id + '\'' +
                ", location='" + building + '\'' +
                ", landmark='" + address + '\'' +
                ", totalSpace=" + totalSpace +
                ", availableSpace=" + availableSpace +
                ", createdTime='" + lastUpdatedTime.toDate() + '\'' +
                '}';
    }
}