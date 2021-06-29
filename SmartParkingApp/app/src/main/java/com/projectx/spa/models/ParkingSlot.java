package com.projectx.spa.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ParkingSlot implements Serializable {
    private String id, building, address, authorizer;
    private int totalSpace, availableSpace;
    private String createdTime;
    private String authorizerId; // can be used as foreign key if needed

    public ParkingSlot() {
    }

    public ParkingSlot(UUID id, String building, String address, String authorizer, int totalSpace, int availableSpace, Timestamp createdTime, String authorizerId) {
        this.id = id.toString();
        this.building = building;
        this.address = address;
        this.authorizer = authorizer;
        this.totalSpace = totalSpace;
        this.availableSpace = availableSpace;
        this.createdTime = createdTime.toString();
        this.authorizerId = authorizerId;
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

    public String getAuthorizer() {
        return authorizer;
    }

    public int getTotalSpace() {
        return totalSpace;
    }

    public int getAvailableSpace() {
        return availableSpace;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getAuthorizerId() {
        return authorizerId;
    }

    public String getParsedCreatedTime() {
        long seconds = 0;
        int nanoseconds = 0;
        try {
            String[] strings = createdTime.split("=");
            seconds = Long.parseLong(strings[1].split(",")[0]);
            nanoseconds = Integer.parseInt(strings[2].split("\\)")[0]);
            Log.d("Date", "seconds " + seconds + "\nnanoseconds " + nanoseconds);
        } catch (Exception e) {
            Log.d("Date", "Error");
        }
        Timestamp timestamp = new Timestamp(seconds, nanoseconds);
        Date date = timestamp.toDate();
        return date.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id='" + id + '\'' +
                ", building='" + building + '\'' +
                ", address='" + address + '\'' +
                ", authorizer='" + authorizer + '\'' +
                ", totalSpace=" + totalSpace +
                ", availableSpace=" + availableSpace +
                ", createdTime='" + createdTime + '\'' +
                ", authorizerId='" + authorizerId + '\'' +
                '}';
    }
}