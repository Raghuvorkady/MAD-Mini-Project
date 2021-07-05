package com.projectx.spa.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class ParkingSlot implements Parcelable {
    private String id, building, address;
    private int totalSpace, availableSpace;
    private Timestamp lastUpdatedTime;
    private String authorizerDocument; // DocumentReference is not Parcelable

    public ParkingSlot() {
    }

    public ParkingSlot(@Nullable String id, @NonNull String building, @NonNull String address,
                       String totalSpace, String availableSpace,
                       @NonNull Timestamp lastUpdatedTime, @NonNull DocumentReference authorizerDocument) {
        this.id = id == null ? "null" : id;
        this.building = building;
        this.address = address;
        this.totalSpace = Integer.parseInt(totalSpace);
        this.availableSpace = Integer.parseInt(availableSpace);
        this.lastUpdatedTime = lastUpdatedTime;
        this.authorizerDocument = authorizerDocument.getPath();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAuthorizerDocument() {
        return authorizerDocument;
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
        authorizerDocument = in.readString();
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
        parcel.writeString(authorizerDocument);
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id='" + id + '\'' +
                ", building='" + building + '\'' +
                ", address='" + address + '\'' +
                ", totalSpace=" + totalSpace +
                ", availableSpace=" + availableSpace +
                ", lastUpdatedTime='" + lastUpdatedTime.toDate() + '\'' +
                ", authorizerDocument=" + authorizerDocument +
                '}';
    }
}