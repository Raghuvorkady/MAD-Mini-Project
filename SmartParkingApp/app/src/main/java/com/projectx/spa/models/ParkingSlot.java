package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class ParkingSlot implements Parcelable {
    private String id, location, landmark;
    private int totalSpace, availableSpace;
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

    public ParkingSlot() {
    }
    private Timestamp createdTime;

    public ParkingSlot(UUID id, String location, String landmark, int totalSpace, int availableSpace, Timestamp createdTime) {
        this.id = id.toString();
        this.location = location;
        this.landmark = landmark;
        this.totalSpace = totalSpace;
        this.availableSpace = availableSpace;
        this.createdTime = createdTime;
    }

    public String getLocation() {
        return location;
    }

    public String getLandmark() {
        return landmark;
    }

    public int getTotalSpace() {
        return totalSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }

    // Parcelable implementations
    protected ParkingSlot(Parcel in) {
        id = in.readString();
        location = in.readString();
        landmark = in.readString();
        totalSpace = in.readInt();
        availableSpace = in.readInt();
        createdTime = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(location);
        parcel.writeString(landmark);
        parcel.writeInt(totalSpace);
        parcel.writeInt(availableSpace);
        parcel.writeParcelable(createdTime, i);
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id='" + id + '\'' +
                ", location='" + location + '\'' +
                ", landmark='" + landmark + '\'' +
                ", totalSpace=" + totalSpace +
                ", availableSpace=" + availableSpace +
                ", createdTime='" + createdTime.toDate() + '\'' +
                '}';
    }
}