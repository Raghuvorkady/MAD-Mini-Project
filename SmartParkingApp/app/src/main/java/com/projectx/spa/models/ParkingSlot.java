package com.projectx.spa.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ParkingSlot implements Serializable {
    private String location, landmark, id;
    private int totalSpace, availableSpace;
    private String createdTime;

    public ParkingSlot() {
    }

    public ParkingSlot(String location, String landmark, String id, int totalSpace, int availableSpace, Timestamp createdTime) {
        this.location = location;
        this.landmark = landmark;
        this.id = id;
        this.totalSpace = totalSpace;
        this.availableSpace = availableSpace;
        this.createdTime = createdTime.toString();
    }

    public String getLocation() {
        return location;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getId() {
        return id;
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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "location='" + location + '\'' +
                ", landmark='" + landmark + '\'' +
                ", id='" + id + '\'' +
                ", totalSpace=" + totalSpace +
                ", availableSpace=" + availableSpace +
                ", createdTime=" + createdTime +
                '}';
    }
}